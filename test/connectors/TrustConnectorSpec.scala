/*
 * Copyright 2021 HM Revenue & Customs
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
import com.github.tomakehurst.wiremock.client.WireMock.{get, okJson, urlEqualTo, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import generators.Generators
import models.beneficiaries._
import models.{BeneficiaryType, Description, Name, RemoveBeneficiary, TrustDetails, TypeOfTrust}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

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

  private val trustsUrl: String = "/trusts"
  private val beneficiariesUrl: String = s"$trustsUrl/beneficiaries"

  private def getTrustDetailsUrl(utr: String) = s"$trustsUrl/$utr/trust-details"
  private def getBeneficiariesUrl(utr: String) = s"$beneficiariesUrl/$utr/transformed"
  private def addClassOfBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/add-unidentified/$utr"
  private def amendClassOfBeneficiaryUrl(utr: String, index: Int) = s"$beneficiariesUrl/amend-unidentified/$utr/$index"
  private def addIndividualBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/add-individual/$utr"
  private def amendIndividualBeneficiaryUrl(utr: String, index: Int) = s"$beneficiariesUrl/amend-individual/$utr/$index"
  private def addCharityBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/add-charity/$utr"
  private def amendCharityBeneficiaryUrl(utr: String, index: Int) = s"$beneficiariesUrl/amend-charity/$utr/$index"
  private def addTrustBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/add-trust/$utr"
  private def amendTrustBeneficiaryUrl(utr: String, index: Int) = s"$beneficiariesUrl/amend-trust/$utr/$index"
  private def addCompanyBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/add-company/$utr"
  private def amendCompanyBeneficiaryUrl(utr: String, index: Int) = s"$beneficiariesUrl/amend-company/$utr/$index"
  private def addEmploymentRelatedBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/add-large/$utr"
  private def amendEmploymentRelatedBeneficiaryUrl(utr: String, index: Int) = s"$beneficiariesUrl/amend-large/$utr/$index"
  private def addOtherBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/add-other/$utr"
  private def amendOtherBeneficiaryUrl(utr: String, index: Int) = s"$beneficiariesUrl/amend-other/$utr/$index"
  private def removeBeneficiaryUrl(utr: String) = s"$beneficiariesUrl/$utr/remove"

  private val individualBeneficiary = IndividualBeneficiary(
    name = Name("first", None, "last"),
    dateOfBirth = None,
    identification = None,
    address = None,
    vulnerableYesNo = false,
    roleInCompany = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2000-01-01"),
    provisional = false
  )

  private val unidentifiedBeneficiary = ClassOfBeneficiary(
    description = "Beneficiary Unidentified 25",
    entityStart = LocalDate.parse("2019-09-23"),
    provisional = false
  )

  private val companyBeneficiary = CompanyBeneficiary(
    name = "Company Ltd",
    utr = None,
    address = None,
    None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2019-09-23"),
    provisional = false
  )

  private val employmentRelatedBeneficiary = EmploymentRelatedBeneficiary(
    name = "Employment Related Endeavours",
    utr = None,
    address = None,
    description = Description("Description 1", None, None, None, None),
    howManyBeneficiaries = "501",
    entityStart = LocalDate.parse("2019-09-23"),
    provisional = false
  )

  private val trustBeneficiary = TrustBeneficiary(
    name = "Nelson Ltd ",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2017-02-28"),
    provisional = false
  )

  private val charityBeneficiary = CharityBeneficiary(
    name = "Humanitarian Endeavours Ltd",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val otherBeneficiary = OtherBeneficiary(
    description = "Other Endeavours Ltd",
    address = None,
    income = None,
    incomeDiscretionYesNo = true,
    entityStart = LocalDate.parse("2019-09-23"),
    provisional = false
  )

  "trust connector" when {

    "getTrustsDetails" in {

      val json = Json.parse(
        """
          |{
          | "startDate": "1920-03-28",
          | "lawCountry": "AD",
          | "administrationCountry": "GB",
          | "residentialStatus": {
          |   "uk": {
          |     "scottishLaw": false,
          |     "preOffShore": "AD"
          |   }
          | },
          | "typeOfTrust": "Will Trust or Intestacy Trust",
          | "deedOfVariation": "Previously there was only an absolute interest under the will",
          | "interVivos": false
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
        get(urlEqualTo(getTrustDetailsUrl(utr)))
          .willReturn(okJson(json.toString))
      )

      val processed = connector.getTrustDetails(utr)

      whenReady(processed) {
        r =>
          r mustBe TrustDetails(startDate = "1920-03-28", typeOfTrust = TypeOfTrust.WillTrustOrIntestacyTrust)
      }

    }

    "getBeneficiaries" when {

      "there are no beneficiaries" must {

        "return a default empty list beneficiaries" in {

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
            get(urlEqualTo(getBeneficiariesUrl(utr)))
              .willReturn(okJson(json.toString))
          )

          val processed = connector.getBeneficiaries(utr)

          whenReady(processed) {
            result =>
              result mustBe Beneficiaries(
                individualDetails = Nil,
                unidentified = Nil,
                company = Nil,
                employmentRelated = Nil,
                trust = Nil,
                charity = Nil,
                other = Nil)
          }

          application.stop()
        }
      }

      "there are beneficiaries" must {

        "parse the response and return the beneficiaries" in {

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
              |					},
              |         "provisional": false
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
              |         "vulnerableBeneficiary": false,
              |         "provisional": false
              |      }
              |    ],
              |    "unidentified": [
              |      {
              |        "lineNo": "311",
              |        "description": "Beneficiary Unidentified 25",
              |        "beneficiaryDiscretion": false,
              |        "beneficiaryShareOfIncome": "25",
              |        "entityStart": "2019-09-23",
              |        "provisional": false
              |      },
              |      {
              |         "lineNo": "309",
              |         "description": "Beneficiary Unidentified 23",
              |         "entityStart": "2019-09-23",
              |         "provisional": false
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
              |       "entityStart": "2017-02-28",
              |       "provisional": false
              |    }
              |  ],
              |  "company": [
              |   {
              |                "lineNo": "184",
              |                "bpMatchStatus": "01",
              |                "organisationName": "Company Ltd",
              |                "entityStart": "2019-09-23",
              |                "provisional": false
              |              }
              |  ],
              |  "large": [
              |  {
              |                "lineNo": "254",
              |                "bpMatchStatus": "01",
              |                "organisationName": "Employment Related Endeavours",
              |                "description": "Description 1",
              |                "numberOfBeneficiary": "501",
              |                "entityStart": "2019-09-23",
              |                "provisional": false
              |              }
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
              |       "entityStart": "2012-03-14",
              |       "provisional": false
              |    }
              |  ],
              |  "other": [
              |              {
              |                "lineNo": "286",
              |                "description": "Other Endeavours Ltd",
              |                "entityStart": "2019-09-23",
              |                "provisional": false
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
            get(urlEqualTo(getBeneficiariesUrl(utr)))
              .willReturn(okJson(json.toString))
          )

          val processed = connector.getBeneficiaries(utr)

          whenReady(processed) {
            result =>
              result mustBe Beneficiaries(
                individualDetails = List(individualBeneficiary),
                unidentified = List(
                  unidentifiedBeneficiary,
                  unidentifiedBeneficiary.copy(description = "Beneficiary Unidentified 23")
                ),
                company = List(companyBeneficiary),
                employmentRelated = List(employmentRelatedBeneficiary),
                trust = List(trustBeneficiary),
                charity = List(charityBeneficiary),
                other = List(otherBeneficiary)
              )
          }

          application.stop()
        }

      }
    }

    "addClassOfBeneficiary" must {

      val classOfBeneficiary = ClassOfBeneficiary(description, date, provisional = true)

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

        result.futureValue.status mustBe OK

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

        result.futureValue.status mustBe OK

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

    "addIndividualBeneficiary" must {

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
          post(urlEqualTo(addIndividualBeneficiaryUrl(utr)))
            .willReturn(ok)
        )

        val result = connector.addIndividualBeneficiary(utr, individualBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(addIndividualBeneficiaryUrl(utr)))
            .willReturn(badRequest)
        )

        val result = connector.addIndividualBeneficiary(utr, individualBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "amendIndividualBeneficiary" must {

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
          post(urlEqualTo(amendIndividualBeneficiaryUrl(utr, index)))
            .willReturn(ok)
        )

        val result = connector.amendIndividualBeneficiary(utr, index, individualBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(amendIndividualBeneficiaryUrl(utr, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendIndividualBeneficiary(utr, index, individualBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "addCharityBeneficiary" must {

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
          post(urlEqualTo(addCharityBeneficiaryUrl(utr)))
            .willReturn(ok)
        )

        val result = connector.addCharityBeneficiary(utr, charityBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(addCharityBeneficiaryUrl(utr)))
            .willReturn(badRequest)
        )

        val result = connector.addCharityBeneficiary(utr, charityBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "amendCharityBeneficiary" must {

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
          post(urlEqualTo(amendCharityBeneficiaryUrl(utr, index)))
            .willReturn(ok)
        )

        val result = connector.amendCharityBeneficiary(utr, index, charityBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(amendCharityBeneficiaryUrl(utr, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendCharityBeneficiary(utr, index, charityBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "addCompanyBeneficiary" must {

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
          post(urlEqualTo(addCompanyBeneficiaryUrl(utr)))
            .willReturn(ok)
        )

        val result = connector.addCompanyBeneficiary(utr, companyBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(addCompanyBeneficiaryUrl(utr)))
            .willReturn(badRequest)
        )

        val result = connector.addCompanyBeneficiary(utr, companyBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "amendCompanyBeneficiary" must {

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
          post(urlEqualTo(amendCompanyBeneficiaryUrl(utr, index)))
            .willReturn(ok)
        )

        val result = connector.amendCompanyBeneficiary(utr, index, companyBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(amendCompanyBeneficiaryUrl(utr, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendCompanyBeneficiary(utr, index, companyBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "addEmploymentRelatedBeneficiary" must {

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
          post(urlEqualTo(addEmploymentRelatedBeneficiaryUrl(utr)))
            .willReturn(ok)
        )

        val result = connector.addEmploymentRelatedBeneficiary(utr, employmentRelatedBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(addEmploymentRelatedBeneficiaryUrl(utr)))
            .willReturn(badRequest)
        )

        val result = connector.addEmploymentRelatedBeneficiary(utr, employmentRelatedBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "amendEmploymentRelatedBeneficiary" must {

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
          post(urlEqualTo(amendEmploymentRelatedBeneficiaryUrl(utr, index)))
            .willReturn(ok)
        )

        val result = connector.amendEmploymentRelatedBeneficiary(utr, index, employmentRelatedBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(amendEmploymentRelatedBeneficiaryUrl(utr, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendEmploymentRelatedBeneficiary(utr, index, employmentRelatedBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "addTrustBeneficiary" must {

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
          post(urlEqualTo(addTrustBeneficiaryUrl(utr)))
            .willReturn(ok)
        )

        val result = connector.addTrustBeneficiary(utr, trustBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(addTrustBeneficiaryUrl(utr)))
            .willReturn(badRequest)
        )

        val result = connector.addTrustBeneficiary(utr, trustBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "amendTrustBeneficiary" must {

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
          post(urlEqualTo(amendTrustBeneficiaryUrl(utr, index)))
            .willReturn(ok)
        )

        val result = connector.amendTrustBeneficiary(utr, index, trustBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(amendTrustBeneficiaryUrl(utr, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendTrustBeneficiary(utr, index, trustBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "addOtherBeneficiary" must {

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
          post(urlEqualTo(addOtherBeneficiaryUrl(utr)))
            .willReturn(ok)
        )

        val result = connector.addOtherBeneficiary(utr, otherBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(addOtherBeneficiaryUrl(utr)))
            .willReturn(badRequest)
        )

        val result = connector.addOtherBeneficiary(utr, otherBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "amendOtherBeneficiary" must {

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
          post(urlEqualTo(amendOtherBeneficiaryUrl(utr, index)))
            .willReturn(ok)
        )

        val result = connector.amendOtherBeneficiary(utr, index, otherBeneficiary)

        result.futureValue.status mustBe OK

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
          post(urlEqualTo(amendOtherBeneficiaryUrl(utr, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendOtherBeneficiary(utr, index, otherBeneficiary)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }

    "removeBeneficiary" must {

      def removeBeneficiary(beneficiaryType: BeneficiaryType): RemoveBeneficiary = RemoveBeneficiary(beneficiaryType, index, date)

      "Return OK when the request is successful" in {

        forAll(arbitraryBeneficiaryType) {
          beneficiaryType =>

            val application = applicationBuilder()
              .configure(
                Seq(
                  "microservice.services.trusts.port" -> server.port(),
                  "auditing.enabled" -> false
                ): _*
              ).build()

            val connector = application.injector.instanceOf[TrustConnector]

            server.stubFor(
              put(urlEqualTo(removeBeneficiaryUrl(utr)))
                .willReturn(ok)
            )

            val result = connector.removeBeneficiary(utr, removeBeneficiary(beneficiaryType))

            result.futureValue.status mustBe OK

            application.stop()
        }
      }

      "return Bad Request when the request is unsuccessful" in {

        forAll(arbitraryBeneficiaryType) {
          beneficiaryType =>

            val application = applicationBuilder()
              .configure(
                Seq(
                  "microservice.services.trusts.port" -> server.port(),
                  "auditing.enabled" -> false
                ): _*
              ).build()

            val connector = application.injector.instanceOf[TrustConnector]

            server.stubFor(
              put(urlEqualTo(removeBeneficiaryUrl(utr)))
                .willReturn(badRequest)
            )

            val result = connector.removeBeneficiary(utr, removeBeneficiary(beneficiaryType))

            result.map(response => response.status mustBe BAD_REQUEST)

            application.stop()
        }
      }

    }

  }
}
