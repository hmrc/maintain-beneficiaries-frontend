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
import connectors.TrustStoreConnector
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import models.beneficiaries._
import models.{AddABeneficiary, Name}
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import viewmodels.addAnother.AddRow
import views.html.{AddABeneficiaryView, AddABeneficiaryYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddABeneficiaryControllerSpec extends SpecBase {

  lazy val getRoute : String = controllers.routes.AddABeneficiaryController.onPageLoad().url
  lazy val submitAnotherRoute : String = controllers.routes.AddABeneficiaryController.submitAnother().url
  lazy val submitYesNoRoute : String = controllers.routes.AddABeneficiaryController.submitOne().url

  val mockStoreConnector : TrustStoreConnector = mock[TrustStoreConnector]

  val addTrusteeForm = new AddABeneficiaryFormProvider()()
  val yesNoForm = new YesNoFormProvider().withPrefix("addABeneficiaryYesNo")

  private val beneficiary = IndividualBeneficiary(
    name = Name(firstName = "First", middleName = None, lastName = "Last"),
    dateOfBirth = Some(LocalDate.parse("1983-09-24")),
    nationalInsuranceNumber = Some("JS123456A"),
    address = None,
    entityStart = LocalDate.parse("2019-02-28"),
    vulnerableYesNo = false,
    income = None,
    incomeDiscretionYesNo = false
  )

  private val trustBeneficiary = TrustBeneficiary(
    name = "Trust Beneficiary Name",
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.of(2017, 2, 28))

  private val charityBeneficiary = CharityBeneficiary(
    name = "Humanitarian Endeavours Ltd",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2012-03-14")
  )

  private val companyBeneficiary = CompanyBeneficiary(
    name = "Humanitarian Company Ltd",
    utr = Some("0987654321"),
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2012-03-14")
  )

  private val unidentifiedBeneficiary = ClassOfBeneficiary(
    description = "Unidentified Beneficiary",
    entityStart = LocalDate.parse("2019-02-28")
  )

  private val otherBeneficiary = OtherBeneficiary(
    description = "Other Endeavours Ltd",
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2019-02-28")
  )

  val beneficiaries = Beneficiaries(
    List(beneficiary),
    List(unidentifiedBeneficiary),
    List(companyBeneficiary),
    List(trustBeneficiary),
    List(charityBeneficiary),
    List(otherBeneficiary)
  )

  val beneficiaryRows = List(
    AddRow("First Last", typeLabel = "Named individual", "Change details", None, "Remove", None),
    AddRow("Unidentified Beneficiary", typeLabel = "Class of beneficiaries", "Change details", Some(controllers.classofbeneficiary.amend.routes.DescriptionController.onPageLoad(0).url), "Remove", None),
    AddRow("Humanitarian Company Ltd", typeLabel = "Named company", "Change details", None, "Remove", None),
    AddRow("Trust Beneficiary Name", typeLabel = "Named trust", "Change details", None, "Remove", None),
    AddRow("Humanitarian Endeavours Ltd", typeLabel = "Named charity", "Change details", None, "Remove", None),
    AddRow("Other Endeavours Ltd", typeLabel = "Other beneficiary", "Change details", None, "Remove", None)
  )

  class FakeService(data: Beneficiaries) extends TrustService {

    override def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries] = Future.successful(data)

    override def getUnidentifiedBeneficiary(utr: String, index: Int)
                                           (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[ClassOfBeneficiary] =
      Future.successful(unidentifiedBeneficiary)

  }

  " AddABeneficiary Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val fakeService = new FakeService(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil))

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

        val fakeService = new FakeService(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil))

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

        val fakeService = new FakeService(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(Seq(
          bind(classOf[TrustService]).toInstance(fakeService)
        )).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddABeneficiaryController.onPageLoad().url

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

        contentAsString(result) mustEqual view(addTrusteeForm, Nil, beneficiaryRows, "The trust has 6 beneficiaries")(fakeRequest, messages).toString

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

        contentAsString(result) mustEqual view(boundForm, Nil, beneficiaryRows, "The trust has 6 beneficiaries")(fakeRequest, messages).toString

        application.stop()
      }

    }
  }
}
