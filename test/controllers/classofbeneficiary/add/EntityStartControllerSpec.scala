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

package controllers.classofbeneficiary.add

import base.SpecBase
import forms.DateAddedToTrustFormProvider
import models.UserAnswers
import org.scalatestplus.mockito.MockitoSugar
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.classofbeneficiary.add.EntityStartView

import java.time.LocalDate

class EntityStartControllerSpec extends SpecBase with MockitoSugar {

  val date: LocalDate = LocalDate.parse("2019-02-03")
  val form: Form[LocalDate] = new DateAddedToTrustFormProvider().withPrefixAndTrustStartDate("classOfBeneficiary.entityStart", date)
  lazy val entityStartRoute: String = routes.EntityStartController.onPageLoad().url
  val description: String = "Description"

  val answersWithDescription: UserAnswers = emptyUserAnswers.set(DescriptionPage, description).success.value

  "EntityStart Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(answersWithDescription)).build()

      val request = FakeRequest(GET, entityStartRoute)

      val view = application.injector.instanceOf[EntityStartView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, description)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = answersWithDescription.set(EntityStartPage, date).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, entityStartRoute)

      val view = application.injector.instanceOf[EntityStartView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(date), description)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val answers = emptyUserAnswers.copy(whenTrustSetup = date)

      val application =
        applicationBuilder(userAnswers = Some(answers)).build()

      val request =
        FakeRequest(POST, entityStartRoute)
          .withFormUrlEncodedBody(
            "value.day" -> date.getDayOfMonth.toString,
            "value.month" -> date.getMonthValue.toString,
            "value.year"  -> date.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CheckDetailsController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(answersWithDescription)).build()

      val request = FakeRequest(POST, entityStartRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[EntityStartView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, description)(request, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, entityStartRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, entityStartRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
