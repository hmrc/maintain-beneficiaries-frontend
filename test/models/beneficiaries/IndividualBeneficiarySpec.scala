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

import models.{Name, NationalInsuranceNumber, NonUkAddress, UkAddress}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class IndividualBeneficiarySpec extends WordSpec with MustMatchers {
  private val testDateOfBirth = Some(LocalDate.of(1970, 2, 28))
  private val testEntityStart = LocalDate.of(2017, 2, 28)

  "IndividualBeneficiary" must {

    "deserialise from backend JSON" when {

      "with UK address" in {
        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |    "firstName": "Nicola",
            |    "middleName": "Andrey",
            |    "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryDiscretion": false,
            |    "beneficiaryShareOfIncome": "10",
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
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = Some(UkAddress(
            "Suite 10",
            "Wealthy Arena",
            Some("Trafagar Square"),
            Some("London"),
            "SE2 2HB"
          )),
          vulnerableYesNo = true,
          roleInCompany = None,
          income = Some("10"),
          incomeDiscretionYesNo = false,
          entityStart = testEntityStart,
          provisional = false
        )
      }

      "with foreign address" in {
        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |    "firstName": "Nicola",
            |    "middleName": "Andrey",
            |    "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryDiscretion": false,
            |    "beneficiaryShareOfIncome": "10",
            |    "identification": {
            |       "safeId": "2222200000000",
            |       "address": {
            |          "line1": "123 Sesame Street",
            |          "line2": "Hollywood, CA",
            |          "line3": "314159",
            |          "country": "US"
            |       }
            |    },
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = Some(NonUkAddress(
            "123 Sesame Street",
            "Hollywood, CA",
            Some("314159"),
            "US"
          )),
          roleInCompany = None,
          vulnerableYesNo = true,
          income = Some("10"),
          incomeDiscretionYesNo = false,
          entityStart = testEntityStart,
          provisional = false
        )
      }

      "with nino" in {
        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |      "firstName": "Nicola",
            |      "middleName": "Andrey",
            |      "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryDiscretion": false,
            |    "beneficiaryShareOfIncome": "10",
            |    "identification": {
            |       "nino": "NH111111A"
            |    },
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = Some(NationalInsuranceNumber("NH111111A")),
          address = None,
          vulnerableYesNo = true,
          roleInCompany = None,
          income = Some("10"),
          incomeDiscretionYesNo = false,
          entityStart = testEntityStart,
          provisional = false
        )
      }

      "with no identification" in {
        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |      "firstName": "Nicola",
            |      "middleName": "Andrey",
            |      "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryDiscretion": true,
            |    "beneficiaryShareOfIncome": "0",
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = None,
          vulnerableYesNo = true,
          roleInCompany = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = testEntityStart,
          provisional = false
        )
      }

      "there is conflicting income info" in {

        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |    "firstName": "Nicola",
            |    "middleName": "Andrey",
            |    "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryDiscretion": true,
            |    "beneficiaryShareOfIncome": "0",
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
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = Some(UkAddress(
            "Suite 10",
            "Wealthy Arena",
            Some("Trafagar Square"),
            Some("London"),
            "SE2 2HB"
          )),
          vulnerableYesNo = true,
          roleInCompany = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = testEntityStart,
          provisional = false
        )
      }

      "there is no discretion for income info" in {

        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |    "firstName": "Nicola",
            |    "middleName": "Andrey",
            |    "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryShareOfIncome": "10000",
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
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = Some(UkAddress(
            "Suite 10",
            "Wealthy Arena",
            Some("Trafagar Square"),
            Some("London"),
            "SE2 2HB"
          )),
          vulnerableYesNo = true,
          roleInCompany = None,
          income = Some("10000"),
          incomeDiscretionYesNo = false,
          entityStart = testEntityStart,
          provisional = false
        )
      }

      "there is no income at all" in {

        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |    "firstName": "Nicola",
            |    "middleName": "Andrey",
            |    "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
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
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = Some(UkAddress(
            "Suite 10",
            "Wealthy Arena",
            Some("Trafagar Square"),
            Some("London"),
            "SE2 2HB"
          )),
          vulnerableYesNo = true,
          roleInCompany = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = testEntityStart,
          provisional = false
        )
      }
      "with director employment type" in {
        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |      "firstName": "Nicola",
            |      "middleName": "Andrey",
            |      "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryType": "director",
            |    "beneficiaryDiscretion": true,
            |    "beneficiaryShareOfIncome": "0",
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = None,
          vulnerableYesNo = true,
          roleInCompany = Some(RoleInCompany.Director),
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = testEntityStart,
          provisional = false
        )
      }
      "with employee employment type" in {
        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |      "firstName": "Nicola",
            |      "middleName": "Andrey",
            |      "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryType": "employee",
            |    "beneficiaryDiscretion": true,
            |    "beneficiaryShareOfIncome": "0",
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = None,
          vulnerableYesNo = true,
          roleInCompany = Some(RoleInCompany.Employee),
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = testEntityStart,
          provisional = false
        )
      }
      "with NA employment type" in {
        val json = Json.parse(
          """
            |{
            |    "lineNo": "1",
            |    "bpMatchStatus": "01",
            |    "name": {
            |      "firstName": "Nicola",
            |      "middleName": "Andrey",
            |      "lastName": "Jackson"
            |    },
            |    "dateOfBirth": "1970-02-28",
            |    "vulnerableBeneficiary": true,
            |    "beneficiaryType": "na",
            |    "beneficiaryDiscretion": true,
            |    "beneficiaryShareOfIncome": "0",
            |    "entityStart": "2017-02-28",
            |    "provisional": false
            |}
            |""".stripMargin)

        val beneficiary = json.as[IndividualBeneficiary]
        beneficiary mustBe IndividualBeneficiary(
          name = Name("Nicola", Some("Andrey"), "Jackson"),
          dateOfBirth = testDateOfBirth,
          identification = None,
          address = None,
          vulnerableYesNo = true,
          roleInCompany = Some(RoleInCompany.NA),
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = testEntityStart,
          provisional = false
        )
      }

    }

    "serialise to backend JSON" when {

      "with UK address in" in {
        val individual = IndividualBeneficiary(
          name = Name(
            firstName = "First",
            middleName = None,
            lastName = "Last"
          ),
          dateOfBirth = None,
          identification = None,
          address = Some(UkAddress(
            line1 = "Line 1",
            line2 = "Line 2",
            line3 = None,
            line4 = None,
            postcode = "NE11ZZ"
          )),
          vulnerableYesNo = false,
          roleInCompany = None,
          income = None,
          incomeDiscretionYesNo = true,
          entityStart = LocalDate.parse("2020-03-27"),
          provisional = false
        )

        val asJson = Json.toJson(individual)

        asJson mustEqual Json.parse(
          """
            |{
            |    "name": {
            |    "firstName": "First",
            |    "lastName": "Last"
            |    },
            |    "vulnerableBeneficiary": false,
            |    "beneficiaryDiscretion": true,
            |    "identification": {
            |       "address": {
            |          "line1": "Line 1",
            |          "line2": "Line 2",
            |          "postCode": "NE11ZZ",
            |          "country": "GB"
            |       }
            |    },
            |    "entityStart": "2020-03-27",
            |    "provisional": false
            |}
            |""".stripMargin)

      }

      "with non-UK address in" in {
        val individual = IndividualBeneficiary(
          name = Name(
            firstName = "First",
            middleName = Some("Middle"),
            lastName = "Last"
          ),
          dateOfBirth = Some(LocalDate.parse("2020-10-05")),
          identification = None,
          address = Some(NonUkAddress(
            line1 = "Line 1",
            line2 = "Line 2",
            line3 = None,
            country = "DE"
          )),
          vulnerableYesNo = false,
          roleInCompany = None,
          income = Some("25"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.parse("2020-03-27"),
          provisional = false
        )

        val asJson = Json.toJson(individual)

        asJson mustBe Json.parse(
          """
            |{
            |    "name": {
            |    "firstName": "First",
            |    "middleName": "Middle",
            |    "lastName": "Last"
            |    },
            |    "dateOfBirth": "2020-10-05",
            |    "identification": {
            |       "address": {
            |          "line1": "Line 1",
            |          "line2": "Line 2",
            |          "country": "DE"
            |       }
            |    },
            |    "vulnerableBeneficiary": false,
            |    "beneficiaryShareOfIncome": "25",
            |    "beneficiaryDiscretion": false,
            |    "entityStart": "2020-03-27",
            |    "provisional": false
            |}
            |""".stripMargin)

      }

      "with nino in" in {
        val individual = IndividualBeneficiary(
          name = Name(
            firstName = "First",
            middleName = Some("Middle"),
            lastName = "Last"
          ),
          dateOfBirth = Some(LocalDate.parse("2020-10-05")),
          identification = Some(NationalInsuranceNumber("JP121212A")),
          address = None,
          vulnerableYesNo = false,
          roleInCompany = None,
          income = Some("25"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.parse("2020-03-27"),
          provisional = false
        )

        val asJson = Json.toJson(individual)

        asJson mustBe Json.parse(
          """
            |{
            |    "name": {
            |    "firstName": "First",
            |    "middleName": "Middle",
            |    "lastName": "Last"
            |    },
            |    "dateOfBirth": "2020-10-05",
            |    "identification": {
            |       "nino": "JP121212A"
            |    },
            |    "vulnerableBeneficiary": false,
            |    "beneficiaryShareOfIncome": "25",
            |    "beneficiaryDiscretion": false,
            |    "entityStart": "2020-03-27",
            |    "provisional": false
            |}
            |""".stripMargin)

      }

      "with no identification in" in {
        val individual = IndividualBeneficiary(
          name = Name(
            firstName = "First",
            middleName = Some("Middle"),
            lastName = "Last"
          ),
          dateOfBirth = Some(LocalDate.parse("2020-10-05")),
          identification = None,
          address = None,
          vulnerableYesNo = false,
          roleInCompany = None,
          income = Some("25"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.parse("2020-03-27"),
          provisional = false
        )

        val asJson = Json.toJson(individual)

        asJson mustBe Json.parse(
          """
            |{
            |    "name": {
            |    "firstName": "First",
            |    "middleName": "Middle",
            |    "lastName": "Last"
            |    },
            |    "dateOfBirth": "2020-10-05",
            |    "vulnerableBeneficiary": false,
            |    "beneficiaryShareOfIncome": "25",
            |    "beneficiaryDiscretion": false,
            |    "entityStart": "2020-03-27",
            |    "provisional": false
            |}
            |""".stripMargin)

      }
      "with director employment type in" in {
        val individual = IndividualBeneficiary(
          name = Name(
            firstName = "First",
            middleName = Some("Middle"),
            lastName = "Last"
          ),
          dateOfBirth = Some(LocalDate.parse("2020-10-05")),
          identification = None,
          address = None,
          vulnerableYesNo = false,
          roleInCompany = Some(RoleInCompany.Director),
          income = Some("25"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.parse("2020-03-27"),
          provisional = false
        )

        val asJson = Json.toJson(individual)

        asJson mustBe Json.parse(
          """
            |{
            |    "name": {
            |    "firstName": "First",
            |    "middleName": "Middle",
            |    "lastName": "Last"
            |    },
            |    "dateOfBirth": "2020-10-05",
            |    "vulnerableBeneficiary": false,
            |    "beneficiaryType": "director",
            |    "beneficiaryShareOfIncome": "25",
            |    "beneficiaryDiscretion": false,
            |    "entityStart": "2020-03-27",
            |    "provisional": false
            |}
            |""".stripMargin)

      }
      "with employee employment type in" in {
        val individual = IndividualBeneficiary(
          name = Name(
            firstName = "First",
            middleName = Some("Middle"),
            lastName = "Last"
          ),
          dateOfBirth = Some(LocalDate.parse("2020-10-05")),
          identification = None,
          address = None,
          vulnerableYesNo = false,
          roleInCompany = Some(RoleInCompany.Employee),
          income = Some("25"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.parse("2020-03-27"),
          provisional = false
        )

        val asJson = Json.toJson(individual)

        asJson mustBe Json.parse(
          """
            |{
            |    "name": {
            |    "firstName": "First",
            |    "middleName": "Middle",
            |    "lastName": "Last"
            |    },
            |    "dateOfBirth": "2020-10-05",
            |    "vulnerableBeneficiary": false,
            |    "beneficiaryType": "employee",
            |    "beneficiaryShareOfIncome": "25",
            |    "beneficiaryDiscretion": false,
            |    "entityStart": "2020-03-27",
            |    "provisional": false
            |}
            |""".stripMargin)

      }
      "with NA employment type in" in {
        val individual = IndividualBeneficiary(
          name = Name(
            firstName = "First",
            middleName = Some("Middle"),
            lastName = "Last"
          ),
          dateOfBirth = Some(LocalDate.parse("2020-10-05")),
          identification = None,
          address = None,
          vulnerableYesNo = false,
          roleInCompany = Some(RoleInCompany.NA),
          income = Some("25"),
          incomeDiscretionYesNo = false,
          entityStart = LocalDate.parse("2020-03-27"),
          provisional = false
        )

        val asJson = Json.toJson(individual)

        asJson mustBe Json.parse(
          """
            |{
            |    "name": {
            |    "firstName": "First",
            |    "middleName": "Middle",
            |    "lastName": "Last"
            |    },
            |    "dateOfBirth": "2020-10-05",
            |    "vulnerableBeneficiary": false,
            |    "beneficiaryType": "na",
            |    "beneficiaryShareOfIncome": "25",
            |    "beneficiaryDiscretion": false,
            |    "entityStart": "2020-03-27",
            |    "provisional": false
            |}
            |""".stripMargin)

      }

    }
  }
}