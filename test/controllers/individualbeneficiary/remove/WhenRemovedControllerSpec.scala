/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.individualbeneficiary.remove

import base.SpecBase
import connectors.TrustConnector
import forms.DateRemovedFromTrustFormProvider
import models.Name
import models.beneficiaries.{Beneficiaries, IndividualBeneficiary}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{TrustService, TrustServiceImpl}
import uk.gov.hmrc.http.HttpResponse
import views.html.individualbeneficiary.remove.WhenRemovedView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class WhenRemovedControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new DateRemovedFromTrustFormProvider()

  private def form = formProvider.withPrefixAndEntityStartDate("individualBeneficiary.whenRemoved", LocalDate.now())

  val validAnswer: LocalDate = LocalDate.now(ZoneOffset.UTC)

  val index = 0

  val name: Name = Name("First", None, "Last")
  val mockConnector: TrustConnector = mock[TrustConnector]

  val fakeService = new TrustServiceImpl(mockConnector)

  lazy val dateRemovedFromTrustRoute: String = routes.WhenRemovedController.onPageLoad(index).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, dateRemovedFromTrustRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, dateRemovedFromTrustRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  def individualBeneficiary(id: Int): IndividualBeneficiary = IndividualBeneficiary(
    name = name,
    dateOfBirth = None,
    identification = None,
    address = None,
    vulnerableYesNo = Some(false),
    roleInCompany = None,
    income = None,
    incomeDiscretionYesNo = Some(false),
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = false
  )

  val beneficiaries: List[IndividualBeneficiary] = List(individualBeneficiary(1), individualBeneficiary(2), individualBeneficiary(3))

  "WhenRemoved Controller" must {

    "return OK and the correct view for a GET" in {

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(Beneficiaries(beneficiaries, Nil, Nil, Nil, Nil, Nil, Nil)))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[WhenRemovedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, index, name.displayName)(getRequest(), messages).toString

      application.stop()
    }


    "redirect to the next page when valid data is submitted" in {

      when(mockConnector.removeBeneficiary(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(200, "")))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[TrustService].toInstance(fakeService)
          )
          .build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.AddABeneficiaryController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val request =
        FakeRequest(POST, dateRemovedFromTrustRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[WhenRemovedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, index, name.displayName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to the Add Beneficiaries page when we get an IndexOutOfBoundsException" in {

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.failed(new IndexOutOfBoundsException("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val result = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.AddABeneficiaryController.onPageLoad().url

      application.stop()
    }
  }
}
