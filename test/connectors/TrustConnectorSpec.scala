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

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{get, okJson, urlEqualTo}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import models.beneficiaries.Beneficiaries
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

class TrustConnectorSpec extends SpecBase with Generators with ScalaFutures
  with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }


  "trust connector" when {

    "get beneficiaries with no individual beneficiaries" must {

      "return a default empty list of individual beneficiaries" in {

        val utr = "1000000008"

        val json = Json.parse(
          """
            |{
            | "beneficiary": {
            |   "charity":[{"lineNo":"1","bpMatchStatus":"01","entityStart":"2019-02-28","organisationName":"1234567890 QwErTyUiOp ,.(/)&'- name","beneficiaryDiscretion":false,"beneficiaryShareOfIncome":"100","identification":{"address":{"line1":"1234567890 QwErTyUiOp ,.(/)&'- name","line2":"1234567890 QwErTyUiOp ,.(/)&'- name","line3":"1234567890 QwErTyUiOp ,.(/)&'- name","country":"DE"}}}]
            | }
            |}
            |""".stripMargin)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          get(urlEqualTo(s"/trusts/$utr/transformed/beneficiaries"))
            .willReturn(okJson(json.toString))
        )

        val processed = connector.getBeneficiaries(utr)

        whenReady(processed) {
          result =>
            result mustBe Beneficiaries(Nil)
        }

        application.stop()
      }

    }

  }

}
