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

package controllers.charityortrust.charity

import base.SpecBase
import config.annotations.CharityBeneficiary
import forms.DateAddedToTrustFormProvider
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.charityortrust.charity.{NamePage, StartDatePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.charityortrust.charity.StartDateView

import java.time.LocalDate

class StartDateControllerSpec extends SpecBase with MockitoSugar {

  private val date: LocalDate = LocalDate.parse("2019-02-01")
  private val form: Form[LocalDate] = new DateAddedToTrustFormProvider().withPrefixAndTrustStartDate("charityBeneficiary.startDate", date)
  private val startDateRoute: String = routes.StartDateController.onPageLoad().url
  private val name: String = "Charity"
  private val onwardRoute = Call("GET", "/foo")
  private val answer = LocalDate.parse("2019-02-03")

  val baseAnswers = emptyUserAnswers.copy(whenTrustSetup = date)
    .set(NamePage, name).success.value

  "NonUkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, startDateRoute)

      val view = application.injector.instanceOf[StartDateView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = baseAnswers.set(StartDatePage, answer).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, startDateRoute)

      val view = application.injector.instanceOf[StartDateView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(answer), name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[CharityBeneficiary]).toInstance(new FakeNavigator(onwardRoute))
          ).build()

      val request =
        FakeRequest(POST, startDateRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> answer.getDayOfMonth.toString,
            "value.month" -> answer.getMonthValue.toString,
            "value.year"  -> answer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(POST, startDateRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[StartDateView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, name)(request, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, startDateRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, startDateRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> answer.getDayOfMonth.toString,
            "value.month" -> answer.getMonthValue.toString,
            "value.year"  -> answer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
