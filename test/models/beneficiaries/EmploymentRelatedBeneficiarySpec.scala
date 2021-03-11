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

import utils.Constants.GB

import java.time.LocalDate
import models.HowManyBeneficiaries._
import models.{Description, NonUkAddress, UkAddress}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class EmploymentRelatedBeneficiarySpec extends WordSpec with MustMatchers {
  
  private val name = "Large Beneficiary"
  private val description = "Description"
  private val utr = "3570719187"
  private val date = "2019-09-23"

  "EmploymentRelatedBeneficiary" must {

    "deserialise from backend JSON" when {

      "taxable" when {

        "with UK address" in {
          val json = Json.parse(
            s"""
               |{
               |  "lineNo": "260",
               |  "bpMatchStatus": "01",
               |  "organisationName": "$name",
               |  "description": "$description",
               |  "numberOfBeneficiary": "1001",
               |  "identification": {
               |    "address": {
               |      "line1": "Suite 10",
               |      "line2": "Wealthy Arena",
               |      "line3": "Trafagar Square",
               |      "line4": "London",
               |      "postCode": "SE2 2HB",
               |      "country": "GB"
               |    }
               |  },
               |  "entityStart": "$date",
               |  "provisional": false
               |}
               |""".stripMargin)

          val beneficiary = json.as[EmploymentRelatedBeneficiary]

          beneficiary mustBe EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = Some(UkAddress(
              "Suite 10",
              "Wealthy Arena",
              Some("Trafagar Square"),
              Some("London"),
              "SE2 2HB"
            )),
            description = Description(description, None, None, None, None),
            howManyBeneficiaries = Over1001,
            entityStart = LocalDate.parse(date),
            provisional = false
          )
        }

        "with foreign address" in {
          val json = Json.parse(
            s"""
               |{
               |  "lineNo": "260",
               |  "bpMatchStatus": "01",
               |  "organisationName": "$name",
               |  "description": "$description",
               |  "numberOfBeneficiary": "201",
               |  "identification": {
               |    "address": {
               |      "line1": "123 Sesame Street",
               |      "line2": "Hollywood, CA",
               |      "line3": "314159",
               |      "country": "US"
               |    }
               |  },
               |  "entityStart": "$date",
               |  "provisional": false
               |}
               |""".stripMargin)

          val beneficiary = json.as[EmploymentRelatedBeneficiary]

          beneficiary mustBe EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = Some(NonUkAddress(
              "123 Sesame Street",
              "Hollywood, CA",
              Some("314159"),
              "US"
            )),
            description = Description(description, None, None, None, None),
            howManyBeneficiaries = Over201,
            entityStart = LocalDate.parse(date),
            provisional = false
          )
        }

        "with multiple descriptions" in {
          val json = Json.parse(
            s"""
               |{
               |  "lineNo": "260",
               |  "bpMatchStatus": "01",
               |  "organisationName": "$name",
               |  "description": "$description",
               |  "description1": "Description 2",
               |  "description2": "Description 3",
               |  "description3": "Description 4",
               |  "description4": "Description 5",
               |  "numberOfBeneficiary": "101",
               |  "identification": {
               |    "utr": "$utr"
               |  },
               |  "entityStart": "$date",
               |  "provisional": false
               |}
               |""".stripMargin)

          val beneficiary = json.as[EmploymentRelatedBeneficiary]

          beneficiary mustBe EmploymentRelatedBeneficiary(
            name = name,
            utr = Some(utr),
            address = None,
            description = Description(description, Some("Description 2"), Some("Description 3"), Some("Description 4"), Some("Description 5")),
            howManyBeneficiaries = Over101,
            entityStart = LocalDate.parse(date),
            provisional = false
          )
        }

        "with utr" in {
          val json = Json.parse(
            s"""
               |{
               |  "lineNo": "260",
               |  "bpMatchStatus": "01",
               |  "organisationName": "$name",
               |  "description": "$description",
               |  "numberOfBeneficiary": "501",
               |  "identification": {
               |    "utr": "$utr"
               |  },
               |  "entityStart": "$date",
               |  "provisional": false
               |}
               |""".stripMargin)

          val beneficiary = json.as[EmploymentRelatedBeneficiary]

          beneficiary mustBe EmploymentRelatedBeneficiary(
            name = name,
            utr = Some(utr),
            address = None,
            description = Description(description, None, None, None, None),
            howManyBeneficiaries = Over501,
            entityStart = LocalDate.parse(date),
            provisional = false
          )
        }

        "with no identification" in {
          val json = Json.parse(
            s"""
               |{
               |  "lineNo": "260",
               |  "bpMatchStatus": "01",
               |  "organisationName": "$name",
               |  "description": "$description",
               |  "numberOfBeneficiary": "1",
               |  "entityStart": "$date",
               |  "provisional": false
               |}
               |""".stripMargin)

          val beneficiary = json.as[EmploymentRelatedBeneficiary]

          beneficiary mustBe EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = None,
            description = Description(description, None, None, None, None),
            howManyBeneficiaries = Over1,
            entityStart = LocalDate.parse(date),
            provisional = false
          )
        }
      }

      "non-taxable" when {

        "there is no country of residence" in {
          val json = Json.parse(
            s"""
               |{
               |  "lineNo": "260",
               |  "bpMatchStatus": "01",
               |  "organisationName": "$name",
               |  "description": "$description",
               |  "numberOfBeneficiary": "1",
               |  "entityStart": "$date",
               |  "provisional": false
               |}
               |""".stripMargin)

          val beneficiary = json.as[EmploymentRelatedBeneficiary]

          beneficiary mustBe EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = None,
            description = Description(description, None, None, None, None),
            howManyBeneficiaries = Over1,
            countryOfResidence = None,
            entityStart = LocalDate.parse(date),
            provisional = false
          )
        }

        "there is a country of residence" in {
          val json = Json.parse(
            s"""
               |{
               |  "lineNo": "260",
               |  "bpMatchStatus": "01",
               |  "organisationName": "$name",
               |  "countryOfResidence": "$GB",
               |  "description": "$description",
               |  "numberOfBeneficiary": "1",
               |  "entityStart": "$date",
               |  "provisional": false
               |}
               |""".stripMargin)

          val beneficiary = json.as[EmploymentRelatedBeneficiary]

          beneficiary mustBe EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = None,
            description = Description(description, None, None, None, None),
            howManyBeneficiaries = Over1,
            countryOfResidence = Some(GB),
            entityStart = LocalDate.parse(date),
            provisional = false
          )
        }
      }
    }
  }
}