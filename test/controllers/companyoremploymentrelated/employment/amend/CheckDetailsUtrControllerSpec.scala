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

package controllers.companyoremploymentrelated.employment.amend

import base.SpecBase
import connectors.TrustConnector
import models.Description
import models.HowManyBeneficiaries.Over1
import models.beneficiaries.{CompanyOrEmploymentRelatedToAdd, TypeOfBeneficiaryToAdd}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.AddNowPage
import pages.companyoremploymentrelated.CompanyOrEmploymentRelatedPage
import pages.companyoremploymentrelated.employment._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.EmploymentRelatedBeneficiaryPrintHelper
import views.html.companyoremploymentrelated.employment.amend.CheckDetailsUtrView

import java.time.LocalDate
import scala.concurrent.Future

class CheckDetailsUtrControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val name: String = "Employment"
  private val description = Description("Some Description", None, None, None, None)
  private val utr: String = "UTRUTRUTR"
  private val date: LocalDate = LocalDate.parse("2019-02-03")

  private lazy val checkDetailsUtrRoute = routes.CheckDetailsUtrController.onPageLoad().url
  private lazy val submitDetailsUtrRoute = routes.CheckDetailsUtrController.onSubmit().url
  private lazy val onwardRoute = controllers.routes.AddABeneficiaryController.onPageLoad().url

  private val userAnswers = emptyUserAnswers
    .set(AddNowPage, TypeOfBeneficiaryToAdd.CompanyOrEmploymentRelated).success.value
    .set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.EmploymentRelated).success.value
    .set(NamePage, name).success.value
    .set(UtrPage, utr).success.value
    .set(AddressYesNoPage, false).success.value
    .set(DescriptionPage, description).success.value
    .set(NumberOfBeneficiariesPage, Over1).success.value
    .set(StartDatePage, date).success.value

  "CheckDetailsUtr Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, checkDetailsUtrRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsUtrView]
      val printHelper = application.injector.instanceOf[EmploymentRelatedBeneficiaryPrintHelper]
      val answerSection = printHelper(userAnswers, provisional = false, name)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(answerSection, name)(request, messages).toString
    }

    "redirect to the 'add a beneficiary' page when submitted" in {

      val mockTrustConnector = mock[TrustConnector]

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
          .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
          .build()

      when(mockTrustConnector.amendEmploymentRelatedBeneficiary(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val request = FakeRequest(POST, submitDetailsUtrRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

  }
}
