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

package controllers.other.remove

import base.SpecBase
import connectors.TrustConnector
import forms.RemoveIndexFormProvider
import models.beneficiaries.{Beneficiaries, OtherBeneficiary}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.other.remove.RemoveYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import views.html.other.remove.RemoveIndexView
import java.time.LocalDate
import scala.concurrent.Future

class RemoveOtherBeneficiaryControllerSpec extends SpecBase with ScalaCheckPropertyChecks with ScalaFutures {

  val messagesPrefix = "removeOtherBeneficiaryYesNo"

  lazy val formProvider = new RemoveIndexFormProvider()
  lazy val form: Form[Boolean] = formProvider(messagesPrefix)

  lazy val description : String = "Some Description 1"

  val mockConnector: TrustConnector = mock[TrustConnector]

  def otherBeneficiary(id: Int, provisional : Boolean): OtherBeneficiary = OtherBeneficiary(
    description = s"Some Description $id",
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = provisional
  )

  val expectedResult: OtherBeneficiary = otherBeneficiary(2, provisional = true)

  val beneficiaries: List[OtherBeneficiary] = List(
    otherBeneficiary(1, provisional = false),
    expectedResult,
    otherBeneficiary(3, provisional = true)
  )

  "RemoveOtherBeneficiary Controller" when {

    "return OK and the correct view for a GET" in {

      val index = 0

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, beneficiaries)))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TrustConnector].toInstance(mockConnector))
        .build()

      val request = FakeRequest(GET, routes.RemoveOtherBeneficiaryController.onPageLoad(index).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RemoveIndexView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, index, description)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(RemoveYesNoPage, true).success.value

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, beneficiaries)))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val request = FakeRequest(GET, routes.RemoveOtherBeneficiaryController.onPageLoad(0).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RemoveIndexView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), 0, description)(request, messages).toString

      application.stop()
    }

    "redirect to the add beneficiaries page if we get an Index Not Found Exception" in {

      val userAnswers = emptyUserAnswers
        .set(RemoveYesNoPage, true).success.value

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.failed(new IndexOutOfBoundsException("")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val request = FakeRequest(GET, routes.RemoveOtherBeneficiaryController.onPageLoad(0).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.AddABeneficiaryController.onPageLoad().url

      application.stop()
    }

    "not removing the beneficiary" must {

      "redirect to the add to page when valid data is submitted" in {

        val index = 0

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustConnector].toInstance(mockConnector))
          .build()

        val request =
          FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index).url)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddABeneficiaryController.onPageLoad().url

        application.stop()
      }
    }

    "removing an existing beneficiary" must {

      "redirect to the next page when valid data is submitted" in {

        val index = 0

        when(mockConnector.getBeneficiaries(any())(any(), any()))
          .thenReturn(Future.successful(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, beneficiaries)))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustConnector].toInstance(mockConnector))
          .build()

        val request =
          FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.other.remove.routes.WhenRemovedController.onPageLoad(0).url

        application.stop()
      }
    }

    "removing a new beneficiary" must {

      "redirect to the add to page, removing the beneficiary" in {

        val index = 2

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[TrustConnector].toInstance(mockConnector))
          .build()

        when(mockConnector.getBeneficiaries(any())(any(), any()))
          .thenReturn(Future.successful(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, beneficiaries)))

        when(mockConnector.removeBeneficiary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200, "")))

        val request =
          FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.AddABeneficiaryController.onPageLoad().url

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val index = 0

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(bind[TrustConnector].toInstance(mockConnector)).build()

      val request =
        FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index).url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, index, description)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val index = 0

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.RemoveOtherBeneficiaryController.onPageLoad(index).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val index = 0

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index).url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
