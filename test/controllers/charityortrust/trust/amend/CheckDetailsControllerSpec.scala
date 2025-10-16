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

package controllers.charityortrust.trust.amend

import base.SpecBase
import connectors.TrustConnector
import extractors.TrustBeneficiaryExtractor
import handlers.ErrorHandler
import models.CheckMode
import models.beneficiaries.TrustBeneficiary
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status, _}
import services.TrustService
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.mappers.TrustBeneficiaryMapper
import utils.print.TrustBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.charityortrust.trust.amend.CheckDetailsView
import java.time.LocalDate
import scala.concurrent.Future
import scala.util.Success

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures with BeforeAndAfterEach {

  private val index = 0

  private lazy val checkDetailsRoute = routes.CheckDetailsController.extractAndRender(index).url
  private lazy val checkCachedDetailsRoute = routes.CheckDetailsController.renderFromUserAnswers(index).url
  private lazy val updateDetailsRoute = routes.CheckDetailsController.extractAndRedirect(index).url
  private lazy val submitDetailsRoute = routes.CheckDetailsController.onSubmit(index).url

  private lazy val onwardRoute = controllers.routes.AddABeneficiaryController.onPageLoad().url

  private val mockService: TrustService = mock[TrustService]
  private val mockExtractor: TrustBeneficiaryExtractor = mock[TrustBeneficiaryExtractor]
  private val mockPrintHelper: TrustBeneficiaryPrintHelper = mock[TrustBeneficiaryPrintHelper]
  private val mockMapper: TrustBeneficiaryMapper = mock[TrustBeneficiaryMapper]
  private val mockTrustConnector: TrustConnector = mock[TrustConnector]

  private val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  private val trustBeneficiary = TrustBeneficiary(
    name = "Trust Name",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2019-03-09"),
    provisional = false
  )

  private val fakeAnswerSection = AnswerSection(Some("Heading"), Nil)

  override def beforeEach(): Unit = {
    reset(mockService)
    reset(mockExtractor)
    reset(mockPrintHelper)
    reset(mockMapper)
    reset(mockTrustConnector)

    when(mockExtractor.apply(any(), any(), any())).thenReturn(Success(emptyUserAnswers))

    when(mockPrintHelper.apply(any(), any(), any())(any())).thenReturn(fakeAnswerSection)

    when(mockMapper.apply(any())).thenReturn(Some(trustBeneficiary))

    when(mockService.getTrustBeneficiary(any(), any())(any(), any()))
      .thenReturn(Future.successful(trustBeneficiary))

    when(mockTrustConnector.amendTrustBeneficiary(any(), any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))
  }

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET (check) for a given index" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockService),
          bind[TrustBeneficiaryExtractor].toInstance(mockExtractor),
          bind[TrustBeneficiaryPrintHelper].toInstance(mockPrintHelper)
        ).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(fakeAnswerSection), index)(request, messages).toString
    }

    "return OK and the correct view for a GET (saved) for a given index" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustBeneficiaryPrintHelper].toInstance(mockPrintHelper)
        ).build()

      val request = FakeRequest(GET, checkCachedDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(fakeAnswerSection), index)(request, messages).toString
    }

    "return OK and redirect for a GET (update) for a given index" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[TrustService].toInstance(mockService),
          bind[TrustBeneficiaryExtractor].toInstance(mockExtractor)
        ).build()

      val request = FakeRequest(GET, updateDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.charityortrust.trust.routes.NameController.onPageLoad(CheckMode).url
    }

    "redirect to the 'add a beneficiary' page when submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
        .overrides(
          bind[TrustBeneficiaryMapper].toInstance(mockMapper),
          bind[TrustConnector].toInstance(mockTrustConnector)
        ).build()

      val request = FakeRequest(POST, submitDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

    "return error when mapper fails" in {

      when(mockMapper.apply(any())).thenReturn(None)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent)
        .overrides(
          bind[TrustBeneficiaryMapper].toInstance(mockMapper),
          bind[TrustConnector].toInstance(mockTrustConnector)
        ).build()

      val request = FakeRequest(POST, submitDetailsRoute)

      val result = route(application, request).value
      status(result) mustEqual INTERNAL_SERVER_ERROR
      contentAsString(result) mustEqual errorHandler.internalServerErrorTemplate(request).futureValue.toString()

      application.stop()
    }
  }
}
