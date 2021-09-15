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

package models.beneficiaries

import models.{NonUkAddress, UkAddress}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

import java.time.LocalDate

class TrustBeneficiarySpec extends WordSpec with MustMatchers {

  "TrustBeneficiary" must {
    "deserialise from backend JSON" when {

      "taxable" when {

        "there is conflicting income info" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "1",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Nelson Ltd ",
              |  "beneficiaryDiscretion": true,
              |  "beneficiaryShareOfIncome": "0",
              |  "identification": {
              |    "safeId": "2222200000000"
              |  },
              |  "entityStart": "2017-02-28",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Nelson Ltd ",
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = Some(true),
            countryOfResidence = None,
            entityStart = LocalDate.of(2017, 2, 28),
            provisional = false
          )
        }

        "there is no discretion for income info" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "1",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Nelson Ltd ",
              |  "beneficiaryShareOfIncome": "10000",
              |  "identification": {
              |    "safeId": "2222200000000"
              |  },
              |  "entityStart": "2017-02-28",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Nelson Ltd ",
            utr = None,
            address = None,
            income = Some("10000"),
            incomeDiscretionYesNo = Some(false),
            countryOfResidence = None,
            entityStart = LocalDate.of(2017, 2, 28),
            provisional = false
          )
        }

        "there is a UTR" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "236",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Beneficiary Charity 25",
              |  "identification": {
              |    "utr": "2570719166"
              |  },
              |  "entityStart": "2019-09-23",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Beneficiary Charity 25",
            utr = Some("2570719166"),
            address = None,
            income = None,
            incomeDiscretionYesNo = Some(true),
            countryOfResidence = None,
            entityStart = LocalDate.of(2019, 9, 23),
            provisional = false
          )
        }

        "there is a UK address" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "1",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Nelson Ltd ",
              |  "beneficiaryShareOfIncome": "10000",
              |  "beneficiaryDiscretion": false,
              |  "identification": {
              |    "safeId": "2222200000000",
              |    "address": {
              |      "line1": "Suite 10",
              |      "line2": "Wealthy Arena",
              |      "line3": "Trafagar Square",
              |      "line4": "London",
              |      "postCode": "SE2 2HB",
              |      "country": "GB"
              |    }
              |  },
              |  "entityStart": "2017-02-28",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Nelson Ltd ",
            utr = None,
            address = Some(UkAddress(
              line1 = "Suite 10",
              line2 = "Wealthy Arena",
              line3 = Some("Trafagar Square"),
              line4 = Some("London"),
              postcode = "SE2 2HB"
            )),
            income = Some("10000"),
            incomeDiscretionYesNo = Some(false),
            countryOfResidence = None,
            entityStart = LocalDate.of(2017, 2, 28),
            provisional = false
          )
        }

        "there is a non-UK address" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "1",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Nelson Ltd ",
              |  "beneficiaryShareOfIncome": "10000",
              |  "beneficiaryDiscretion": false,
              |  "identification": {
              |    "safeId": "2222200000000",
              |    "address": {
              |      "line1": "Suite 10",
              |      "line2": "Wealthy Arena",
              |      "line3": "Paris",
              |      "country": "FR"
              |    }
              |  },
              |  "entityStart": "2017-02-28",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Nelson Ltd ",
            utr = None,
            address = Some(NonUkAddress(
              line1 = "Suite 10",
              line2 = "Wealthy Arena",
              line3 = Some("Paris"),
              country = "FR"
            )),
            income = Some("10000"),
            incomeDiscretionYesNo = Some(false),
            countryOfResidence = None,
            entityStart = LocalDate.of(2017, 2, 28),
            provisional = false
          )
        }

        "there is a country of residence" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "1",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Nelson Ltd ",
              |  "beneficiaryDiscretion": true,
              |  "identification": {
              |    "safeId": "2222200000000"
              |  },
              |  "countryOfResidence": "GB",
              |  "entityStart": "2017-02-28",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Nelson Ltd ",
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = Some(true),
            countryOfResidence = Some("GB"),
            entityStart = LocalDate.of(2017, 2, 28),
            provisional = false
          )
        }
      }

      "non-taxable" when {

        "there is no country of residence" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "1",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Nelson Ltd ",
              |  "identification": {
              |    "safeId": "2222200000000"
              |  },
              |  "entityStart": "2017-02-28",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Nelson Ltd ",
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = None,
            countryOfResidence = None,
            entityStart = LocalDate.of(2017, 2, 28),
            provisional = false
          )
        }

        "there is a country of residence" in {
          val json = Json.parse(
            """
              |{
              |  "lineNo": "1",
              |  "bpMatchStatus": "01",
              |  "organisationName": "Nelson Ltd ",
              |  "identification": {
              |    "safeId": "2222200000000"
              |  },
              |  "countryOfResidence": "GB",
              |  "entityStart": "2017-02-28",
              |  "provisional": false
              |}
              |""".stripMargin)

          val beneficiary = json.as[TrustBeneficiary]
          beneficiary mustBe TrustBeneficiary(
            name = "Nelson Ltd ",
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = None,
            countryOfResidence = Some("GB"),
            entityStart = LocalDate.of(2017, 2, 28),
            provisional = false
          )
        }
      }
    }
  }
}
