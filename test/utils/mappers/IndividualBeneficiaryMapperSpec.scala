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

import java.time.LocalDate

import base.SpecBase
import models.beneficiaries.RoleInCompany
import models.{CombinedPassportOrIdCard, IdCard, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add.{IdCardDetailsPage, IdCardDetailsYesNoPage, PassportDetailsPage, PassportDetailsYesNoPage, StartDatePage}
import pages.individualbeneficiary.amend.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}

class IndividualBeneficiaryMapperSpec extends SpecBase {

  private val name = Name("First", None, "Last")
  private val dateOfBirth = LocalDate.parse("2010-02-03")
  private val startDate = LocalDate.parse("2019-03-09")
  private val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")
  private val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), "country")

  "IndividualBeneficiaryMapper" when {

    val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

    "adding" must {

      "4mld" when {

        "return None for empty user answers" in {

          val result = mapper(emptyUserAnswers, provisional = true)
          result mustBe None
        }

        "generate class of individual model with nino and income discretion" in {

          val nino = "AA123456A"

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, true).success.value
            .set(DateOfBirthPage, dateOfBirth).success.value
            .set(IncomeDiscretionYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, true).success.value
            .set(NationalInsuranceNumberPage, nino).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = true).get

          result.name mustBe name
          result.roleInCompany mustBe None
          result.dateOfBirth mustBe Some(dateOfBirth)
          result.incomeDiscretionYesNo mustBe Some(true)
          result.income mustBe None
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe Some(NationalInsuranceNumber(nino))
          result.address mustBe None
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }

        "generate class of individual model with UK address and no income discretion" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, true).success.value
            .set(DateOfBirthPage, dateOfBirth).success.value
            .set(IncomeDiscretionYesNoPage, false).success.value
            .set(IncomePercentagePage, 45).success.value
            .set(NationalInsuranceNumberYesNoPage, false).success.value
            .set(AddressYesNoPage, true).success.value
            .set(LiveInTheUkYesNoPage, true).success.value
            .set(UkAddressPage, ukAddress).success.value
            .set(PassportDetailsYesNoPage, false).success.value
            .set(IdCardDetailsYesNoPage, false).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = true).get

          result.name mustBe name
          result.roleInCompany mustBe None
          result.dateOfBirth mustBe Some(dateOfBirth)
          result.incomeDiscretionYesNo mustBe Some(false)
          result.income mustBe Some("45")
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe None
          result.address mustBe Some(ukAddress)
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }

        "generate class of individual model with non-UK address" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, true).success.value
            .set(DateOfBirthPage, dateOfBirth).success.value
            .set(IncomeDiscretionYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, false).success.value
            .set(AddressYesNoPage, true).success.value
            .set(LiveInTheUkYesNoPage, false).success.value
            .set(NonUkAddressPage, nonUkAddress).success.value
            .set(PassportDetailsYesNoPage, false).success.value
            .set(IdCardDetailsYesNoPage, false).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = true).get

          result.name mustBe name
          result.roleInCompany mustBe None
          result.dateOfBirth mustBe Some(dateOfBirth)
          result.incomeDiscretionYesNo mustBe Some(true)
          result.income mustBe None
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe None
          result.address mustBe Some(nonUkAddress)
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }

        "generate class of individual model with neither nino nor address" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthPage, dateOfBirth).success.value
            .set(IncomeDiscretionYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, false).success.value
            .set(AddressYesNoPage, false).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = true).get

          result.name mustBe name
          result.roleInCompany mustBe None
          result.dateOfBirth mustBe Some(dateOfBirth)
          result.incomeDiscretionYesNo mustBe Some(true)
          result.income mustBe None
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe None
          result.address mustBe None
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }

        "generate class of individual model with role in company" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(RoleInCompanyPage, RoleInCompany.Employee).success.value
            .set(DateOfBirthPage, dateOfBirth).success.value
            .set(IncomeDiscretionYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, false).success.value
            .set(AddressYesNoPage, false).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = true).get

          result.name mustBe name
          result.roleInCompany mustBe Some(RoleInCompany.Employee)
          result.dateOfBirth mustBe Some(dateOfBirth)
          result.incomeDiscretionYesNo mustBe Some(true)
          result.income mustBe None
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe None
          result.address mustBe None
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }


        "generate class of individual model with passport" in {

          val passport = Passport("SP", "123456789", LocalDate.of(2024, 8, 16))

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, true).success.value
            .set(DateOfBirthPage, dateOfBirth).success.value
            .set(IncomeDiscretionYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, false).success.value
            .set(AddressYesNoPage, true).success.value
            .set(LiveInTheUkYesNoPage, false).success.value
            .set(NonUkAddressPage, nonUkAddress).success.value
            .set(PassportDetailsYesNoPage, true).success.value
            .set(PassportDetailsPage, passport).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = true).get

          result.name mustBe name
          result.roleInCompany mustBe None
          result.dateOfBirth mustBe Some(dateOfBirth)
          result.incomeDiscretionYesNo mustBe Some(true)
          result.income mustBe None
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe Some(passport)
          result.address mustBe Some(nonUkAddress)
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }

        "generate class of individual model with id card" in {

          val idcard = IdCard("SP", "123456789", LocalDate.of(2024, 8, 16))

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, false).success.value
            .set(IncomeDiscretionYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, false).success.value
            .set(AddressYesNoPage, true).success.value
            .set(LiveInTheUkYesNoPage, true).success.value
            .set(UkAddressPage, ukAddress).success.value
            .set(PassportDetailsYesNoPage, false).success.value
            .set(IdCardDetailsYesNoPage, true).success.value
            .set(IdCardDetailsPage, idcard).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = true).get

          result.name mustBe name
          result.roleInCompany mustBe None
          result.dateOfBirth mustBe None
          result.incomeDiscretionYesNo mustBe Some(true)
          result.income mustBe None
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe Some(idcard)
          result.address mustBe Some(ukAddress)
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }

      }

      "5mld" when {

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

            val result = mapper(userAnswers, provisional = true).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe Some(true)
            result.income mustBe None
            result.countryOfResidence mustBe None
            result.nationality mustBe None
            result.mentalCapacityYesNo mustBe Some(true)
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

            val result = mapper(userAnswers, provisional = true).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe Some(true)
            result.income mustBe None
            result.countryOfResidence mustBe Some("GB")
            result.nationality mustBe Some("GB")
            result.mentalCapacityYesNo mustBe Some(false)
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

            val result = mapper(userAnswers, provisional = true).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe None
            result.income mustBe None
            result.countryOfResidence mustBe None
            result.nationality mustBe None
            result.mentalCapacityYesNo mustBe Some(true)
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

            val result = mapper(userAnswers, provisional = true).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe None
            result.income mustBe None
            result.countryOfResidence mustBe Some("GB")
            result.nationality mustBe Some("GB")
            result.mentalCapacityYesNo mustBe Some(false)
            result.identification mustBe None
            result.address mustBe None
            result.vulnerableYesNo mustBe None
            result.entityStart mustBe startDate
          }

        }

      }

    }

    "amending" must {

      "4mld" when {

        "return None for empty user answers" in {

          val result = mapper(emptyUserAnswers, provisional = false)
          result mustBe None
        }

        "generate class of individual model with passport or ID card" in {

          val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

          val passport = CombinedPassportOrIdCard("SP", "123456789", LocalDate.of(2024, 8, 16))

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, true).success.value
            .set(DateOfBirthPage, dateOfBirth).success.value
            .set(IncomeDiscretionYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, false).success.value
            .set(AddressYesNoPage, true).success.value
            .set(LiveInTheUkYesNoPage, false).success.value
            .set(NonUkAddressPage, nonUkAddress).success.value
            .set(PassportOrIdCardDetailsYesNoPage, true).success.value
            .set(PassportOrIdCardDetailsPage, passport).success.value
            .set(VPE1FormYesNoPage, false).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers, provisional = false).get

          result.name mustBe name
          result.roleInCompany mustBe None
          result.dateOfBirth mustBe Some(dateOfBirth)
          result.incomeDiscretionYesNo mustBe Some(true)
          result.income mustBe None
          result.countryOfResidence mustBe None
          result.nationality mustBe None
          result.mentalCapacityYesNo mustBe None
          result.identification mustBe Some(passport)
          result.address mustBe Some(nonUkAddress)
          result.vulnerableYesNo mustBe Some(false)
          result.entityStart mustBe startDate
        }

      }

      "5mld" when {

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

            val result = mapper(userAnswers, provisional = false).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe Some(true)
            result.income mustBe None
            result.countryOfResidence mustBe None
            result.nationality mustBe None
            result.mentalCapacityYesNo mustBe Some(true)
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

            val result = mapper(userAnswers, provisional = false).get

            result.name mustBe name
            result.roleInCompany mustBe None
            result.dateOfBirth mustBe Some(dateOfBirth)
            result.incomeDiscretionYesNo mustBe None
            result.income mustBe None
            result.countryOfResidence mustBe Some("GB")
            result.nationality mustBe Some("GB")
            result.mentalCapacityYesNo mustBe Some(false)
            result.identification mustBe None
            result.address mustBe None
            result.vulnerableYesNo mustBe None
            result.entityStart mustBe startDate
          }

        }

      }

    }
  }
}
