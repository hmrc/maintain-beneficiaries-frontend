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

import models.UkAddress
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class CharityBeneficiarySpec extends WordSpec with MustMatchers {

  "CharityBeneficiary" must {
    "deserialise from backend JSON" when {
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
            |  "entityStart": "2017-02-28"
            |}
            |""".stripMargin)

        val beneficiary = json.as[CharityBeneficiary]
        beneficiary mustBe CharityBeneficiary(
          name = "Nelson Ltd ",
          address = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.of(2017, 2, 28)
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
            |  "entityStart": "2017-02-28"
            |}
            |""".stripMargin)

        val beneficiary = json.as[CharityBeneficiary]
        beneficiary mustBe CharityBeneficiary(
          name = "Nelson Ltd ",
          address = None,
          income = Some("10000"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2017, 2, 28)
        )
      }
      "there is no income at all" in {
        val json = Json.parse(
          """
            |{
            |  "lineNo": "1",
            |  "bpMatchStatus": "01",
            |  "organisationName": "Nelson Ltd ",
            |  "identification": {
            |    "safeId": "2222200000000"
            |  },
            |  "entityStart": "2017-02-28"
            |}
            |""".stripMargin)

        val beneficiary = json.as[CharityBeneficiary]
        beneficiary mustBe CharityBeneficiary(
          name = "Nelson Ltd ",
          address = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.of(2017, 2, 28)
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
            |          "line1": "Suite 10",
            |          "line2": "Wealthy Arena",
            |          "line3": "Trafagar Square",
            |          "line4": "London",
            |          "postCode": "SE2 2HB",
            |          "country": "GB"
            |       }
            |  },
            |  "entityStart": "2017-02-28"
            |}
            |""".stripMargin)

        val beneficiary = json.as[CharityBeneficiary]
        beneficiary mustBe CharityBeneficiary(
          name = "Nelson Ltd ",
          address = Some(UkAddress(
            "Suite 10",
            "Wealthy Arena",
            Some("Trafagar Square"),
            Some("London"),
            "SE2 2HB"
          )),
          income = Some("10000"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.of(2017, 2, 28)
        )
      }
    }
  }
}
