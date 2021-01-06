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
import models.{TrustDetails, TypeOfTrust}
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {

    "return OK and the correct view for a GET" in {

      val mockTrustConnector = mock[TrustConnector]

      when(mockTrustConnector.getTrustDetails(any())(any(), any()))
        .thenReturn(Future.successful(TrustDetails(startDate = "2019-06-01", typeOfTrust = TypeOfTrust.WillTrustOrIntestacyTrust)))

      val application = applicationBuilder(userAnswers = None).overrides(bind[TrustConnector].toInstance(mockTrustConnector)).build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad("UTRUTRUTR").url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(controllers.routes.AddABeneficiaryController.onPageLoad().url)

      application.stop()
    }
  }
}
