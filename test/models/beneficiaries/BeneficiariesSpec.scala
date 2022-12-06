/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import models.HowManyBeneficiaries.Over1
import models.{CombinedPassportOrIdCard, Description, IdCard, Name, Passport}
import viewmodels.RadioOption

import java.time.LocalDate

class BeneficiariesSpec extends SpecBase {

  private val individualBeneficiary = IndividualBeneficiary(
    name = Name("First", None, "last"),
    dateOfBirth = None,
    identification = None,
    address = None,
    vulnerableYesNo = Some(false),
    roleInCompany = None,
    income = None  ,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val classOfBeneficiaries = ClassOfBeneficiary(
    description = "description",
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val charityBeneficiary = CharityBeneficiary(
    name = "name",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val trustBeneficiary = TrustBeneficiary(
    name = "name",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val companyBeneficiary = CompanyBeneficiary(
    name = "name",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val employmentRelatedBeneficiary = EmploymentRelatedBeneficiary(
    name = "name",
    utr = None,
    address = None,
    description = Description("Description", None, None, None, None),
    howManyBeneficiaries = Over1,
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val otherBeneficiary = OtherBeneficiary(
    description = "description",
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val individualBeneficiaires: List[IndividualBeneficiary] = List.fill(25)(individualBeneficiary)
  private val classesOfBeneficiaires: List[ClassOfBeneficiary] = List.fill(25)(classOfBeneficiaries)
  private val charityBeneficiaires: List[CharityBeneficiary] = List.fill(25)(charityBeneficiary)
  private val trustBeneficiaires: List[TrustBeneficiary] = List.fill(25)(trustBeneficiary)
  private val companyBeneficiaires: List[CompanyBeneficiary] = List.fill(25)(companyBeneficiary)
  private val employmentRelatedBeneficiaires: List[EmploymentRelatedBeneficiary] = List.fill(25)(employmentRelatedBeneficiary)
  private val otherBeneficiaires: List[OtherBeneficiary] = List.fill(25)(otherBeneficiary)

  "Beneficiaries" must {

    "have all available options if no beneficiary types are maxed out" in {

      val beneficiaries = Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil)

      beneficiaries.nonMaxedOutOptions mustBe List(
        RadioOption("whatTypeOfBeneficiary.individual", "individual", "whatTypeOfBeneficiary.individual"),
        RadioOption("whatTypeOfBeneficiary.classOfBeneficiaries", "classOfBeneficiaries", "whatTypeOfBeneficiary.classOfBeneficiaries"),
        RadioOption("whatTypeOfBeneficiary.charityOrTrust", "charityOrTrust", "whatTypeOfBeneficiary.charityOrTrust"),
        RadioOption("whatTypeOfBeneficiary.companyOrEmploymentRelated", "companyOrEmploymentRelated", "whatTypeOfBeneficiary.companyOrEmploymentRelated"),
        RadioOption("whatTypeOfBeneficiary.other", "other", "whatTypeOfBeneficiary.other")
      )

      beneficiaries.maxedOutOptions mustBe Nil

    }

    "replace charity or trust radio option with trust radio option if charities is maxed out" in {

      val beneficiaries = Beneficiaries(Nil, Nil, Nil, Nil, Nil, charityBeneficiaires, Nil)

      beneficiaries.nonMaxedOutOptions mustBe List(
        RadioOption("whatTypeOfBeneficiary.individual", "individual", "whatTypeOfBeneficiary.individual"),
        RadioOption("whatTypeOfBeneficiary.classOfBeneficiaries", "classOfBeneficiaries", "whatTypeOfBeneficiary.classOfBeneficiaries"),
        RadioOption("whatTypeOfBeneficiary.trust", "trust", "whatTypeOfBeneficiary.trust"),
        RadioOption("whatTypeOfBeneficiary.companyOrEmploymentRelated", "companyOrEmploymentRelated", "whatTypeOfBeneficiary.companyOrEmploymentRelated"),
        RadioOption("whatTypeOfBeneficiary.other", "other", "whatTypeOfBeneficiary.other")
      )

      beneficiaries.maxedOutOptions mustBe List(
        RadioOption("whatTypeOfBeneficiary.charity", "charity", "whatTypeOfBeneficiary.charity")
      )

    }

    "have no available options if all beneficiary types are maxed out" in {

      val beneficiaries = Beneficiaries(
        individualDetails = individualBeneficiaires,
        unidentified = classesOfBeneficiaires,
        company = companyBeneficiaires,
        employmentRelated = employmentRelatedBeneficiaires,
        trust = trustBeneficiaires,
        charity = charityBeneficiaires,
        other = otherBeneficiaires
      )

      beneficiaries.nonMaxedOutOptions mustBe Nil

      beneficiaries.maxedOutOptions mustBe List(
        RadioOption("whatTypeOfBeneficiary.individual", "individual", "whatTypeOfBeneficiary.individual"),
        RadioOption("whatTypeOfBeneficiary.classOfBeneficiaries", "classOfBeneficiaries", "whatTypeOfBeneficiary.classOfBeneficiaries"),
        RadioOption("whatTypeOfBeneficiary.charity", "charity", "whatTypeOfBeneficiary.charity"),
        RadioOption("whatTypeOfBeneficiary.trust", "trust", "whatTypeOfBeneficiary.trust"),
        RadioOption("whatTypeOfBeneficiary.company", "company", "whatTypeOfBeneficiary.company"),
        RadioOption("whatTypeOfBeneficiary.employmentRelated", "employmentRelated", "whatTypeOfBeneficiary.employmentRelated"),
        RadioOption("whatTypeOfBeneficiary.other", "other", "whatTypeOfBeneficiary.other")
      )

    }

    ".individualHasUniquePassportNumber(...)" must {
      "return true when the passport number doesn't equal the other passport numbers in the list" in {
        val passportData = Passport(countryOfIssue = "country", number = "1122", expirationDate = LocalDate.now().plusYears(2))
        val individualDetails = List(
          individualBeneficiary.copy(identification = Some(passportData)),
          individualBeneficiary.copy(identification = Some(passportData.copy(number = "2233")))
        )
        val beneficiaries = Beneficiaries(individualDetails, Nil, Nil, Nil, Nil, Nil, Nil)

        beneficiaries.individualHasUniquePassportNumber(passportNo = "3344") mustBe true
      }

      "return false when the passport number is equal to another passport number in the list" in {
        val passportData = Passport(countryOfIssue = "country", number = "11111", expirationDate = LocalDate.now().plusYears(2))
        val individualDetails = List(
          individualBeneficiary.copy(identification = Some(passportData)),
          individualBeneficiary.copy(identification = Some(passportData.copy(number = "22222")))
        )
        val beneficiaries = Beneficiaries(individualDetails, Nil, Nil, Nil, Nil, Nil, Nil)

        beneficiaries.individualHasUniquePassportNumber(passportNo = "11111") mustBe false
      }

      "return true when the passport number is equal to another form of identification" in {
        val idCardData = IdCard(countryOfIssue = "country", number = "11111", expirationDate = LocalDate.now().plusYears(2))
        val individualDetails = List(individualBeneficiary.copy(identification = Some(idCardData)))
        val beneficiaries = Beneficiaries(individualDetails, Nil, Nil, Nil, Nil, Nil, Nil)

        beneficiaries.individualHasUniquePassportNumber(passportNo = "11111") mustBe true
      }
    }

    ".individualHasUniqueIdCardNumber(...)" must {
      "return true when the ID Card number doesn't equal the other ID Card numbers in the list" in {
        val idCardData = IdCard(countryOfIssue = "country", number = "11111", expirationDate = LocalDate.now())
        val individualDetails = List(individualBeneficiary.copy(identification = Some(idCardData)))
        val beneficiaries = Beneficiaries(individualDetails, Nil, Nil, Nil, Nil, Nil, Nil)

        beneficiaries.individualHasUniqueIdCardNumber(idCardNo = "333333") mustBe true
      }

      "return false when the ID Card number is equal to another ID Card number in the list" in {
        val idCardData = IdCard(countryOfIssue = "country", number = "11111", expirationDate = LocalDate.now().plusYears(5))
        val individualDetails = List(individualBeneficiary.copy(identification = Some(idCardData)))
        val beneficiaries = Beneficiaries(individualDetails, Nil, Nil, Nil, Nil, Nil, Nil)

        beneficiaries.individualHasUniqueIdCardNumber(idCardNo = "11111") mustBe false
      }

      "return true when the ID Card number is equal to another form of identification" in {
        val idCardData = CombinedPassportOrIdCard(countryOfIssue = "country", number = "11111", expirationDate = LocalDate.now().plusYears(5))
        val individualDetails = List(individualBeneficiary.copy(identification = Some(idCardData)))
        val beneficiaries = Beneficiaries(individualDetails, Nil, Nil, Nil, Nil, Nil, Nil)

        beneficiaries.individualHasUniqueIdCardNumber(idCardNo = "11111") mustBe true
      }
    }
  }

}
