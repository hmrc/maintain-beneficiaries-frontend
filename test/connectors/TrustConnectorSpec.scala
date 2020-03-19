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

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import models.beneficiaries.ClassOfBeneficiary
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

class TrustConnectorSpec extends SpecBase with Generators with ScalaFutures
  with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def amendClassOfBeneficiaryUrl(utr: String, index: Int) = s"/trusts/amend-unidentified-beneficiary/$utr/$index"
  private def addClassOfBeneficiaryUrl(utr: String) = s"/trusts/add-unidentified-beneficiary/$utr"

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

  val utr = "1000000008"
  val index = 0
  val description = "description"
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "TrustConnector amendClassOfBeneficiary" must {

    "Return OK when the request is successful" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        post(urlEqualTo(amendClassOfBeneficiaryUrl(utr, index)))
          .willReturn(ok)
      )

      val result = connector.amendClassOfBeneficiary(utr, index, description)

      result.futureValue.status mustBe (OK)

      application.stop()
    }

    "return Bad Request when the request is unsuccessful" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        post(urlEqualTo(amendClassOfBeneficiaryUrl(utr, index)))
          .willReturn(badRequest)
      )

      val result = connector.amendClassOfBeneficiary(utr, index, description)

      result.map(response => response.status mustBe BAD_REQUEST)

      application.stop()
    }

  }

  "TrustConnector addClassOfBeneficiary" must {

    val classOfBeneficiary = ClassOfBeneficiary(description, date)

    "Return OK when the request is successful" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        post(urlEqualTo(addClassOfBeneficiaryUrl(utr)))
          .willReturn(ok)
      )

      val result = connector.addClassOfBeneficiary(utr, classOfBeneficiary)

      result.futureValue.status mustBe (OK)

      application.stop()
    }

    "return Bad Request when the request is unsuccessful" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustConnector]

      server.stubFor(
        post(urlEqualTo(addClassOfBeneficiaryUrl(utr)))
          .willReturn(badRequest)
      )

      val result = connector.addClassOfBeneficiary(utr, classOfBeneficiary)

      result.map(response => response.status mustBe BAD_REQUEST)

      application.stop()
    }

  }

}
