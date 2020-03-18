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

package controllers.classofbeneficiary

import base.SpecBase
import connectors.TrustConnector
import forms.StandardSingleFieldFormProvider
import models.beneficiaries.{Beneficiaries, ClassOfBeneficiary}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import uk.gov.hmrc.http.HttpResponse
import views.html.classofbeneficiary.DescriptionView

import scala.concurrent.Future

class DescriptionControllerSpec extends SpecBase with MockitoSugar {

  val form: Form[String] = new StandardSingleFieldFormProvider().withPrefix("classOfBeneficiary.description")
  val index = 0
  lazy val descriptionRoute: String = routes.DescriptionController.onPageLoad(index).url
  val description = "Description"

  val mockTrustConnector: TrustConnector = mock[TrustConnector]
  when(mockTrustConnector.getBeneficiaries(any())(any(), any())).thenReturn(Future.successful(Beneficiaries(Nil, List(ClassOfBeneficiary(description)))))
  when(mockTrustConnector.amendClassOfBeneficiary(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

  val mockTrustService: TrustService = mock[TrustService]
  when(mockTrustService.getBeneficiary(any(), any())(any(), any())).thenReturn(Future.successful(ClassOfBeneficiary(description)))

  "Description Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request = FakeRequest(GET, descriptionRoute)

      val view = application.injector.instanceOf[DescriptionView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(description), index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[TrustService].toInstance(mockTrustService),
            bind[TrustConnector].toInstance(mockTrustConnector)
          )
          .build()

      val request =
        FakeRequest(POST, descriptionRoute)
          .withFormUrlEncodedBody(("value", description))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.AddABeneficiaryController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, descriptionRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DescriptionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, index)(fakeRequest, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, descriptionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, descriptionRoute)
          .withFormUrlEncodedBody(("value", description))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
