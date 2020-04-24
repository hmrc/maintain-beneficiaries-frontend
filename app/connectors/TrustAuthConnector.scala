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
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

case class TrustAuthResponseBody(redirectUrl: Option[String] = None)

object TrustAuthResponseBody {
  implicit val format: Format[TrustAuthResponseBody] = Json.format[TrustAuthResponseBody]
}

sealed trait TrustAuthResponse

object TrustAuthAllowed extends TrustAuthResponse
case class TrustAuthDenied(redirectUrl: String) extends TrustAuthResponse
object TrustAuthInternalServerError extends TrustAuthResponse

@ImplementedBy(classOf[TrustAuthConnectorImpl])
trait TrustAuthConnector {
  def authorisedForUtr(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse]
}

class TrustAuthConnectorImpl @Inject()(http: HttpClient, config: FrontendAppConfig)
  extends TrustAuthConnector {

  val baseUrl: String = config.trustAuthUrl + "/trusts-auth/authorised/"

  override def authorisedForUtr(utr: String)
                               (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustAuthResponse] = {
    http.GET[TrustAuthResponseBody](baseUrl + utr).map {
      case TrustAuthResponseBody(Some(redirectUrl)) => TrustAuthDenied(redirectUrl)
      case _ => TrustAuthAllowed
    }.recoverWith {
      case _ => Future.successful(TrustAuthInternalServerError)
    }
  }
}
