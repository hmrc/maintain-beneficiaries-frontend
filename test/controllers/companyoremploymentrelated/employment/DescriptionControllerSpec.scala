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

package controllers.companyoremploymentrelated.employment

import base.SpecBase
import config.annotations.EmploymentRelatedBeneficiary
import forms.EmploymentRelatedBeneficiaryDescriptionFormProvider
import models.{Description, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.companyoremploymentrelated.employment.DescriptionPage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.companyoremploymentrelated.employment.DescriptionView

class DescriptionControllerSpec extends SpecBase with MockitoSugar {

  private val form: Form[Description] = new EmploymentRelatedBeneficiaryDescriptionFormProvider().withPrefix("employmentBeneficiary.description")
  private val descriptionRoute: String = routes.DescriptionController.onPageLoad(NormalMode).url
  private val description: Description = Description("Description", None, None, None, None)
  private val onwardRoute = Call("GET", "/foo")

  "Description Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, descriptionRoute)

      val view = application.injector.instanceOf[DescriptionView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = emptyUserAnswers.set(DescriptionPage, description).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, descriptionRoute)

      val view = application.injector.instanceOf[DescriptionView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(description), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[EmploymentRelatedBeneficiary]).toInstance(new FakeNavigator(onwardRoute))
          ).build()

      val request =
        FakeRequest(POST, descriptionRoute)
          .withFormUrlEncodedBody(("description", description.description))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, descriptionRoute)

      val boundForm = form.bind(Map("description" -> ""))

      val view = application.injector.instanceOf[DescriptionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, descriptionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, descriptionRoute)
          .withFormUrlEncodedBody(("description", description.description))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
