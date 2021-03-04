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
import play.api.libs.json.{JsBoolean, Json}
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

  val identifier = "1000000008"
  val index = 0
  val description = "description"
  val date: LocalDate = LocalDate.parse("2019-02-03")

  private val trustsUrl: String = "/trusts"
  private val beneficiariesUrl: String = s"$trustsUrl/beneficiaries"

  private def getTrustDetailsUrl(identifier: String) = s"$trustsUrl/$identifier/trust-details"
  private def getBeneficiariesUrl(identifier: String) = s"$beneficiariesUrl/$identifier/transformed"
  private def addClassOfBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/add-unidentified/$identifier"
  private def amendClassOfBeneficiaryUrl(identifier: String, index: Int) = s"$beneficiariesUrl/amend-unidentified/$identifier/$index"
  private def addIndividualBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/add-individual/$identifier"
  private def amendIndividualBeneficiaryUrl(identifier: String, index: Int) = s"$beneficiariesUrl/amend-individual/$identifier/$index"
  private def addCharityBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/add-charity/$identifier"
  private def amendCharityBeneficiaryUrl(identifier: String, index: Int) = s"$beneficiariesUrl/amend-charity/$identifier/$index"
  private def addTrustBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/add-trust/$identifier"
  private def amendTrustBeneficiaryUrl(identifier: String, index: Int) = s"$beneficiariesUrl/amend-trust/$identifier/$index"
  private def addCompanyBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/add-company/$identifier"
  private def amendCompanyBeneficiaryUrl(identifier: String, index: Int) = s"$beneficiariesUrl/amend-company/$identifier/$index"
  private def addEmploymentRelatedBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/add-large/$identifier"
  private def amendEmploymentRelatedBeneficiaryUrl(identifier: String, index: Int) = s"$beneficiariesUrl/amend-large/$identifier/$index"
  private def addOtherBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/add-other/$identifier"
  private def amendOtherBeneficiaryUrl(identifier: String, index: Int) = s"$beneficiariesUrl/amend-other/$identifier/$index"
  private def removeBeneficiaryUrl(identifier: String) = s"$beneficiariesUrl/$identifier/remove"
  private def isTrust5mldUrl(identifier: String) = s"$trustsUrl/$identifier/is-trust-5mld"

  private val individualBeneficiary = IndividualBeneficiary(
    name = Name("first", None, "last"),
    dateOfBirth = None,
    identification = None,
    address = None,
    vulnerableYesNo = Some(false),
    roleInCompany = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
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
    income = None,
    incomeDiscretionYesNo = Some(true),
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
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2017-02-28"),
    provisional = false
  )

  private val charityBeneficiary = CharityBeneficiary(
    name = "Humanitarian Endeavours Ltd",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val otherBeneficiary = OtherBeneficiary(
    description = "Other Endeavours Ltd",
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
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
        get(urlEqualTo(getTrustDetailsUrl(identifier)))
          .willReturn(okJson(json.toString))
      )

      val processed = connector.getTrustDetails(identifier)

      whenReady(processed) {
        r =>
          r mustBe TrustDetails(startDate = "1920-03-28", typeOfTrust = Some(TypeOfTrust.WillTrustOrIntestacyTrust), trustTaxable = None)
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
            get(urlEqualTo(getBeneficiariesUrl(identifier)))
              .willReturn(okJson(json.toString))
          )

          val processed = connector.getBeneficiaries(identifier)

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
              |  "beneficiary": {
              |    "individualDetails": [
              |      {
              |        "lineNo": "7",
              |        "bpMatchStatus": "01",
              |        "entityStart": "2000-01-01",
              |        "name": {
              |          "firstName": "first",
              |          "lastName": "last"
              |        },
              |        "vulnerableBeneficiary": false,
              |        "provisional": false
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
              |        "lineNo": "309",
              |        "description": "Beneficiary Unidentified 23",
              |        "entityStart": "2019-09-23",
              |        "provisional": false
              |      }
              |    ],
              |    "trust": [
              |      {
              |        "lineNo": "1",
              |        "bpMatchStatus": "01",
              |        "organisationName": "Nelson Ltd ",
              |        "beneficiaryDiscretion": true,
              |        "beneficiaryShareOfIncome": "0",
              |        "identification": {
              |          "safeId": "2222200000000"
              |        },
              |        "entityStart": "2017-02-28",
              |        "provisional": false
              |      }
              |    ],
              |    "company": [
              |      {
              |        "lineNo": "184",
              |        "bpMatchStatus": "01",
              |        "organisationName": "Company Ltd",
              |        "beneficiaryDiscretion": true,
              |        "entityStart": "2019-09-23",
              |        "provisional": false
              |      }
              |    ],
              |    "large": [
              |      {
              |        "lineNo": "254",
              |        "bpMatchStatus": "01",
              |        "organisationName": "Employment Related Endeavours",
              |        "description": "Description 1",
              |        "numberOfBeneficiary": "501",
              |        "entityStart": "2019-09-23",
              |        "provisional": false
              |      }
              |    ],
              |    "charity": [
              |      {
              |        "lineNo": "1",
              |        "bpMatchStatus": "01",
              |        "organisationName": "Humanitarian Endeavours Ltd",
              |        "beneficiaryDiscretion": true,
              |        "beneficiaryShareOfIncome": "0",
              |        "identification": {
              |          "safeId": "2222200000000"
              |        },
              |        "entityStart": "2012-03-14",
              |        "provisional": false
              |      }
              |    ],
              |    "other": [
              |      {
              |        "lineNo": "286",
              |        "description": "Other Endeavours Ltd",
              |        "beneficiaryDiscretion": true,
              |        "entityStart": "2019-09-23",
              |        "provisional": false
              |      }
              |    ]
              |  }
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
            get(urlEqualTo(getBeneficiariesUrl(identifier)))
              .willReturn(okJson(json.toString))
          )

          val processed = connector.getBeneficiaries(identifier)

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
          post(urlEqualTo(addClassOfBeneficiaryUrl(identifier)))
            .willReturn(ok)
        )

        val result = connector.addClassOfBeneficiary(identifier, classOfBeneficiary)

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
          post(urlEqualTo(addClassOfBeneficiaryUrl(identifier)))
            .willReturn(badRequest)
        )

        val result = connector.addClassOfBeneficiary(identifier, classOfBeneficiary)

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
          post(urlEqualTo(amendClassOfBeneficiaryUrl(identifier, index)))
            .willReturn(ok)
        )

        val result = connector.amendClassOfBeneficiary(identifier, index, description)

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
          post(urlEqualTo(amendClassOfBeneficiaryUrl(identifier, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendClassOfBeneficiary(identifier, index, description)

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
          post(urlEqualTo(addIndividualBeneficiaryUrl(identifier)))
            .willReturn(ok)
        )

        val result = connector.addIndividualBeneficiary(identifier, individualBeneficiary)

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
          post(urlEqualTo(addIndividualBeneficiaryUrl(identifier)))
            .willReturn(badRequest)
        )

        val result = connector.addIndividualBeneficiary(identifier, individualBeneficiary)

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
          post(urlEqualTo(amendIndividualBeneficiaryUrl(identifier, index)))
            .willReturn(ok)
        )

        val result = connector.amendIndividualBeneficiary(identifier, index, individualBeneficiary)

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
          post(urlEqualTo(amendIndividualBeneficiaryUrl(identifier, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendIndividualBeneficiary(identifier, index, individualBeneficiary)

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
          post(urlEqualTo(addCharityBeneficiaryUrl(identifier)))
            .willReturn(ok)
        )

        val result = connector.addCharityBeneficiary(identifier, charityBeneficiary)

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
          post(urlEqualTo(addCharityBeneficiaryUrl(identifier)))
            .willReturn(badRequest)
        )

        val result = connector.addCharityBeneficiary(identifier, charityBeneficiary)

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
          post(urlEqualTo(amendCharityBeneficiaryUrl(identifier, index)))
            .willReturn(ok)
        )

        val result = connector.amendCharityBeneficiary(identifier, index, charityBeneficiary)

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
          post(urlEqualTo(amendCharityBeneficiaryUrl(identifier, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendCharityBeneficiary(identifier, index, charityBeneficiary)

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
          post(urlEqualTo(addCompanyBeneficiaryUrl(identifier)))
            .willReturn(ok)
        )

        val result = connector.addCompanyBeneficiary(identifier, companyBeneficiary)

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
          post(urlEqualTo(addCompanyBeneficiaryUrl(identifier)))
            .willReturn(badRequest)
        )

        val result = connector.addCompanyBeneficiary(identifier, companyBeneficiary)

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
          post(urlEqualTo(amendCompanyBeneficiaryUrl(identifier, index)))
            .willReturn(ok)
        )

        val result = connector.amendCompanyBeneficiary(identifier, index, companyBeneficiary)

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
          post(urlEqualTo(amendCompanyBeneficiaryUrl(identifier, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendCompanyBeneficiary(identifier, index, companyBeneficiary)

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
          post(urlEqualTo(addEmploymentRelatedBeneficiaryUrl(identifier)))
            .willReturn(ok)
        )

        val result = connector.addEmploymentRelatedBeneficiary(identifier, employmentRelatedBeneficiary)

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
          post(urlEqualTo(addEmploymentRelatedBeneficiaryUrl(identifier)))
            .willReturn(badRequest)
        )

        val result = connector.addEmploymentRelatedBeneficiary(identifier, employmentRelatedBeneficiary)

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
          post(urlEqualTo(amendEmploymentRelatedBeneficiaryUrl(identifier, index)))
            .willReturn(ok)
        )

        val result = connector.amendEmploymentRelatedBeneficiary(identifier, index, employmentRelatedBeneficiary)

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
          post(urlEqualTo(amendEmploymentRelatedBeneficiaryUrl(identifier, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendEmploymentRelatedBeneficiary(identifier, index, employmentRelatedBeneficiary)

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
          post(urlEqualTo(addTrustBeneficiaryUrl(identifier)))
            .willReturn(ok)
        )

        val result = connector.addTrustBeneficiary(identifier, trustBeneficiary)

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
          post(urlEqualTo(addTrustBeneficiaryUrl(identifier)))
            .willReturn(badRequest)
        )

        val result = connector.addTrustBeneficiary(identifier, trustBeneficiary)

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
          post(urlEqualTo(amendTrustBeneficiaryUrl(identifier, index)))
            .willReturn(ok)
        )

        val result = connector.amendTrustBeneficiary(identifier, index, trustBeneficiary)

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
          post(urlEqualTo(amendTrustBeneficiaryUrl(identifier, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendTrustBeneficiary(identifier, index, trustBeneficiary)

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
          post(urlEqualTo(addOtherBeneficiaryUrl(identifier)))
            .willReturn(ok)
        )

        val result = connector.addOtherBeneficiary(identifier, otherBeneficiary)

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
          post(urlEqualTo(addOtherBeneficiaryUrl(identifier)))
            .willReturn(badRequest)
        )

        val result = connector.addOtherBeneficiary(identifier, otherBeneficiary)

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
          post(urlEqualTo(amendOtherBeneficiaryUrl(identifier, index)))
            .willReturn(ok)
        )

        val result = connector.amendOtherBeneficiary(identifier, index, otherBeneficiary)

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
          post(urlEqualTo(amendOtherBeneficiaryUrl(identifier, index)))
            .willReturn(badRequest)
        )

        val result = connector.amendOtherBeneficiary(identifier, index, otherBeneficiary)

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
              put(urlEqualTo(removeBeneficiaryUrl(identifier)))
                .willReturn(ok)
            )

            val result = connector.removeBeneficiary(identifier, removeBeneficiary(beneficiaryType))

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
              put(urlEqualTo(removeBeneficiaryUrl(identifier)))
                .willReturn(badRequest)
            )

            val result = connector.removeBeneficiary(identifier, removeBeneficiary(beneficiaryType))

            result.map(response => response.status mustBe BAD_REQUEST)

            application.stop()
        }
      }

    }

    "isTrust5mld" must {

      "return true" when {
        "untransformed data is 5mld" in {

          val json = JsBoolean(true)

          val application = applicationBuilder()
            .configure(
              Seq(
                "microservice.services.trusts.port" -> server.port(),
                "auditing.enabled" -> false
              ): _*
            ).build()

          val connector = application.injector.instanceOf[TrustConnector]

          server.stubFor(
            get(urlEqualTo(isTrust5mldUrl(identifier)))
              .willReturn(okJson(json.toString))
          )

          val processed = connector.isTrust5mld(identifier)

          whenReady(processed) {
            r =>
              r mustBe true
          }
        }
      }

      "return false" when {
        "untransformed data is 4mld" in {

          val json = JsBoolean(false)

          val application = applicationBuilder()
            .configure(
              Seq(
                "microservice.services.trusts.port" -> server.port(),
                "auditing.enabled" -> false
              ): _*
            ).build()

          val connector = application.injector.instanceOf[TrustConnector]

          server.stubFor(
            get(urlEqualTo(isTrust5mldUrl(identifier)))
              .willReturn(okJson(json.toString))
          )

          val processed = connector.isTrust5mld(identifier)

          whenReady(processed) {
            r =>
              r mustBe false
          }
        }
      }
    }

  }
}
