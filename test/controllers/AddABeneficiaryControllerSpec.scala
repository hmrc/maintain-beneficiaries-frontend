/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import java.time.LocalDate

import base.SpecBase
import connectors.{TrustConnector, TrustStoreConnector}
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import models.HowManyBeneficiaries.Over201
import models.beneficiaries.{Beneficiaries, ClassOfBeneficiary, IndividualBeneficiary, _}
import models.{AddABeneficiary, Name, NationalInsuranceNumber, RemoveBeneficiary, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.concurrent.ScalaFutures
import pages.AddNowPage
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.AddABeneficiaryViewHelper
import viewmodels.addAnother.AddRow
import views.html.{AddABeneficiaryView, AddABeneficiaryYesNoView, MaxedOutBeneficiariesView}

import scala.concurrent.{ExecutionContext, Future}

class AddABeneficiaryControllerSpec extends SpecBase with ScalaFutures {

  lazy val getRoute : String = controllers.routes.AddABeneficiaryController.onPageLoad().url
  lazy val submitAnotherRoute : String = controllers.routes.AddABeneficiaryController.submitAnother().url
  lazy val submitYesNoRoute : String = controllers.routes.AddABeneficiaryController.submitOne().url
  lazy val submitCompleteRoute : String = controllers.routes.AddABeneficiaryController.submitComplete().url

  val mockStoreConnector : TrustStoreConnector = mock[TrustStoreConnector]

  val addTrusteeForm = new AddABeneficiaryFormProvider()()
  val yesNoForm = new YesNoFormProvider().withPrefix("addABeneficiaryYesNo")

  private val individualBeneficiary = IndividualBeneficiary(
    name = Name(firstName = "First", middleName = None, lastName = "Last"),
    dateOfBirth = Some(LocalDate.parse("1983-09-24")),
    identification = Some(NationalInsuranceNumber("JS123456A")),
    address = None,
    entityStart = LocalDate.parse("2019-02-28"),
    vulnerableYesNo = false,
    income = None,
    incomeDiscretionYesNo = false,
    provisional = false
  )

  private val trustBeneficiary = TrustBeneficiary(
    name = "Trust Beneficiary Name",
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.of(2017, 2, 28),
    provisional = false
  )

  private val charityBeneficiary = CharityBeneficiary(
    name = "Humanitarian Endeavours Ltd",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val companyBeneficiary = CompanyBeneficiary(
    name = "Humanitarian Company Ltd",
    utr = Some("0987654321"),
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val employmentRelatedBeneficiary = EmploymentRelatedBeneficiary(
    name = "Employment Related Endeavours",
    utr = Some("0987654321"),
    address = None,
    description = Seq("Description"),
    howManyBeneficiaries = Over201,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val unidentifiedBeneficiary = ClassOfBeneficiary(
    description = "Unidentified Beneficiary",
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = false
  )

  private val otherBeneficiary = OtherBeneficiary(
    description = "Other Endeavours Ltd",
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = false
  )

  val beneficiaries = Beneficiaries(
    List(individualBeneficiary),
    List(unidentifiedBeneficiary),
    List(companyBeneficiary),
    List(employmentRelatedBeneficiary),
    List(trustBeneficiary),
    List(charityBeneficiary),
    List(otherBeneficiary)
  )

  lazy val featureNotAvailable : String = controllers.routes.FeatureNotAvailableController.onPageLoad().url

  val beneficiaryRows = List(
    AddRow("First Last", typeLabel = "Named individual", "Change details", Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRender(0).url), "Remove", Some(controllers.individualbeneficiary.remove.routes.RemoveIndividualBeneficiaryController.onPageLoad(0).url)),
    AddRow("Unidentified Beneficiary", typeLabel = "Class of beneficiaries", "Change details", Some(controllers.classofbeneficiary.amend.routes.DescriptionController.onPageLoad(0).url), "Remove", Some(controllers.classofbeneficiary.remove.routes.RemoveClassOfBeneficiaryController.onPageLoad(0).url)),
    AddRow("Humanitarian Company Ltd", typeLabel = "Named company", "Change details", Some(featureNotAvailable), "Remove", Some(featureNotAvailable)),
    AddRow("Employment Related Endeavours", typeLabel = "Employment related", "Change details", Some(featureNotAvailable), "Remove", Some(featureNotAvailable)),
    AddRow("Trust Beneficiary Name", typeLabel = "Named trust", "Change details", Some(featureNotAvailable), "Remove", Some(featureNotAvailable)),
    AddRow("Humanitarian Endeavours Ltd", typeLabel = "Named charity", "Change details", Some(controllers.charityortrust.charity.amend.routes.CheckDetailsController.extractAndRender(0).url), "Remove", Some(controllers.charityortrust.charity.remove.routes.RemoveCharityBeneficiaryController.onPageLoad(0).url)),
    AddRow("Other Endeavours Ltd", typeLabel = "Other beneficiary", "Change details", Some(featureNotAvailable), "Remove", Some(controllers.other.remove.routes.RemoveOtherBeneficiaryController.onPageLoad(0).url))
  )

  class FakeService(data: Beneficiaries) extends TrustService {

    override def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries] = Future.successful(data)

    override def getUnidentifiedBeneficiary(utr: String, index: Int)
                                           (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[ClassOfBeneficiary] =
      Future.successful(unidentifiedBeneficiary)

    override def getIndividualBeneficiary(utr: String, index: Int)
                                         (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[IndividualBeneficiary] =
      Future.successful(individualBeneficiary)

    override def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
      Future.successful(HttpResponse(OK))

    override def getCharityBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[CharityBeneficiary] =
      Future.successful(charityBeneficiary)

    override def getOtherBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[OtherBeneficiary] =
      Future.successful(otherBeneficiary)
  }

  " AddABeneficiary Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val fakeService = new FakeService(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil))

        val application = applicationBuilder(userAnswers = None).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "no beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val fakeService = new FakeService(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(yesNoForm)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val fakeService = new FakeService(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddNowController.onPageLoad().url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = yesNoForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddABeneficiaryYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "there are beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(addTrusteeForm, Nil, beneficiaryRows, "The trust has 7 beneficiaries", Nil)(fakeRequest, messages).toString

        application.stop()
      }


      "redirect to the maintain task list when the user says they are done" in {

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService),
          bind(classOf[TrustStoreConnector]).toInstance(mockStoreConnector)
        )).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.NoComplete.toString))

        when(mockStoreConnector.setTaskComplete(any())(any(), any())).thenReturn(Future.successful(HttpResponse.apply(200)))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9788/maintain-a-trust/overview"

        application.stop()
      }

      "redirect to the maintain task list when the user says they want to add later" ignore {

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.YesLater.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9788/maintain-a-trust/overview"

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addTrusteeForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddABeneficiaryView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual view(boundForm, Nil, beneficiaryRows, "The trust has 7 beneficiaries", Nil)(fakeRequest, messages).toString

        application.stop()
      }

      "Clear out the user answers when starting the add class of beneficiary journey and redirect to what type of beneficiary page" in {

        val mockTrustConnector = mock[TrustConnector]

        val userAnswers = emptyUserAnswers
          .set(AddNowPage, TypeOfBeneficiaryToAdd.ClassOfBeneficiaries).success.value
          .set(DescriptionPage, "Description").success.value
          .set(EntityStartPage, LocalDate.parse("2019-02-03")).success.value

        reset(playbackRepository)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers),
            affinityGroup = Agent
          ).overrides(
            bind[TrustConnector].toInstance(mockTrustConnector)
          ).build()

        when(mockTrustConnector.getBeneficiaries(any())(any(), any()))
          .thenReturn(Future.successful(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil)))
        when(playbackRepository.set(any())).thenReturn(Future.successful(true))

        val request = FakeRequest(POST, submitAnotherRoute)
          .withFormUrlEncodedBody(("value", AddABeneficiary.YesNow.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.AddNowController.onPageLoad().url

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(playbackRepository).set(uaCaptor.capture)
        uaCaptor.getValue.data mustBe Json.obj()
      }

    }

    "maxed out beneficiaries" must {

      val beneficiaries = Beneficiaries(
        List.fill(25)(individualBeneficiary),
        List.fill(25)(unidentifiedBeneficiary),
        List.fill(25)(companyBeneficiary),
        List.fill(25)(employmentRelatedBeneficiary),
        List.fill(25)(trustBeneficiary),
        List.fill(25)(charityBeneficiary),
        List.fill(25)(otherBeneficiary)
      )

      val fakeService = new FakeService(beneficiaries)

      val beneficiaryRows = new AddABeneficiaryViewHelper(beneficiaries).rows

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[MaxedOutBeneficiariesView]

        status(result) mustEqual OK

        val content = contentAsString(result)

        content mustEqual view(beneficiaryRows.inProgress, beneficiaryRows.complete, "The trust has 175 beneficiaries")(fakeRequest, messages).toString
        content must include("You cannot enter another beneficiary as you have entered a maximum of 175.")
        content must include("If you have further beneficiaries to add, write to HMRC with their details.")

        application.stop()

      }

      "return correct view when one type of beneficiary is maxed out" in {

        val beneficiaries = Beneficiaries(
          Nil,
          Nil,
          Nil,
          Nil,
          Nil,
          List.fill(25)(charityBeneficiary),
          Nil
        )

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        contentAsString(result) must include("You cannot add another charity as you have entered a maximum of 25.")
        contentAsString(result) must include("If you have further beneficiaries to add within this type, write to HMRC with their details.")

        application.stop()

      }

      "return correct view when more than one type of beneficiary is maxed out" in {

        val beneficiaries = Beneficiaries(
          Nil,
          List.fill(25)(unidentifiedBeneficiary),
          Nil,
          Nil,
          Nil,
          List.fill(25)(charityBeneficiary),
          Nil
        )

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        contentAsString(result) must include("You have entered the maximum number of beneficiaries for:")
        contentAsString(result) must include("If you have further beneficiaries to add within these types, write to HMRC with their details.")

        application.stop()

      }

      "redirect to add to page and set beneficiaries to complete when user clicks continue" in {

        val fakeService = new FakeService(beneficiaries)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService),
          bind(classOf[TrustStoreConnector]).toInstance(mockStoreConnector)
        )).build()

        val request = FakeRequest(POST, submitCompleteRoute)

        when(mockStoreConnector.setTaskComplete(any())(any(), any())).thenReturn(Future.successful(HttpResponse.apply(200)))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9788/maintain-a-trust/overview"

        application.stop()

      }

    }
  }
}
