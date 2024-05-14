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

package controllers.actions

import base.SpecBase
import models.requests.{IdentifierRequest, OrganisationUser}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Result
import uk.gov.hmrc.auth.core.Enrolments

import scala.concurrent.Future

class SaveSessionActionSpec extends SpecBase with MockitoSugar with ScalaFutures {

  class Harness(utr: String) extends SaveActiveSessionImpl(utr, mockSessionRepository) {
    def callFilter[A](request: IdentifierRequest[A]): Future[Option[Result]] = filter(request)
  }

  "Save Session Action" when {

    "setting a session" must {

      "continue the request and not block the request" in {

        when(mockSessionRepository.get("id")).thenReturn(Future.successful(None))
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

        val action = new Harness("utr")

        val futureResult = action.callFilter(IdentifierRequest(fakeRequest, OrganisationUser("id", Enrolments(Set()))))

        whenReady(futureResult) { result =>
          result mustBe None
        }
      }

    }
  }
}
