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

import models.Name
import models.beneficiaries.{Beneficiaries, CharityBeneficiary, ClassOfBeneficiary, IndividualBeneficiary, OtherBeneficiary, TrustBeneficiary}
import play.api.libs.json.Json
import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{get, okJson, urlEqualTo}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.test.Helpers._
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

  val utr = "1000000008"
  val index = 0
  val description = "description"
  val date: LocalDate = LocalDate.parse("2019-02-03")

  private def amendClassOfBeneficiaryUrl(utr: String, index: Int) = s"/trusts/amend-unidentified-beneficiary/$utr/$index"
  private def addClassOfBeneficiaryUrl(utr: String) = s"/trusts/add-unidentified-beneficiary/$utr"

  "trust connector" when {

    "get beneficiaries returns a trust with empty lists" must {

      "return a default empty list beneficiaries" in {

        val utr = "1000000008"

        val json = Json.parse(
          """
            |{
            | "beneficiary": {
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
            result mustBe Beneficiaries(
              individualDetails = Nil,
              unidentified = Nil,
              company = Nil,
              trust = Nil,
              charity = Nil,
              other = Nil)
        }

        application.stop()
      }

    }

    "get beneficiaries" must {

      "parse the response and return the beneficiaries" in {
        val utr = "1000000008"

        val json = Json.parse(
          """
            |{
            | "beneficiary": {
            |   "charity": [
            |				{
            |					"lineNo": "1",
            |					"bpMatchStatus": "01",
            |					"entityStart": "2019-02-28",
            |					"organisationName": "1234567890 QwErTyUiOp ,.(/)&'- name",
            |					"beneficiaryDiscretion": false,
            |					"beneficiaryShareOfIncome": "100",
            |					"identification": {
            |						"address": {
            |							"line1": "1234567890 QwErTyUiOp ,.(/)&'- name",
            |							"line2": "1234567890 QwErTyUiOp ,.(/)&'- name",
            |							"line3": "1234567890 QwErTyUiOp ,.(/)&'- name",
            |							"country": "DE"
            |						}
            |					}
            |				}
            |			],
            |   "individualDetails": [
            |     {
            |         "lineNo": "7",
            |         "bpMatchStatus": "01",
            |         "entityStart": "2000-01-01",
            |         "name": {
            |           "firstName": "first",
            |           "lastName": "last"
            |         },
            |         "vulnerableBeneficiary": false
            |      }
            |    ],
            |    "unidentified": [
            |      {
            |        "lineNo": "311",
            |        "description": "Beneficiary Unidentified 25",
            |        "beneficiaryDiscretion": false,
            |        "beneficiaryShareOfIncome": "25",
            |        "entityStart": "2019-09-23"
            |      },
            |      {
            |         "lineNo": "309",
            |         "description": "Beneficiary Unidentified 23",
            |         "entityStart": "2019-09-23"
            |       }
            |  ],
            |  "trust": [
            |    {
            |       "lineNo": "1",
            |       "bpMatchStatus": "01",
            |       "organisationName": "Nelson Ltd ",
            |       "beneficiaryDiscretion": true,
            |       "beneficiaryShareOfIncome": "0",
            |       "identification": {
            |         "safeId": "2222200000000"
            |       },
            |       "entityStart": "2017-02-28"
            |    }
            |  ],
            |  "charity": [
            |    {
            |       "lineNo": "1",
            |       "bpMatchStatus": "01",
            |       "organisationName": "Humanitarian Endeavours Ltd",
            |       "beneficiaryDiscretion": true,
            |       "beneficiaryShareOfIncome": "0",
            |       "identification": {
            |         "safeId": "2222200000000"
            |       },
            |       "entityStart": "2012-03-14"
            |    }
            |  ],
            |  "other": [
            |              {
            |                "lineNo": "286",
            |                "description": "Other Endeavours Ltd",
            |                "entityStart": "2019-09-23"
            |              }
            |              ]
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
            result mustBe Beneficiaries(
              individualDetails = List(
                IndividualBeneficiary(
                  name = Name("first", None, "last"),
                  dateOfBirth = None,
                  nationalInsuranceNumber = None,
                  address = None,
                  vulnerableYesNo = false,
                  income = None,
                  incomeDiscretionYesNo = true,
                  entityStart = LocalDate.parse("2000-01-01")
                )
              ),
              unidentified = List(
                ClassOfBeneficiary(
                  description = "Beneficiary Unidentified 25",
                  entityStart = LocalDate.parse("2019-09-23")
                ),
                ClassOfBeneficiary(
                  description = "Beneficiary Unidentified 23",
                  entityStart = LocalDate.parse("2019-09-23")
                )
              ),
              company = Nil,
              trust = List(
                TrustBeneficiary(
                  name = "Nelson Ltd ",
                  address = None,
                  income = None,
                  incomeDiscretionYesNo = true,
                  entityStart = LocalDate.parse("2017-02-28")
                )
              ),
              charity = List(
                CharityBeneficiary(
                  name = "Humanitarian Endeavours Ltd",
                  utr = None,
                  address = None,
                  income = None,
                  incomeDiscretionYesNo = true,
                  entityStart = LocalDate.parse("2012-03-14")
                )
              ),
              other = List(
                OtherBeneficiary(
                  description = "Other Endeavours Ltd",
                  address = None,
                  income = None,
                  incomeDiscretionYesNo = true,
                  entityStart = LocalDate.parse("2019-09-23")
                )
              )
            )
        }

        application.stop()
      }

    }

    "amendClassOfBeneficiary" must {

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

    "addClassOfBeneficiary" must {

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

}
