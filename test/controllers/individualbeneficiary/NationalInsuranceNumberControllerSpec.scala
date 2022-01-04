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
import forms.NationalInsuranceNumberFormProvider
import models.{Name, NormalMode}
import navigation.Navigator
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.individualbeneficiary.amend.IndexPage
import pages.individualbeneficiary.{NamePage, NationalInsuranceNumberPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustServiceImpl
import views.html.individualbeneficiary.NationalInsuranceNumberView

import scala.concurrent.Future

class NationalInsuranceNumberControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

  val formProvider = new NationalInsuranceNumberFormProvider()
  val form: Form[String] = formProvider.apply("individualBeneficiary.nationalInsuranceNumber", Nil)

  val index = 0
  val name: Name = Name("FirstName", None, "LastName")

  lazy val nationalInsuranceNumberRoute: String = routes.NationalInsuranceNumberController.onPageLoad(NormalMode).url

  val mockTrustsService: TrustServiceImpl = mock[TrustServiceImpl]

  override protected def beforeEach(): Unit = {
    reset(mockTrustsService)
    when(mockTrustsService.getIndividualNinos(any(), any())(any(), any()))
      .thenReturn(Future.successful(Nil))
  }

  "NationalInsuranceNumber Controller" must {

    "return OK and the correct view for a GET" when {

      "adding" in {

        val userAnswers = emptyUserAnswers.set(NamePage, name).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
          .build()

        val request = FakeRequest(GET, nationalInsuranceNumberRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[NationalInsuranceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, name.displayName)(request, messages).toString

        verify(mockTrustsService).getIndividualNinos(any(), eqTo(None))(any(), any())

        application.stop()
      }

      "amending" in {

        val userAnswers = emptyUserAnswers
          .set(IndexPage, index).success.value
          .set(NamePage, name).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
          .build()

        val request = FakeRequest(GET, nationalInsuranceNumberRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[NationalInsuranceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, name.displayName)(request, messages).toString

        verify(mockTrustsService).getIndividualNinos(any(), eqTo(Some(index)))(any(), any())

        application.stop()
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(NationalInsuranceNumberPage, "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
        .build()

      val request = FakeRequest(GET, nationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), NormalMode, name.displayName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "adding" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(fakeNavigator),
            bind[TrustServiceImpl].toInstance(mockTrustsService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "AA000000A"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        verify(mockTrustsService).getIndividualNinos(any(), eqTo(None))(any(), any())

        application.stop()
      }

      "amending" in {

        val userAnswers = emptyUserAnswers.set(IndexPage, index).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(fakeNavigator),
            bind[TrustServiceImpl].toInstance(mockTrustsService)
          ).build()

        val request = FakeRequest(POST, nationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "AA000000A"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        verify(mockTrustsService).getIndividualNinos(any(), eqTo(Some(index)))(any(), any())

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage, name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
        .build()

      val request = FakeRequest(POST, nationalInsuranceNumberRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, name.displayName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, nationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, nationalInsuranceNumberRoute)
        .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
