/*
 * Copyright 2023 HM Revenue & Customs
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
import config.annotations
import forms.PassportDetailsFormProvider
import models.beneficiaries.{Beneficiaries, IndividualBeneficiary}
import models.{Mode, Name, NormalMode, Passport, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.individualbeneficiary.{NamePage, PassportDetailsPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.PlaybackRepository
import services.TrustServiceImpl
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.html.individualbeneficiary.PassportDetailsView

import java.time.LocalDate
import scala.concurrent.Future

class PassportDetailsControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

  private val validData: Passport = Passport("country", "passport number", LocalDate.of(2020, 1, 1))

  private val mockTrustsService = mock[TrustServiceImpl]

  private val individualBeneficiary = IndividualBeneficiary(
    name = Name("First", None, "last"),
    dateOfBirth = None,
    identification = Some(validData),
    address = None,
    vulnerableYesNo = None,
    roleInCompany = None,
    income = None  ,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val beneficiaries: Beneficiaries = Beneficiaries(
    List(individualBeneficiary)
  )

  override protected def beforeEach(): Unit = {
    reset(mockTrustsService)
    when(mockTrustsService.getBeneficiaries(any())(any(), any()))
      .thenReturn(Future.successful(beneficiaries))
  }

  private val formProvider = new PassportDetailsFormProvider(frontendAppConfig)
  private def form: Form[Passport] = formProvider.withPrefix("individualBeneficiary", beneficiaries)

  private def onwardRoute: Call = Call("GET", "/foo")
  private val name: Name = Name("FirstName", None, "LastName")

  private val baseAnswers: UserAnswers = emptyUserAnswers
    .set(NamePage, name).success.value

  private val mode: Mode = NormalMode
  private val passportDetailsRoute: String = routes.PassportDetailsController.onPageLoad(mode).url

  private val getRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, passportDetailsRoute)

  private val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

  "PassportDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers))
        .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
        .build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[PassportDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mode, countryOptions, name.displayName)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers
        .set(NamePage, name).success.value
        .set(PassportDetailsPage, validData).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
        .build()

      val view = application.injector.instanceOf[PassportDetailsView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validData), mode, countryOptions, name.displayName)(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockPlaybackRepository = mock[PlaybackRepository]

      when(mockPlaybackRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(bind[Navigator].qualifiedWith(classOf[annotations.IndividualBeneficiary]).toInstance(fakeNavigator))
          .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
          .build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day"   -> validData.expirationDate.getDayOfMonth.toString,
            "expiryDate.month" -> validData.expirationDate.getMonthValue.toString,
            "expiryDate.year"  -> validData.expirationDate.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers))
        .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
        .build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PassportDetailsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mode, countryOptions, name.displayName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
        .build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[TrustServiceImpl].toInstance(mockTrustsService))
        .build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day"   -> validData.expirationDate.getDayOfMonth.toString,
            "expiryDate.month" -> validData.expirationDate.getMonthValue.toString,
            "expiryDate.year"  -> validData.expirationDate.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
