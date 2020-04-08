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

import base.SpecBase
import models.{HowManyBeneficiaries, Name}
import viewmodels.RadioOption

class BeneficiariesSpec extends SpecBase {

  private val individualBeneficiary = IndividualBeneficiary(
    Name("First", None, "last"),
    None,
    None,
    None,
    vulnerableYesNo = false,
    None,
    None,
    incomeDiscretionYesNo = true,
    LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val classOfBeneficiaries = ClassOfBeneficiary(
    "description",
    LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val charityBeneficiary = CharityBeneficiary(
    "name",
    None,
    None,
    None,
    incomeDiscretionYesNo = true,
    LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val trustBeneficiary = TrustBeneficiary(
    "name",
    None,
    None,
    incomeDiscretionYesNo = true,
    LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val companyBeneficiary = CompanyBeneficiary(
    "name",
    None,
    None,
    None,
    incomeDiscretionYesNo = true,
    LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val employmentRelatedBeneficiary = EmploymentRelatedBeneficiary(
    "name",
    None,
    None,
    Nil,
    HowManyBeneficiaries.Over1,
    LocalDate.parse("2019-02-03"),
    provisional = false
  )

  private val otherBeneficiary = OtherBeneficiary(
    "description",
    None,
    None,
    incomeDiscretionYesNo = true,
    LocalDate.parse("2019-02-03"),
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
        RadioOption("whatTypeOfBeneficiary.other", "other", "whatTypeOfBeneficiary.other"))

      beneficiaries.maxedOutOptions mustBe Nil

    }

    "replace charity or trust radio option with trust radio option if charities is maxed out" in {

      val beneficiaries = Beneficiaries(Nil, Nil, Nil, Nil, Nil, charityBeneficiaires, Nil)

      beneficiaries.nonMaxedOutOptions mustBe List(
        RadioOption("whatTypeOfBeneficiary.individual", "individual", "whatTypeOfBeneficiary.individual"),
        RadioOption("whatTypeOfBeneficiary.classOfBeneficiaries", "classOfBeneficiaries", "whatTypeOfBeneficiary.classOfBeneficiaries"),
        RadioOption("whatTypeOfBeneficiary.trust", "trust", "whatTypeOfBeneficiary.trust"),
        RadioOption("whatTypeOfBeneficiary.companyOrEmploymentRelated", "companyOrEmploymentRelated", "whatTypeOfBeneficiary.companyOrEmploymentRelated"),
        RadioOption("whatTypeOfBeneficiary.other", "other", "whatTypeOfBeneficiary.other"))

      beneficiaries.maxedOutOptions mustBe List(
        RadioOption("whatTypeOfBeneficiary.charity", "charity", "whatTypeOfBeneficiary.charity")
      )

    }

    "have no available options if all beneficiary types are maxed out" in {

      val beneficiaries = Beneficiaries(
        individualBeneficiaires,
        classesOfBeneficiaires,
        companyBeneficiaires,
        employmentRelatedBeneficiaires,
        trustBeneficiaires,
        charityBeneficiaires,
        otherBeneficiaires
      )

      beneficiaries.nonMaxedOutOptions mustBe Nil

      beneficiaries.maxedOutOptions mustBe List(
        RadioOption("whatTypeOfBeneficiary.individual", "individual", "whatTypeOfBeneficiary.individual"),
        RadioOption("whatTypeOfBeneficiary.classOfBeneficiaries", "classOfBeneficiaries", "whatTypeOfBeneficiary.classOfBeneficiaries"),
        RadioOption("whatTypeOfBeneficiary.charity", "charity", "whatTypeOfBeneficiary.charity"),
        RadioOption("whatTypeOfBeneficiary.trust", "trust", "whatTypeOfBeneficiary.trust"),
        RadioOption("whatTypeOfBeneficiary.company", "company", "whatTypeOfBeneficiary.company"),
        RadioOption("whatTypeOfBeneficiary.employmentRelated", "employmentRelated", "whatTypeOfBeneficiary.employmentRelated"),
        RadioOption("whatTypeOfBeneficiary.other", "other", "whatTypeOfBeneficiary.other"))

    }


  }

}