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

package controllers.companyoremploymentrelated.employment

import base.SpecBase
import config.annotations.EmploymentRelatedBeneficiary
import forms.NonUkAddressFormProvider
import models.{NonUkAddress, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.companyoremploymentrelated.employment.{NamePage, NonUkAddressPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.html.companyoremploymentrelated.employment.NonUkAddressView

class NonUkAddressControllerSpec extends SpecBase with MockitoSugar {

  private val form: Form[NonUkAddress] = new NonUkAddressFormProvider()()
  private val nonUkAddressRoute: String = routes.NonUkAddressController.onPageLoad(NormalMode).url
  private val name: String = "Large"
  private val onwardRoute = Call("GET", "/foo")
  private val answer = NonUkAddress("Line 1", "Line 2", None, "DE")
  private val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

  val baseAnswers = emptyUserAnswers.set(NamePage, name).success.value

  "NonUkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, nonUkAddressRoute)

      val view = application.injector.instanceOf[NonUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, countryOptions, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = baseAnswers.set(NonUkAddressPage, answer).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, nonUkAddressRoute)

      val view = application.injector.instanceOf[NonUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(answer), NormalMode, countryOptions, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[EmploymentRelatedBeneficiary]).toInstance(new FakeNavigator(onwardRoute))
          ).build()

      val request =
        FakeRequest(POST, nonUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "DE"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(POST, nonUkAddressRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[NonUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, countryOptions, name)(request, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, nonUkAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, nonUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "DE"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
