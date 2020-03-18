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

package models.beneficiaries

import java.time.LocalDate

import models.{Name, NonUkAddress, UkAddress}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class CompanyBeneficiarySpec extends WordSpec with MustMatchers {
  "CompanyBeneficiary" must {
    "deserialise from backend JSON" when {
      "with UK address" in {

        val json = Json.parse(
          """{
            |   "lineNo": "185",
            |   "bpMatchStatus": "01",
            |   "organisationName": "Beneficiary Org 24",
            |    "identification": {
            |       "safeId": "2222200000000",
            |       "address": {
            |          "line1": "Suite 10",
            |          "line2": "Wealthy Arena",
            |          "line3": "Trafagar Square",
            |          "line4": "London",
            |          "postCode": "SE2 2HB",
            |          "country": "GB"
            |       }
            |    },
            |    "beneficiaryDiscretion": false,
            |    "beneficiaryShareOfIncome": "98",
            |    "entityStart": "2019-09-23"
            |    }""".stripMargin)

        val beneficiary = json.as[CompanyBeneficiary]

        beneficiary mustBe CompanyBeneficiary(
          name = "Beneficiary Org 24",
          utr = None,
          address = Some(UkAddress(
            "Suite 10",
            "Wealthy Arena",
            Some("Trafagar Square"),
            Some("London"),
            "SE2 2HB"
          )),
          income = Some("98"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "with foreign address" in {

        val json = Json.parse(
          """{
            |   "lineNo": "185",
            |   "bpMatchStatus": "01",
            |   "organisationName": "Beneficiary Org 24",
            |    "identification": {
            |       "safeId": "2222200000000",
            |       "address": {
            |          "line1": "123 Sesame Street",
            |          "line2": "Hollywood, CA",
            |          "line3": "314159",
            |          "country": "US"
            |       }
            |    },
            |    "beneficiaryDiscretion": false,
            |    "beneficiaryShareOfIncome": "98",
            |    "entityStart": "2019-09-23"
            |    }""".stripMargin)

        val beneficiary = json.as[CompanyBeneficiary]

        beneficiary mustBe CompanyBeneficiary(
          name = "Beneficiary Org 24",
          utr = None,
          address = Some(NonUkAddress(
            "123 Sesame Street",
            "Hollywood, CA",
            Some("314159"),
            "US"
          )),
          income = Some("98"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "with utr" in {
        val json = Json.parse(
          """
            |{
            |                "lineNo": "185",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Org 24",
            |                "identification": {
            |                  "utr": "2570719121"
            |                },
            |                "beneficiaryDiscretion": false,
            |                "beneficiaryShareOfIncome": "98",
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[CompanyBeneficiary]

        beneficiary mustBe CompanyBeneficiary(
          name = "Beneficiary Org 24",
          utr = Some("2570719121"),
          address = None,
          income = Some("98"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }
      "with no identification" in {
        val json = Json.parse(
          """
            |{
            |                "lineNo": "185",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Org 24",
            |                "beneficiaryDiscretion": false,
            |                "beneficiaryShareOfIncome": "98",
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[CompanyBeneficiary]

        beneficiary mustBe CompanyBeneficiary(
          name = "Beneficiary Org 24",
          utr = None,
          address = None,
          income = Some("98"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "implying incomeDiscretionYesNo" in {
        val json = Json.parse(
          """
            |{
            |                "lineNo": "185",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Org 24",
            |                "identification": {
            |                  "utr": "2570719121"
            |                },
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[CompanyBeneficiary]

        beneficiary mustBe CompanyBeneficiary(
          name = "Beneficiary Org 24",
          utr = Some("2570719121"),
          address = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }
    }
  }
}