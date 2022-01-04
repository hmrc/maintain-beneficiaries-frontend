/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.individualbeneficiary

import base.SpecBase
import config.annotations.IndividualBeneficiary
import forms.YesNoFormProvider
import models.{Mode, Name, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.individualbeneficiary.{IdCardDetailsYesNoPage, NamePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import views.html.individualbeneficiary.IdCardDetailsYesNoView

import scala.concurrent.Future

class IdCardDetailsYesNoControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new YesNoFormProvider()
  private def form = formProvider.withPrefix("individualBeneficiary.idCardDetailsYesNo")

  def onwardRoute: Call = Call("GET", "/foo")
  val name: Name = Name("FirstName", None, "LastName")

  val baseAnswers: UserAnswers = emptyUserAnswers
    .set(NamePage, name).success.value

  val mode: Mode = NormalMode
  val idCardDetailsYesNoRoute: String = routes.IdCardDetailsYesNoController.onPageLoad(mode).url

  val getRequest = FakeRequest(GET, idCardDetailsYesNoRoute)

  "IdCardDetailsYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[IdCardDetailsYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mode, name.displayName)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers
        .set(NamePage, name).success.value
        .set(IdCardDetailsYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[IdCardDetailsYesNoView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), mode, name.displayName)(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockPlaybackRepository = mock[PlaybackRepository]

      when(mockPlaybackRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(fakeNavigator))

          .build()

      val request =
        FakeRequest(POST, idCardDetailsYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, idCardDetailsYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IdCardDetailsYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mode, name.displayName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, idCardDetailsYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
