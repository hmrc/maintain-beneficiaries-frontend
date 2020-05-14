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

package controllers

import java.time.LocalDate

import base.SpecBase
import forms.AddBeneficiaryTypeFormProvider
import models.NormalMode
import models.beneficiaries.TypeOfBeneficiaryToAdd.{CharityOrTrust, ClassOfBeneficiaries, CompanyOrEmploymentRelated, Individual, Other, prefix}
import models.beneficiaries.{Beneficiaries, ClassOfBeneficiary, TypeOfBeneficiaryToAdd}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.AddNowPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import viewmodels.RadioOption
import views.html.AddNowView

import scala.concurrent.Future

class AddNowControllerSpec extends SpecBase with MockitoSugar {

  val form: Form[TypeOfBeneficiaryToAdd] = new AddBeneficiaryTypeFormProvider()()
  lazy val addNowRoute: String = routes.AddNowController.onPageLoad().url
  val classOfBeneficiariesAnswer: TypeOfBeneficiaryToAdd.ClassOfBeneficiaries.type = TypeOfBeneficiaryToAdd.ClassOfBeneficiaries

  val mockTrustService: TrustService = mock[TrustService]

  when(mockTrustService.getBeneficiaries(any())(any(), any()))
    .thenReturn(Future.successful(Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil)))

  val values: List[TypeOfBeneficiaryToAdd] = List(
    Individual, ClassOfBeneficiaries, CharityOrTrust, CompanyOrEmploymentRelated, Other
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption(prefix, value.toString)
  }

  "AddNow Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request = FakeRequest(GET, addNowRoute)

      val view = application.injector.instanceOf[AddNowView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, options)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = emptyUserAnswers.set(AddNowPage, classOfBeneficiariesAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(answers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request = FakeRequest(GET, addNowRoute)

      val view = application.injector.instanceOf[AddNowView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(classOfBeneficiariesAnswer), options)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when Class of beneficiaries is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", classOfBeneficiariesAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.classofbeneficiary.add.routes.DescriptionController.onPageLoad().url

      application.stop()
    }

    "redirect to the next page when Individual beneficiary is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.Individual.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.individualbeneficiary.routes.NameController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to the next page when Charity or trust is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.CharityOrTrust.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.charityortrust.routes.CharityOrTrustController.onPageLoad().url

      application.stop()
    }

    "redirect to the next page when Charity is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.Charity.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.charityortrust.charity.routes.NameController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to the next page when Trust is submitted" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.Trust.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.charityortrust.trust.routes.NameController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to the next page when Other beneficiary is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.Other.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.other.add.routes.DescriptionController.onPageLoad().url

      application.stop()
    }

    "redirect to the next page when Company or employment related is submitted" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.CompanyOrEmploymentRelated.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.companyoremploymentrelated.routes.CompanyOrEmploymentRelatedController.onPageLoad().url

      application.stop()
    }

    "redirect to the next page when Company is submitted" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.Company.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.companyoremploymentrelated.company.routes.NameController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to the next page when Employment related is submitted" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", TypeOfBeneficiaryToAdd.EmploymentRelated.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.companyoremploymentrelated.employment.add.routes.NameController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request = FakeRequest(POST, addNowRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AddNowView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, options)(fakeRequest, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request = FakeRequest(GET, addNowRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request =
        FakeRequest(POST, addNowRoute)
          .withFormUrlEncodedBody(("value", classOfBeneficiariesAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "not show a certain radio option if there are 25 or more of that type of beneficiary" in {

      val classOfBeneficiaries = ClassOfBeneficiary("description", LocalDate.parse("2019-02-03"), provisional = false)

      val classesOfBeneficiaries: List[ClassOfBeneficiary] = List.fill(25)(classOfBeneficiaries)

      val beneficiaries = Beneficiaries(Nil, classesOfBeneficiaries, Nil, Nil, Nil, Nil, Nil)

      when(mockTrustService.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(beneficiaries))

      val application = applicationBuilder(Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockTrustService)
        ).build()

      val request = FakeRequest(GET, addNowRoute)

      val view = application.injector.instanceOf[AddNowView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, beneficiaries.nonMaxedOutOptions)(fakeRequest, messages).toString

      application.stop()

    }
  }
}
