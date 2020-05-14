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

package controllers.individualbeneficiary.amend

import java.time.LocalDate

import base.SpecBase
import connectors.TrustConnector
import models.beneficiaries.IndividualBeneficiary
import models.{Name, NationalInsuranceNumber}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.individualbeneficiary._
import pages.individualbeneficiary.add.StartDatePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.IndividualBeneficiaryPrintHelper
import views.html.individualbeneficiary.amend.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val index = 0

  private lazy val checkDetailsRoute = routes.CheckDetailsController.extractAndRender(index).url
  private lazy val submitDetailsRoute = routes.CheckDetailsController.onSubmit(index).url

  private lazy val onwardRoute = controllers.routes.AddABeneficiaryController.onPageLoad().url

  private val name = Name("First", None, "Last")
  private val dateOfBirth = LocalDate.parse("2010-02-03")
  private val nino = "AA123456A"
  private val startDate = LocalDate.parse("2019-03-09")

  private val individualBeneficiary = IndividualBeneficiary(
    name = Name(
      firstName = "First",
      middleName = None,
      lastName = "Last"
    ),
    dateOfBirth = Some(LocalDate.parse("2010-02-03")),
    identification = Some(NationalInsuranceNumber("AA123456A")),
    address = None,
    vulnerableYesNo = false,
    roleInCompany = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2019-03-09"),
    provisional = false
  )

  private val userAnswers = emptyUserAnswers
    .set(NamePage, name).success.value
    .set(DateOfBirthYesNoPage, true).success.value
    .set(DateOfBirthPage, dateOfBirth).success.value
    .set(NationalInsuranceNumberYesNoPage, true).success.value
    .set(NationalInsuranceNumberPage, nino).success.value
    .set(VPE1FormYesNoPage, false).success.value
    .set(IncomeDiscretionYesNoPage, true).success.value
    .set(StartDatePage, startDate).success.value

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET for a given index" in {

      val mockService : TrustService = mock[TrustService]

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[TrustService].toInstance(mockService)
        )
        .build()

      when(mockService.getIndividualBeneficiary(any(), any())(any(), any()))
        .thenReturn(Future.successful(individualBeneficiary))

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]
      val printHelper = application.injector.instanceOf[IndividualBeneficiaryPrintHelper]
      val answerSection = printHelper(userAnswers, provisional = false, name.displayName)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(answerSection, index)(fakeRequest, messages).toString
    }

    "redirect to the 'add a beneficiary' page when submitted" in {

      val mockTrustConnector = mock[TrustConnector]

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
          .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
          .build()

      when(mockTrustConnector.amendIndividualBeneficiary(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

      val request = FakeRequest(POST, submitDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

  }
}
