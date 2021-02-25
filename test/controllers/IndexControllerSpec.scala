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

package controllers

import base.SpecBase
import connectors.TrustConnector
import models.{TrustDetails, TypeOfTrust, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService

import java.time.LocalDate
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {

    "populate user answers and redirect" in {

      val is5mldEnabled = false
      val trustType = TypeOfTrust.WillTrustOrIntestacyTrust
      val startDate = "2019-06-01"
      val identifier = "1234567890"

      reset(playbackRepository)

      val mockTrustConnector = mock[TrustConnector]
      val mockFeatureFlagService = mock[FeatureFlagService]

      when(playbackRepository.set(any()))
        .thenReturn(Future.successful(true))

      when(mockTrustConnector.getTrustDetails(any())(any(), any()))
        .thenReturn(Future.successful(TrustDetails(startDate = startDate, typeOfTrust = Some(trustType))))

      when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
        .thenReturn(Future.successful(is5mldEnabled))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[TrustConnector].toInstance(mockTrustConnector),
          bind[FeatureFlagService].toInstance(mockFeatureFlagService)
        ).build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(identifier).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.AddABeneficiaryController.onPageLoad().url)

      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(playbackRepository).set(uaCaptor.capture)

      uaCaptor.getValue.internalId mustBe "id"
      uaCaptor.getValue.identifier mustBe identifier
      uaCaptor.getValue.whenTrustSetup mustBe LocalDate.parse(startDate)
      uaCaptor.getValue.trustType.get mustBe trustType
      uaCaptor.getValue.is5mldEnabled mustBe is5mldEnabled

      application.stop()
    }
  }
}
