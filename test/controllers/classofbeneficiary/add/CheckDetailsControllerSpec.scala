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

package controllers.classofbeneficiary.add

import java.time.LocalDate

import base.SpecBase
import connectors.TrustConnector
import models.beneficiaries.TypeOfBeneficiaryToAdd
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.AddNowPage
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.print.ClassOfBeneficiaryPrintHelper
import views.html.classofbeneficiary.add.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val description: String = "Description"
  private val date: LocalDate = LocalDate.parse("2019-02-03")

  private lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoad().url
  private lazy val submitDetailsRoute = routes.CheckDetailsController.onSubmit().url
  private lazy val onwardRoute = controllers.routes.AddABeneficiaryController.onPageLoad().url

  private val userAnswers = emptyUserAnswers
    .set(AddNowPage, TypeOfBeneficiaryToAdd.ClassOfBeneficiaries).success.value
    .set(DescriptionPage, description).success.value
    .set(EntityStartPage, date).success.value

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]
      val printHelper = application.injector.instanceOf[ClassOfBeneficiaryPrintHelper]
      val answerSection = printHelper(userAnswers, description)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(answerSection))(request, messages).toString
    }

    "redirect to the 'add a beneficiary' page when submitted" in {

      val mockTrustConnector = mock[TrustConnector]

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
          .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
          .build()

      when(mockTrustConnector.addClassOfBeneficiary(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val request = FakeRequest(POST, submitDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

  }
}
