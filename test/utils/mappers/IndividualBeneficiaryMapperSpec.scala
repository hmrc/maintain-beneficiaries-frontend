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

package utils.mappers

import base.SpecBase
import models.{CombinedPassportOrIdCard, Name, NationalInsuranceNumber, NonUkAddress, UkAddress}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add.StartDatePage

import java.time.LocalDate

class IndividualBeneficiaryMapperSpec extends SpecBase {

  private val name = Name("First", None, "Last")
  private val dateOfBirth = LocalDate.parse("2010-02-03")
  private val startDate = LocalDate.parse("2019-03-09")
  private val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), "country")

  "IndividualBeneficiaryMapper" when {

    val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

    "adding" must {

        "taxable" when {

          "no country of nationality, no country of residence, not legally incapable" in {

            val nino = "AA123456A"

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(DateOfBirthYesNoPage, true).success.value
              .set(DateOfBirthPage, dateOfBirth).success.value
              .set(IncomeDiscretionYesNoPage, true).success.value
              .set(CountryOfNationalityYesNoPage, false).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value
              .set(MentalCapacityYesNoPage, false).success.value
              .set(NationalInsuranceNumberYesNoPage, true).success.value
              .set(NationalInsuranceNumberPage, nino).success.value
              .set(VPE1FormYesNoPage, false).success.value
              .set(StartDatePage, startDate).success.value

            val result = mapper(userAnswers).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe Some(true)
            result.income mustBe None
            result.countryOfResidence mustBe None
            result.nationality mustBe None
            result.mentalCapacityYesNo mustBe Some(false)
            result.identification mustBe Some(NationalInsuranceNumber(nino))
            result.address mustBe None
            result.vulnerableYesNo mustBe Some(false)
            result.entityStart mustBe startDate
          }

          "UK country of nationality, UK country of residence, legally incapable" in {

            val nino = "AA123456A"

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(DateOfBirthYesNoPage, true).success.value
              .set(DateOfBirthPage, dateOfBirth).success.value
              .set(IncomeDiscretionYesNoPage, true).success.value
              .set(CountryOfNationalityYesNoPage, true).success.value
              .set(CountryOfNationalityUkYesNoPage, true).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, true).success.value
              .set(MentalCapacityYesNoPage, true).success.value
              .set(NationalInsuranceNumberYesNoPage, true).success.value
              .set(NationalInsuranceNumberPage, nino).success.value
              .set(VPE1FormYesNoPage, false).success.value
              .set(StartDatePage, startDate).success.value

            val result = mapper(userAnswers).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe Some(true)
            result.income mustBe None
            result.countryOfResidence mustBe Some("GB")
            result.nationality mustBe Some("GB")
            result.mentalCapacityYesNo mustBe Some(true)
            result.identification mustBe Some(NationalInsuranceNumber(nino))
            result.address mustBe None
            result.vulnerableYesNo mustBe Some(false)
            result.entityStart mustBe startDate
          }

        }

        "non-taxable" when {

          "no country of nationality, no country of residence, not legally incapable" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(DateOfBirthYesNoPage, true).success.value
              .set(DateOfBirthPage, dateOfBirth).success.value
              .set(CountryOfNationalityYesNoPage, false).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value
              .set(MentalCapacityYesNoPage, false).success.value
              .set(StartDatePage, startDate).success.value

            val result = mapper(userAnswers).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe None
            result.income mustBe None
            result.countryOfResidence mustBe None
            result.nationality mustBe None
            result.mentalCapacityYesNo mustBe Some(false)
            result.identification mustBe None
            result.address mustBe None
            result.vulnerableYesNo mustBe None
            result.entityStart mustBe startDate
          }

          "UK country of nationality, UK country of residence, and legally incapable" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(DateOfBirthYesNoPage, true).success.value
              .set(DateOfBirthPage, dateOfBirth).success.value
              .set(CountryOfNationalityYesNoPage, true).success.value
              .set(CountryOfNationalityUkYesNoPage, true).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, true).success.value
              .set(MentalCapacityYesNoPage, true).success.value
              .set(StartDatePage, startDate).success.value

            val result = mapper(userAnswers).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe None
            result.income mustBe None
            result.countryOfResidence mustBe Some("GB")
            result.nationality mustBe Some("GB")
            result.mentalCapacityYesNo mustBe Some(true)
            result.identification mustBe None
            result.address mustBe None
            result.vulnerableYesNo mustBe None
            result.entityStart mustBe startDate
          }

        }

    }

    "amending" must {

        "taxable" when {

          "UK country of nationality, UK country of residence, and legally incapable" in {

            val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

            val passport = CombinedPassportOrIdCard("SP", "123456789", LocalDate.of(2024, 8, 16))

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(DateOfBirthYesNoPage, true).success.value
              .set(DateOfBirthPage, dateOfBirth).success.value
              .set(IncomeDiscretionYesNoPage, true).success.value
              .set(CountryOfNationalityYesNoPage, false).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value
              .set(MentalCapacityYesNoPage, false).success.value
              .set(NationalInsuranceNumberYesNoPage, false).success.value
              .set(AddressYesNoPage, true).success.value
              .set(LiveInTheUkYesNoPage, false).success.value
              .set(NonUkAddressPage, nonUkAddress).success.value
              .set(PassportOrIdCardDetailsYesNoPage, true).success.value
              .set(PassportOrIdCardDetailsPage, passport).success.value
              .set(VPE1FormYesNoPage, false).success.value
              .set(StartDatePage, startDate).success.value

            val result = mapper(userAnswers).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe Some(true)
            result.income mustBe None
            result.countryOfResidence mustBe None
            result.nationality mustBe None
            result.mentalCapacityYesNo mustBe Some(false)
            result.identification mustBe Some(passport)
            result.address mustBe Some(nonUkAddress)
            result.vulnerableYesNo mustBe Some(false)
            result.entityStart mustBe startDate
          }

        }

        "non-taxable" when {

          "UK country of nationality, UK country of residence, and legally incapable" in {

            val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(DateOfBirthYesNoPage, true).success.value
              .set(DateOfBirthPage, dateOfBirth).success.value
              .set(CountryOfNationalityYesNoPage, true).success.value
              .set(CountryOfNationalityUkYesNoPage, true).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, true).success.value
              .set(MentalCapacityYesNoPage, true).success.value
              .set(StartDatePage, startDate).success.value

            val result = mapper(userAnswers).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe None
            result.income mustBe None
            result.countryOfResidence mustBe Some("GB")
            result.nationality mustBe Some("GB")
            result.mentalCapacityYesNo mustBe Some(true)
            result.identification mustBe None
            result.address mustBe None
            result.vulnerableYesNo mustBe None
            result.entityStart mustBe startDate
          }

        }

    }
  }
}
