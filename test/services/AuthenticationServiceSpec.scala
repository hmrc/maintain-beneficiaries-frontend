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

package services

import base.SpecBase
import config.FrontendAppConfig
import connectors.{TrustAuthAllowed, TrustAuthConnector, TrustAuthDenied, TrustAuthInternalServerError}
import models.requests.{AgentUser, DataRequest}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{EitherValues, RecoverMethods}
import play.api.inject.bind
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import play.api.test.Helpers._

import scala.concurrent.Future

class AuthenticationServiceSpec extends SpecBase with ScalaFutures with EitherValues with RecoverMethods {

  private val utr = "0987654321"

  private val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  private val agentEnrolment = Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)
  private val trustsEnrolment = Enrolment("HMRC-TERS-ORG", List(EnrolmentIdentifier("SAUTR", utr)), "Activated", None)

  private val enrolments = Enrolments(Set(
    agentEnrolment,
    trustsEnrolment
  ))

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  private implicit val dataRequest = DataRequest[AnyContent](fakeRequest, emptyUserAnswers, AgentUser("internalId", enrolments, "arn"))

  type RetrievalType = Option[String] ~ Option[AffinityGroup] ~ Enrolments

  lazy val trustAuthConnector = mock[TrustAuthConnector]

  "invoking authenticate" when {
    "user is authenticated" must {
      "return the data request" in {
        when(trustAuthConnector.authorisedForUtr(any())(any(), any())).thenReturn(Future.successful(TrustAuthAllowed))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticate[AnyContent](utr)) {
          result =>
            result.right.value mustBe dataRequest
        }
      }
    }
    "user requires additional action" must {
      "redirect to desired url" in {
        when(trustAuthConnector.authorisedForUtr(any())(any(), any())).thenReturn(Future.successful(TrustAuthDenied("some-url")))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticate[AnyContent](utr)) {
          result =>
            val r = Future.successful(result.left.value)
            status(r) mustBe SEE_OTHER
            redirectLocation(r) mustEqual Some("some-url")
        }
      }
    }
    "an internal server error is returned" must {
      "return an internal server error result" in {
        when(trustAuthConnector.authorisedForUtr(any())(any(), any())).thenReturn(Future.successful(TrustAuthInternalServerError))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticate[AnyContent](utr)) {
          result =>
            result.left.value.header.status mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

  }

  private def buildApp = {
    applicationBuilder()
      .overrides(bind[TrustAuthConnector].toInstance(trustAuthConnector))
      .build()
  }
}
