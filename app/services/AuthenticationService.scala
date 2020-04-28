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

import com.google.inject.Inject
import connectors.TrustAuthConnector
import models.requests.DataRequest
import models.{TrustAuthAgentAllowed, TrustAuthAllowed, TrustAuthDenied, TrustAuthInternalServerError}
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationServiceImpl @Inject()(trustAuthConnector: TrustAuthConnector) extends AuthenticationService {
  override def authenticateAgent()
                                (implicit hc: HeaderCarrier): Future[Either[Result, String]] = {
    trustAuthConnector.agentIsAuthorised().flatMap {
      case TrustAuthAgentAllowed(arn) => Future.successful(Right(arn))
      case TrustAuthDenied(redirectUrl) => Future.successful(Left(Redirect(redirectUrl)))
      case _ =>
        Logger.warn(s"Unable to authenticate agent with trusts-auth")
        Future.successful(Left(InternalServerError))
    }  }

  override def authenticateForUtr[A](utr: String)
                                    (implicit request: DataRequest[A], hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]] = {
    trustAuthConnector.authorisedForUtr(utr).flatMap {
      case TrustAuthAllowed => Future.successful(Right(request))
      case TrustAuthDenied(redirectUrl) => Future.successful(Left(Redirect(redirectUrl)))
      case _ =>
        Logger.warn(s"Unable to authenticate for utr with trusts-auth")
        Future.successful(Left(InternalServerError))
    }
  }

}

trait AuthenticationService {
  def authenticateAgent()
                       (implicit hc: HeaderCarrier): Future[Either[Result, String]]
  def authenticateForUtr[A](utr: String)
                           (implicit request: DataRequest[A],
                            hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]]
}
