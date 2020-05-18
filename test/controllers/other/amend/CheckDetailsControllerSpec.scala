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

package controllers.other.amend

import java.time.LocalDate

import base.SpecBase
import connectors.TrustConnector
import models.UkAddress
import models.beneficiaries.OtherBeneficiary
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.other._
import pages.other.add.StartDatePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustService
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.OtherBeneficiaryPrintHelper
import views.html.other.amend.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val index = 0

  private lazy val checkDetailsRoute = routes.CheckDetailsController.extractAndRender(index).url
  private lazy val submitDetailsRoute = routes.CheckDetailsController.onSubmit(index).url

  private lazy val onwardRoute = controllers.routes.AddABeneficiaryController.onPageLoad().url

  private val description = "Other Name"
  private val startDate = LocalDate.parse("2019-03-09")
  private val address = UkAddress("Line 1", "Line 2", None, None, "NE98 1ZZ")

  private val otherBeneficiary = OtherBeneficiary(
    description = "Other Name",
    address = Some(UkAddress("Line 1", "Line 2", None, None, "NE98 1ZZ")),
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2019-03-09"),
    provisional = false
  )

  private val userAnswers = emptyUserAnswers
    .set(DescriptionPage, description).success.value
    .set(AddressYesNoPage, true).success.value
    .set(AddressUkYesNoPage, true).success.value
    .set(UkAddressPage, address).success.value
    .set(DiscretionYesNoPage, true).success.value
    .set(StartDatePage, startDate).success.value

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET for a given index" in {

      val mockService : TrustService = mock[TrustService]

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[TrustService].toInstance(mockService)
        )
        .build()

      when(mockService.getOtherBeneficiary(any(), any())(any(), any()))
        .thenReturn(Future.successful(otherBeneficiary))

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]
      val printHelper = application.injector.instanceOf[OtherBeneficiaryPrintHelper]
      val answerSection = printHelper(userAnswers, provisional = false, description)

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

      when(mockTrustConnector.amendOtherBeneficiary(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

      val request = FakeRequest(POST, submitDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

  }
}
