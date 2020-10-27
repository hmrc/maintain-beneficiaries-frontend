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

package connectors

import com.google.inject.ImplementedBy
import config.FrontendAppConfig
import javax.inject.Inject
import models.{TrustAuthInternalServerError, TrustAuthResponse}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TrustAuthConnectorImpl])
trait TrustAuthConnector {
  def agentIsAuthorised()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse]
  def authorisedForUtr(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse]
}

class TrustAuthConnectorImpl @Inject()(http: HttpClient, config: FrontendAppConfig)
  extends TrustAuthConnector {

  val baseUrl: String = config.trustAuthUrl + "/trusts-auth"

  private val logger = Logger(getClass)

  override def agentIsAuthorised()
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse] = {
    http.GET[TrustAuthResponse](s"$baseUrl/agent-authorised").recoverWith {
      case _ =>
        logger.warn(s"[Session ID: ${utils.Session.id(hc)}] unable to authenticate agent due to an exception")
        Future.successful(TrustAuthInternalServerError)
    }
  }

  override def authorisedForUtr(utr: String)
                               (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse] = {
    http.GET[TrustAuthResponse](s"$baseUrl/authorised/$utr").recoverWith {
      case _ =>
        logger.warn(s"[Session ID: ${utils.Session.id(hc)}] unable to authenticate organisation for $utr due to an exception")
        Future.successful(TrustAuthInternalServerError)
    }
  }
}
