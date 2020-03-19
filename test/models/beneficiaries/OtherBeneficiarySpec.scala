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

import models.{NonUkAddress, UkAddress}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class OtherBeneficiarySpec extends WordSpec with MustMatchers {
  
  "OtherBeneficiary" must {
    "deserialise from backend JSON" when {
      "with UK address" in {

        val json = Json.parse(
          """{
            |                "lineNo": "262",
            |                "description": "Beneficiary Other 1",
            |                "address": {
            |                  "line1": "Ben Other Line 1",
            |                  "line2": "Ben Other Line 2",
            |                  "line3": "Ben Other Line 3",
            |                  "line4": "Ben Other Line 4",
            |                  "postCode": "AB1 2BA",
            |                  "country": "GB"
            |                },
            |                "entityStart": "2019-09-23"
            |              }""".stripMargin)

        val beneficiary = json.as[OtherBeneficiary]

        beneficiary mustBe OtherBeneficiary(
          description = "Beneficiary Other 1",
          address = Some(UkAddress(
            "Ben Other Line 1",
            "Ben Other Line 2",
            Some("Ben Other Line 3"),
            Some("Ben Other Line 4"),
            "AB1 2BA"
          )),
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "with foreign address" in {

        val json = Json.parse(
          """{
            |                "lineNo": "263",
            |                "description": "Beneficiary Other 24",
            |                "address": {
            |                  "line1": "Ben Other Line 1",
            |                  "line2": "Ben Other Line 2",
            |                  "line3": "Ben Other Line 3",
            |                  "country": "RU"
            |                },
            |                "entityStart": "2019-09-23"
            |              }""".stripMargin)

        val beneficiary = json.as[OtherBeneficiary]

        beneficiary mustBe OtherBeneficiary(
          description = "Beneficiary Other 24",
          address = Some(NonUkAddress(
            "Ben Other Line 1",
            "Ben Other Line 2",
            Some("Ben Other Line 3"),
            "RU"
          )),
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "with no identification" in {
        val json = Json.parse(
          """{
            |                "lineNo": "274",
            |                "description": "Beneficiary Other 13",
            |                "beneficiaryDiscretion": false,
            |                "beneficiaryShareOfIncome": "20",
            |                "entityStart": "2019-09-23"
            |              }""".stripMargin)

        val beneficiary = json.as[OtherBeneficiary]

        beneficiary mustBe OtherBeneficiary(
          description = "Beneficiary Other 13",
          address = None,
          income = Some("20"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "there is conflicting income info" in {
        val json = Json.parse(
          """
            |{
            |                "lineNo": "276",
            |                "description": "Beneficiary Other 15",
            |                "entityStart": "2019-09-23",
            |                "beneficiaryDiscretion": true,
            |                "beneficiaryShareOfIncome": "0"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[OtherBeneficiary]
        beneficiary mustBe OtherBeneficiary(
          description = "Beneficiary Other 15",
          address = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }
      "there is no discretion for income info" in {
        val json = Json.parse(
          """{
            |                "lineNo": "275",
            |                "description": "Beneficiary Other 14",
            |                "entityStart": "2019-09-23",
            |                "beneficiaryDiscretion": false,
            |                "beneficiaryShareOfIncome": "20"
            |              }""".stripMargin)

        val beneficiary = json.as[OtherBeneficiary]
        beneficiary mustBe OtherBeneficiary(
          description = "Beneficiary Other 14",
          address = None,
          income = Some("20"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }
      "there is no income at all" in {
        val json = Json.parse(
          """{
            |                "lineNo": "274",
            |                "description": "Beneficiary Other 13",
            |                "address": {
            |                  "line1": "Ben Other Line 1",
            |                  "line2": "Ben Other Line 2",
            |                  "line3": "Ben Other Line 3",
            |                  "line4": "Ben Other Line 4",
            |                  "postCode": "AB1 2BA",
            |                  "country": "GB"
            |                },
            |                "entityStart": "2019-09-23"
            |              }""".stripMargin)

        val beneficiary = json.as[OtherBeneficiary]
        beneficiary mustBe OtherBeneficiary(
          description = "Beneficiary Other 13",
          address = Some(UkAddress(
            "Ben Other Line 1",
            "Ben Other Line 2",
            Some("Ben Other Line 3"),
            Some("Ben Other Line 4"),
            "AB1 2BA"
          )),
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }
    }
  }
}