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

package connectors

import com.google.inject.ImplementedBy
import config.FrontendAppConfig
import javax.inject.Inject
import models.{TrustAuthInternalServerError, TrustAuthResponse}
import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TrustAuthConnectorImpl])
trait TrustAuthConnector {
  def agentIsAuthorised()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse]

  def authorisedForUtr(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse]
}

class TrustAuthConnectorImpl @Inject()(http: HttpClientV2, config: FrontendAppConfig)
  extends TrustAuthConnector with Logging {

  private val baseUrl: String = config.trustAuthUrl + "/trusts-auth"

  override def agentIsAuthorised()
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse] = {
    http.get(url"$baseUrl/agent-authorised").execute[TrustAuthResponse].recoverWith {
      case e =>
        logger.warn(s"[Session ID: ${utils.Session.id(hc)}] unable to authenticate agent due to an exception ${e.getMessage}")
        Future.successful(TrustAuthInternalServerError)
    }
  }

  override def authorisedForUtr(utr: String)
                               (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse] = {
    http.get(url"$baseUrl/authorised/$utr").execute[TrustAuthResponse].recoverWith {
      case e =>
        logger.warn(s"[Session ID: ${utils.Session.id(hc)}] unable to authenticate organisation for $utr due to an exception ${e.getMessage}")
        Future.successful(TrustAuthInternalServerError)
    }
  }
}
