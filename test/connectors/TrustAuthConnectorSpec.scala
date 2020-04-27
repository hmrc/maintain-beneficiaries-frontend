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

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.{AsyncFreeSpec, MustMatchers}
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global

class TrustAuthConnectorSpec extends AsyncFreeSpec with MustMatchers with WireMockHelper with DefaultAwaitTimeout{

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def authorisedUrlFor(utr: String): String = s"/trusts-auth/authorised/$utr"

  private def responseFromJson(json: JsValue) = {
    aResponse().withStatus(Status.OK).withBody(json.toString())
  }


  private def allowedResponse = responseFromJson(Json.obj())

  private def redirectResponse(redirectUrl: String) = responseFromJson(Json.obj("redirectUrl" -> redirectUrl))

  private def wiremock(utr: String, response: ResponseDefinitionBuilder) = {
    server.stubFor(get(urlEqualTo(authorisedUrlFor(utr))).willReturn(response))
  }

  lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts-auth.port" -> server.port(),
      "auditing.enabled" -> false
    ): _*).build()

  private lazy val connector = app.injector.instanceOf[TrustAuthConnector]

  private val utr = "0123456789"

  "TrustAuthConnector" - {

    "returns 'Allowed' when" - {
      "service returns with no redirect url" in {

        wiremock(utr, allowedResponse)

        connector.authorisedForUtr(utr) map { result =>
          result mustEqual TrustAuthAllowed
        }
      }
    }
    "returns 'Denied' when" - {
      "service returns a redirect url" in {

        wiremock(utr, redirectResponse("redirect-url"))

        connector.authorisedForUtr(utr) map { result =>
          result mustEqual TrustAuthDenied("redirect-url")
        }
      }
    }
    "returns 'Internal server error' when" - {
      "service returns something not OK" in {

        wiremock(utr, aResponse().withStatus(Status.INTERNAL_SERVER_ERROR))

        connector.authorisedForUtr(utr) map { result =>
          result mustEqual TrustAuthInternalServerError
        }
      }
    }
  }
}
