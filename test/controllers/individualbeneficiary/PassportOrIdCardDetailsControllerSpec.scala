/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.LocalDate

import base.SpecBase
import config.annotations.IndividualBeneficiary
import forms.CombinedPassportOrIdCardDetailsFormProvider
import models.{CombinedPassportOrIdCard, DetailsType, Mode, Name, NormalMode}
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.individualbeneficiary.NamePage
import pages.individualbeneficiary.PassportOrIdCardDetailsPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.html.individualbeneficiary.PassportOrIdCardDetailsView

import scala.concurrent.Future

class PassportOrIdCardDetailsControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new CombinedPassportOrIdCardDetailsFormProvider(frontendAppConfig)
  val form = formProvider.withPrefix("individualBeneficiary.passportOrIdCardDetails")
  val name = Name("FirstName", None, "LastName")

  val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

  val baseAnswers = emptyUserAnswers
    .set(NamePage, name).success.value

  val mode: Mode = NormalMode
  lazy val passportOrIdCardDetailsRoute = routes.PassportOrIdCardDetailsController.onPageLoad(mode).url

  val validData = CombinedPassportOrIdCard("country", "number", LocalDate.parse("2020-02-03"))

  "PassportOrIdCardDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, passportOrIdCardDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PassportOrIdCardDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mode, name.displayName, countryOptions)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers.set(PassportOrIdCardDetailsPage, validData).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, passportOrIdCardDetailsRoute)

      val view = application.injector.instanceOf[PassportOrIdCardDetailsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validData), mode, name.displayName, countryOptions)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockPlaybackRepository = mock[PlaybackRepository]

      when(mockPlaybackRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(baseAnswers))
        .overrides(bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(fakeNavigator))
        .build()

      val request =
        FakeRequest(POST, passportOrIdCardDetailsRoute)
          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day"   -> validData.expirationDate.getDayOfMonth.toString,
            "expiryDate.month" -> validData.expirationDate.getMonthValue.toString,
            "expiryDate.year"  -> validData.expirationDate.getYear.toString,
            "detailsType"      -> DetailsType.Combined.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, passportOrIdCardDetailsRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PassportOrIdCardDetailsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mode, name.displayName, countryOptions)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, passportOrIdCardDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, passportOrIdCardDetailsRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}