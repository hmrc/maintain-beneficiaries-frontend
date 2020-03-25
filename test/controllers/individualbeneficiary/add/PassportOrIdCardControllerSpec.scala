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

package controllers.individualbeneficiary.add

import java.time.LocalDate

import base.SpecBase
import forms.CombinedPassportOrIdCardDetailsFormProvider
import models.{CombinedPassportOrIdCard, Name, Passport}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.NamePage
import pages.individualbeneficiary.PassportOrIdCardDetailsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import utils.countryOptions.CountryOptions
import views.html.individualbeneficiary.add.PassportOrIdCardDetailsView

import scala.concurrent.Future

class PassportOrIdCardControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new CombinedPassportOrIdCardDetailsFormProvider()
  val form = formProvider.withPrefix("individualBeneficiary")

  lazy val passportDetailsRoute = routes.PassportOrIdCardController.onPageLoad().url

  val countryOptions = injector.instanceOf[CountryOptions]

  override val emptyUserAnswers = super.emptyUserAnswers
    .set(NamePage, Name("Individual", None, "Beneficiary")).success.value

  "PassportDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, passportDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PassportOrIdCardDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, "Individual Beneficiary", countryOptions.options)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "NUMBER", LocalDate.of(2040, 12, 31))).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, passportDetailsRoute)

      val view = application.injector.instanceOf[PassportOrIdCardDetailsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(CombinedPassportOrIdCard("GB", "NUMBER", LocalDate.of(2040, 12, 31))), "Individual Beneficiary", countryOptions.options)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockPlaybackRepository = mock[PlaybackRepository]

      when(mockPlaybackRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(
            "country"-> "GB",
            "expiryDate.day" -> "21",
            "expiryDate.month" -> "3",
            "expiryDate.year" -> "2079",
            "number" -> "PASSPORTNUMBER"
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PassportOrIdCardDetailsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, "Individual Beneficiary", countryOptions.options)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, passportDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}

