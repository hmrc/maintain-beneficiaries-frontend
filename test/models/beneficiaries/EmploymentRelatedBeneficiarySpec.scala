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

import models.HowManyBeneficiaries._
import models.{NonUkAddress, UkAddress}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class EmploymentRelatedBeneficiarySpec extends WordSpec with MustMatchers {

  "EmploymentRelatedBeneficiary" must {

    "deserialise from backend JSON" when {

      "with UK address" in {

        val json = Json.parse(
          """
            |{
            |                "lineNo": "260",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Large 24",
            |                "description": "Description 1",
            |                "numberOfBeneficiary": "1001",
            |                "identification": {
            |                   "address": {
            |                   "line1": "Suite 10",
            |                   "line2": "Wealthy Arena",
            |                   "line3": "Trafagar Square",
            |                   "line4": "London",
            |                   "postCode": "SE2 2HB",
            |                   "country": "GB"
            |                  }
            |                },
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[EmploymentRelatedBeneficiary]

        beneficiary mustBe EmploymentRelatedBeneficiary(
          name = "Beneficiary Large 24",
          utr = None,
          address = Some(UkAddress(
            "Suite 10",
            "Wealthy Arena",
            Some("Trafagar Square"),
            Some("London"),
            "SE2 2HB"
          )),
          description = Seq("Description 1"),
          howManyBeneficiaries = Over1001,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }
      "with foreign address" in {

        val json = Json.parse(
          """
            |{
            |                "lineNo": "260",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Large 24",
            |                "description": "Description 1",
            |                "numberOfBeneficiary": "201",
            |                "identification": {
            |                "address": {
            |                   "line1": "123 Sesame Street",
            |                   "line2": "Hollywood, CA",
            |                   "line3": "314159",
            |                   "country": "US"
            |                   }
            |                },
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[EmploymentRelatedBeneficiary]

        beneficiary mustBe EmploymentRelatedBeneficiary(
          name = "Beneficiary Large 24",
          utr = None,
          address = Some(NonUkAddress(
            "123 Sesame Street",
            "Hollywood, CA",
            Some("314159"),
            "US"
          )),
          description = Seq("Description 1"),
          howManyBeneficiaries = Over201,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "with multiple descriptions" in {
        val json = Json.parse(
          """
            |{
            |                "lineNo": "260",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Large 24",
            |                "description": "Description 1",
            |                "description1": "Description 2",
            |                "description2": "Description 3",
            |                "description3": "Description 4",
            |                "description4": "Description 5",
            |                "numberOfBeneficiary": "101",
            |                "identification": {
            |                  "utr": "3570719187"
            |                },
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[EmploymentRelatedBeneficiary]

        beneficiary mustBe EmploymentRelatedBeneficiary(
          name = "Beneficiary Large 24",
          utr = Some("3570719187"),
          address = None,
          description = Seq("Description 1", "Description 2", "Description 3", "Description 4", "Description 5"),
          howManyBeneficiaries = Over101,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

      "with utr" in {
        val json = Json.parse(
          """
            |{
            |                "lineNo": "260",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Large 24",
            |                "description": "Description 1",
            |                "numberOfBeneficiary": "501",
            |                "identification": {
            |                  "utr": "3570719187"
            |                },
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[EmploymentRelatedBeneficiary]

        beneficiary mustBe EmploymentRelatedBeneficiary(
          name = "Beneficiary Large 24",
          utr = Some("3570719187"),
          address = None,
          description = Seq("Description 1"),
          howManyBeneficiaries = Over501,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }
      "with no identification" in {
        val json = Json.parse(
          """
            |{
            |                "lineNo": "260",
            |                "bpMatchStatus": "01",
            |                "organisationName": "Beneficiary Large 25",
            |                "description": "Description 1",
            |                "numberOfBeneficiary": "1",
            |                "entityStart": "2019-09-23"
            |              }
            |""".stripMargin)

        val beneficiary = json.as[EmploymentRelatedBeneficiary]

        beneficiary mustBe EmploymentRelatedBeneficiary(
          name = "Beneficiary Large 25",
          utr = None,
          address = None,
          description = Seq("Description 1"),
          howManyBeneficiaries = Over1,
          entityStart = LocalDate.of(2019, 9, 23)
        )
      }

    }
  }
}