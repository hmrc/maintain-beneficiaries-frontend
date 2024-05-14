/*
 * Copyright 2024 HM Revenue & Customs
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

package extractors

import base.SpecBase
import models.YesNoDontKnow.{DontKnow, No, Yes}
import models.beneficiaries.IndividualBeneficiary
import models.{CombinedPassportOrIdCard, IdCard, Name, Passport, UserAnswers}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add._
import pages.individualbeneficiary.amend._
import utils.Constants.GB

import java.time.LocalDate

class IndividualBeneficiaryExtractorSpec extends SpecBase {

  private val index = 0
  private val name: Name = Name("First", None, "Last")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val income: Int = 50
  private val country: String = "FR"

  private val extractor = new IndividualBeneficiaryExtractor()

  "IndividualBeneficiaryExtractor" must {

    "Populate user answers" when {

        "taxable" when {

          "underlying trust data is 4mld" when {

            val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = true, isUnderlyingData5mld = false)

            "has no country of nationality, no country of residence and no mental capacity" in {

              val individual = IndividualBeneficiary(
                name = name,
                dateOfBirth = Some(date),
                identification = None,
                address = None,
                vulnerableYesNo = Some(false),
                roleInCompany = None,
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = None,
                nationality = None,
                mentalCapacityYesNo = None,
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, individual, index).get

              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(IncomeDiscretionYesNoPage).get mustBe true
              result.get(IncomePercentagePage) mustBe None
              result.get(CountryOfNationalityYesNoPage) mustBe None
              result.get(CountryOfNationalityUkYesNoPage) mustBe None
              result.get(CountryOfNationalityPage) mustBe None
              result.get(CountryOfResidenceYesNoPage) mustBe None
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(PassportDetailsYesNoPage) mustBe None
              result.get(PassportDetailsPage) mustBe None
              result.get(IdCardDetailsYesNoPage) mustBe None
              result.get(IdCardDetailsPage) mustBe None
              result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
              result.get(PassportOrIdCardDetailsPage) mustBe None
              result.get(MentalCapacityYesNoPage) mustBe None
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }
          }

          "underlying trust data is 5mld" when {

            val baseAnswers = emptyUserAnswers.copy(isTaxable = true, isUnderlyingData5mld = true)

            "has no country of nationality, no country of residence and no mental capacity" in {

              val individual = IndividualBeneficiary(
                name = name,
                dateOfBirth = Some(date),
                identification = None,
                address = None,
                vulnerableYesNo = Some(false),
                roleInCompany = None,
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = None,
                nationality = None,
                mentalCapacityYesNo = None,
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, individual, index).get

              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(IncomeDiscretionYesNoPage).get mustBe true
              result.get(IncomePercentagePage) mustBe None
              result.get(CountryOfNationalityYesNoPage).get mustBe false
              result.get(CountryOfNationalityUkYesNoPage) mustBe None
              result.get(CountryOfNationalityPage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe false
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(PassportDetailsYesNoPage) mustBe None
              result.get(PassportDetailsPage) mustBe None
              result.get(IdCardDetailsYesNoPage) mustBe None
              result.get(IdCardDetailsPage) mustBe None
              result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
              result.get(PassportOrIdCardDetailsPage) mustBe None
              result.get(MentalCapacityYesNoPage) mustBe None
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }

            "has UK country of nationality,  UK country of residence and mental capacity" in {

              val individual = IndividualBeneficiary(
                name = name,
                dateOfBirth = Some(date),
                identification = None,
                address = None,
                vulnerableYesNo = Some(false),
                roleInCompany = None,
                income = Some(income.toString),
                incomeDiscretionYesNo = Some(false),
                countryOfResidence = Some(GB),
                nationality = Some(GB),
                mentalCapacityYesNo = Some(Yes),
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, individual, index).get

              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(IncomeDiscretionYesNoPage).get mustBe false
              result.get(IncomePercentagePage).get mustBe income
              result.get(CountryOfNationalityYesNoPage).get mustBe true
              result.get(CountryOfNationalityUkYesNoPage).get mustBe true
              result.get(CountryOfNationalityPage).get mustBe GB
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe true
              result.get(CountryOfResidencePage).get mustBe GB
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(PassportDetailsYesNoPage) mustBe None
              result.get(PassportDetailsPage) mustBe None
              result.get(IdCardDetailsYesNoPage) mustBe None
              result.get(IdCardDetailsPage) mustBe None
              result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
              result.get(PassportOrIdCardDetailsPage) mustBe None
              result.get(MentalCapacityYesNoPage).get mustBe Yes
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }

              "has UK country of nationality,  UK country of residence and don't know mental capacity" in {

                val individual = IndividualBeneficiary(
                  name = name,
                  dateOfBirth = Some(date),
                  identification = None,
                  address = None,
                  vulnerableYesNo = Some(false),
                  roleInCompany = None,
                  income = Some(income.toString),
                  incomeDiscretionYesNo = Some(false),
                  countryOfResidence = Some(GB),
                  nationality = Some(GB),
                  mentalCapacityYesNo = Some(DontKnow),
                  entityStart = date,
                  provisional = true
                )

                val result = extractor(baseAnswers, individual, index).get

                result.get(NamePage).get mustBe name
                result.get(DateOfBirthYesNoPage).get mustBe true
                result.get(DateOfBirthPage).get mustBe date
                result.get(IncomeDiscretionYesNoPage).get mustBe false
                result.get(IncomePercentagePage).get mustBe income
                result.get(CountryOfNationalityYesNoPage).get mustBe true
                result.get(CountryOfNationalityUkYesNoPage).get mustBe true
                result.get(CountryOfNationalityPage).get mustBe GB
                result.get(CountryOfResidenceYesNoPage).get mustBe true
                result.get(CountryOfResidenceUkYesNoPage).get mustBe true
                result.get(CountryOfResidencePage).get mustBe GB
                result.get(NationalInsuranceNumberYesNoPage).get mustBe false
                result.get(NationalInsuranceNumberPage) mustBe None
                result.get(AddressYesNoPage).get mustBe false
                result.get(LiveInTheUkYesNoPage) mustBe None
                result.get(UkAddressPage) mustBe None
                result.get(NonUkAddressPage) mustBe None
                result.get(PassportDetailsYesNoPage) mustBe None
                result.get(PassportDetailsPage) mustBe None
                result.get(IdCardDetailsYesNoPage) mustBe None
                result.get(IdCardDetailsPage) mustBe None
                result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
                result.get(PassportOrIdCardDetailsPage) mustBe None
                result.get(MentalCapacityYesNoPage).get mustBe DontKnow
                result.get(StartDatePage).get mustBe date
                result.get(IndexPage).get mustBe index
              }

            "has non-UK country of nationality, on-UK country of residence and no mental capacity" in {

              val individual = IndividualBeneficiary(
                name = name,
                dateOfBirth = Some(date),
                identification = None,
                address = None,
                vulnerableYesNo = Some(false),
                roleInCompany = None,
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = Some(country),
                nationality = Some(country),
                mentalCapacityYesNo = Some(No),
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, individual, index).get

              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(IncomeDiscretionYesNoPage).get mustBe true
              result.get(IncomePercentagePage) mustBe None
              result.get(CountryOfNationalityYesNoPage).get mustBe true
              result.get(CountryOfNationalityUkYesNoPage).get mustBe false
              result.get(CountryOfNationalityPage).get mustBe country
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe false
              result.get(CountryOfResidencePage).get mustBe country
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(PassportDetailsYesNoPage) mustBe None
              result.get(PassportDetailsPage) mustBe None
              result.get(IdCardDetailsYesNoPage) mustBe None
              result.get(IdCardDetailsPage) mustBe None
              result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
              result.get(PassportOrIdCardDetailsPage) mustBe None
              result.get(MentalCapacityYesNoPage).get mustBe No
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }
          }
        }

        "non-taxable" when {

          val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = false, isUnderlyingData5mld = true)

          "has no country of nationality, no country of residence and no mental capacity" in {

            val individual = IndividualBeneficiary(
              name = name,
              dateOfBirth = Some(date),
              identification = None,
              address = None,
              vulnerableYesNo = None,
              roleInCompany = None,
              income = None,
              incomeDiscretionYesNo = None,
              countryOfResidence = None,
              nationality = None,
              mentalCapacityYesNo = None,
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, individual, index).get

            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(IncomeDiscretionYesNoPage) mustBe None
            result.get(IncomePercentagePage) mustBe None
            result.get(CountryOfNationalityYesNoPage).get mustBe false
            result.get(CountryOfNationalityUkYesNoPage) mustBe None
            result.get(CountryOfNationalityPage) mustBe None
            result.get(CountryOfResidenceYesNoPage).get mustBe false
            result.get(CountryOfResidenceUkYesNoPage) mustBe None
            result.get(CountryOfResidencePage) mustBe None
            result.get(NationalInsuranceNumberYesNoPage) mustBe None
            result.get(NationalInsuranceNumberPage) mustBe None
            result.get(AddressYesNoPage) mustBe None
            result.get(LiveInTheUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(PassportDetailsYesNoPage) mustBe None
            result.get(PassportDetailsPage) mustBe None
            result.get(IdCardDetailsYesNoPage) mustBe None
            result.get(IdCardDetailsPage) mustBe None
            result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
            result.get(PassportOrIdCardDetailsPage) mustBe None
            result.get(MentalCapacityYesNoPage) mustBe None
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }

          "has UK country of nationality, UK country of residence and mental capacity" in {

            val individual = IndividualBeneficiary(
              name = name,
              dateOfBirth = Some(date),
              identification = None,
              address = None,
              vulnerableYesNo = None,
              roleInCompany = None,
              income = None,
              incomeDiscretionYesNo = None,
              countryOfResidence = Some(GB),
              nationality = Some(GB),
              mentalCapacityYesNo = Some(Yes),
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, individual, index).get

            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(IncomeDiscretionYesNoPage) mustBe None
            result.get(IncomePercentagePage) mustBe None
            result.get(CountryOfNationalityYesNoPage).get mustBe true
            result.get(CountryOfNationalityUkYesNoPage).get mustBe true
            result.get(CountryOfNationalityPage).get mustBe GB
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe true
            result.get(CountryOfResidencePage).get mustBe GB
            result.get(NationalInsuranceNumberYesNoPage) mustBe None
            result.get(NationalInsuranceNumberPage) mustBe None
            result.get(AddressYesNoPage) mustBe None
            result.get(LiveInTheUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(PassportDetailsYesNoPage) mustBe None
            result.get(PassportDetailsPage) mustBe None
            result.get(IdCardDetailsYesNoPage) mustBe None
            result.get(IdCardDetailsPage) mustBe None
            result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
            result.get(PassportOrIdCardDetailsPage) mustBe None
            result.get(MentalCapacityYesNoPage).get mustBe Yes
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }

          "has non-UK country of nationality, on-UK country of residence and no mental capacity" in {

            val individual = IndividualBeneficiary(
              name = name,
              dateOfBirth = Some(date),
              identification = None,
              address = None,
              vulnerableYesNo = None,
              roleInCompany = None,
              income = None,
              incomeDiscretionYesNo = None,
              countryOfResidence = Some(country),
              nationality = Some(country),
              mentalCapacityYesNo = Some(No),
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, individual, index).get

            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(IncomeDiscretionYesNoPage) mustBe None
            result.get(IncomePercentagePage) mustBe None
            result.get(CountryOfNationalityYesNoPage).get mustBe true
            result.get(CountryOfNationalityUkYesNoPage).get mustBe false
            result.get(CountryOfNationalityPage).get mustBe country
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe false
            result.get(CountryOfResidencePage).get mustBe country
            result.get(NationalInsuranceNumberYesNoPage) mustBe None
            result.get(NationalInsuranceNumberPage) mustBe None
            result.get(AddressYesNoPage) mustBe None
            result.get(LiveInTheUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(PassportDetailsYesNoPage) mustBe None
            result.get(PassportDetailsPage) mustBe None
            result.get(IdCardDetailsYesNoPage) mustBe None
            result.get(IdCardDetailsPage) mustBe None
            result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
            result.get(PassportOrIdCardDetailsPage) mustBe None
            result.get(MentalCapacityYesNoPage).get mustBe No
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }
        }

        "migrating from non-taxable to taxable" when {

          val baseAnswers: UserAnswers = emptyUserAnswers.copy(isUnderlyingData5mld = true, migratingFromNonTaxableToTaxable = true)

          "discretion and income undefined" in {

            val individual = IndividualBeneficiary(
              name = name,
              dateOfBirth = Some(date),
              identification = None,
              address = None,
              vulnerableYesNo = None,
              roleInCompany = None,
              income = None,
              incomeDiscretionYesNo = None,
              countryOfResidence = Some(GB),
              nationality = Some(GB),
              mentalCapacityYesNo = Some(Yes),
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, individual, index).get

            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(IncomeDiscretionYesNoPage) mustBe None
            result.get(IncomePercentagePage) mustBe None
            result.get(CountryOfNationalityYesNoPage).get mustBe true
            result.get(CountryOfNationalityUkYesNoPage).get mustBe true
            result.get(CountryOfNationalityPage).get mustBe GB
            result.get(NationalInsuranceNumberYesNoPage).get mustBe false
            result.get(NationalInsuranceNumberPage) mustBe None
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe true
            result.get(CountryOfResidencePage).get mustBe GB
            result.get(AddressYesNoPage).get mustBe false
            result.get(LiveInTheUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(PassportDetailsYesNoPage) mustBe None
            result.get(PassportDetailsPage) mustBe None
            result.get(IdCardDetailsYesNoPage) mustBe None
            result.get(IdCardDetailsPage) mustBe None
            result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
            result.get(PassportOrIdCardDetailsPage) mustBe None
            result.get(MentalCapacityYesNoPage).get mustBe Yes
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }

          "has discretion" in {

            val individual = IndividualBeneficiary(
              name = name,
              dateOfBirth = Some(date),
              identification = None,
              address = None,
              vulnerableYesNo = None,
              roleInCompany = None,
              income = None,
              incomeDiscretionYesNo = Some(true),
              countryOfResidence = Some(GB),
              nationality = Some(GB),
              mentalCapacityYesNo = Some(Yes),
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, individual, index).get

            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(IncomeDiscretionYesNoPage).get mustBe true
            result.get(IncomePercentagePage) mustBe None
            result.get(CountryOfNationalityYesNoPage).get mustBe true
            result.get(CountryOfNationalityUkYesNoPage).get mustBe true
            result.get(CountryOfNationalityPage).get mustBe GB
            result.get(NationalInsuranceNumberYesNoPage).get mustBe false
            result.get(NationalInsuranceNumberPage) mustBe None
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe true
            result.get(CountryOfResidencePage).get mustBe GB
            result.get(AddressYesNoPage).get mustBe false
            result.get(LiveInTheUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(PassportDetailsYesNoPage) mustBe None
            result.get(PassportDetailsPage) mustBe None
            result.get(IdCardDetailsYesNoPage) mustBe None
            result.get(IdCardDetailsPage) mustBe None
            result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
            result.get(PassportOrIdCardDetailsPage) mustBe None
            result.get(MentalCapacityYesNoPage).get mustBe Yes
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }

          "has income" in {

            val individual = IndividualBeneficiary(
              name = name,
              dateOfBirth = Some(date),
              identification = None,
              address = None,
              vulnerableYesNo = None,
              roleInCompany = None,
              income = Some(income.toString),
              incomeDiscretionYesNo = Some(false),
              countryOfResidence = Some(GB),
              nationality = Some(GB),
              mentalCapacityYesNo = Some(Yes),
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, individual, index).get

            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(IncomeDiscretionYesNoPage).get mustBe false
            result.get(IncomePercentagePage).get mustBe income
            result.get(CountryOfNationalityYesNoPage).get mustBe true
            result.get(CountryOfNationalityUkYesNoPage).get mustBe true
            result.get(CountryOfNationalityPage).get mustBe GB
            result.get(NationalInsuranceNumberYesNoPage).get mustBe false
            result.get(NationalInsuranceNumberPage) mustBe None
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe true
            result.get(CountryOfResidencePage).get mustBe GB
            result.get(AddressYesNoPage).get mustBe false
            result.get(LiveInTheUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(PassportDetailsYesNoPage) mustBe None
            result.get(PassportDetailsPage) mustBe None
            result.get(IdCardDetailsYesNoPage) mustBe None
            result.get(IdCardDetailsPage) mustBe None
            result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
            result.get(PassportOrIdCardDetailsPage) mustBe None
            result.get(MentalCapacityYesNoPage).get mustBe Yes
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }
        }

    }

    "id extraction" when {
      val baseAnswers = emptyUserAnswers.copy(isTaxable = true, isUnderlyingData5mld = true)

      val idCard = IdCard("country", "number", date)
      val passport = Passport("country", "number", date)

      "individual has a passport" in {
        val individual = IndividualBeneficiary(
          name = name,
          dateOfBirth = Some(date),
          identification = Some(passport),
          address = None,
          vulnerableYesNo = None,
          roleInCompany = None,
          income = None,
          incomeDiscretionYesNo = Some(true),
          countryOfResidence = Some(GB),
          nationality = Some(GB),
          mentalCapacityYesNo = Some(Yes),
          entityStart = date,
          provisional = true
        )

        val result = extractor(baseAnswers, individual, index).get

        result.get(NamePage).get mustBe name
        result.get(DateOfBirthYesNoPage).get mustBe true
        result.get(DateOfBirthPage).get mustBe date
        result.get(IncomeDiscretionYesNoPage).get mustBe true
        result.get(IncomePercentagePage) mustBe None
        result.get(CountryOfNationalityYesNoPage).get mustBe true
        result.get(CountryOfNationalityUkYesNoPage).get mustBe true
        result.get(CountryOfNationalityPage).get mustBe GB
        result.get(NationalInsuranceNumberYesNoPage).get mustBe false
        result.get(NationalInsuranceNumberPage) mustBe None
        result.get(CountryOfResidenceYesNoPage).get mustBe true
        result.get(CountryOfResidenceUkYesNoPage).get mustBe true
        result.get(CountryOfResidencePage).get mustBe GB
        result.get(AddressYesNoPage).get mustBe false
        result.get(LiveInTheUkYesNoPage) mustBe None
        result.get(UkAddressPage) mustBe None
        result.get(NonUkAddressPage) mustBe None
        result.get(PassportDetailsYesNoPage) mustBe Some(true)
        result.get(PassportDetailsPage) mustBe Some(passport)
        result.get(IdCardDetailsYesNoPage) mustBe None
        result.get(IdCardDetailsPage) mustBe None
        result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
        result.get(PassportOrIdCardDetailsPage)  mustBe None
        result.get(MentalCapacityYesNoPage).get mustBe Yes
        result.get(StartDatePage).get mustBe date
        result.get(IndexPage).get mustBe index
      }

      "individual has an ID Card" in {
        val individual = IndividualBeneficiary(
          name = name,
          dateOfBirth = Some(date),
          identification = Some(idCard),
          address = None,
          vulnerableYesNo = None,
          roleInCompany = None,
          income = None,
          incomeDiscretionYesNo = Some(true),
          countryOfResidence = Some(GB),
          nationality = Some(GB),
          mentalCapacityYesNo = Some(Yes),
          entityStart = date,
          provisional = true
        )

        val result = extractor(baseAnswers, individual, index).get

        result.get(NamePage).get mustBe name
        result.get(DateOfBirthYesNoPage).get mustBe true
        result.get(DateOfBirthPage).get mustBe date
        result.get(IncomeDiscretionYesNoPage).get mustBe true
        result.get(IncomePercentagePage) mustBe None
        result.get(CountryOfNationalityYesNoPage).get mustBe true
        result.get(CountryOfNationalityUkYesNoPage).get mustBe true
        result.get(CountryOfNationalityPage).get mustBe GB
        result.get(NationalInsuranceNumberYesNoPage).get mustBe false
        result.get(NationalInsuranceNumberPage) mustBe None
        result.get(CountryOfResidenceYesNoPage).get mustBe true
        result.get(CountryOfResidenceUkYesNoPage).get mustBe true
        result.get(CountryOfResidencePage).get mustBe GB
        result.get(AddressYesNoPage).get mustBe false
        result.get(LiveInTheUkYesNoPage) mustBe None
        result.get(UkAddressPage) mustBe None
        result.get(NonUkAddressPage) mustBe None
        result.get(PassportDetailsYesNoPage) mustBe Some(false)
        result.get(PassportDetailsPage) mustBe None
        result.get(IdCardDetailsYesNoPage) mustBe Some(true)
        result.get(IdCardDetailsPage) mustBe Some(idCard)
        result.get(PassportOrIdCardDetailsYesNoPage) mustBe None
        result.get(PassportOrIdCardDetailsPage) mustBe None
        result.get(MentalCapacityYesNoPage).get mustBe Yes
        result.get(StartDatePage).get mustBe date
        result.get(IndexPage).get mustBe index
      }

      "individual has an Combined passport ID Card" in {
        val combined = CombinedPassportOrIdCard("country", "number", date)

        val individual = IndividualBeneficiary(
          name = name,
          dateOfBirth = Some(date),
          identification = Some(combined),
          address = None,
          vulnerableYesNo = None,
          roleInCompany = None,
          income = None,
          incomeDiscretionYesNo = Some(true),
          countryOfResidence = Some(GB),
          nationality = Some(GB),
          mentalCapacityYesNo = Some(Yes),
          entityStart = date,
          provisional = true
        )

        val result = extractor(baseAnswers, individual, index).get

        result.get(NamePage).get mustBe name
        result.get(DateOfBirthYesNoPage).get mustBe true
        result.get(DateOfBirthPage).get mustBe date
        result.get(IncomeDiscretionYesNoPage).get mustBe true
        result.get(IncomePercentagePage) mustBe None
        result.get(CountryOfNationalityYesNoPage).get mustBe true
        result.get(CountryOfNationalityUkYesNoPage).get mustBe true
        result.get(CountryOfNationalityPage).get mustBe GB
        result.get(NationalInsuranceNumberYesNoPage).get mustBe false
        result.get(NationalInsuranceNumberPage) mustBe None
        result.get(CountryOfResidenceYesNoPage).get mustBe true
        result.get(CountryOfResidenceUkYesNoPage).get mustBe true
        result.get(CountryOfResidencePage).get mustBe GB
        result.get(AddressYesNoPage).get mustBe false
        result.get(LiveInTheUkYesNoPage) mustBe None
        result.get(UkAddressPage) mustBe None
        result.get(NonUkAddressPage) mustBe None
        result.get(PassportDetailsYesNoPage) mustBe None
        result.get(PassportDetailsPage) mustBe None
        result.get(IdCardDetailsYesNoPage) mustBe None
        result.get(IdCardDetailsPage) mustBe None
        result.get(PassportOrIdCardDetailsYesNoPage).get mustBe true
        result.get(PassportOrIdCardDetailsPage) mustBe Some(combined)
        result.get(MentalCapacityYesNoPage).get mustBe Yes
        result.get(StartDatePage).get mustBe date
        result.get(IndexPage).get mustBe index
      }
    }

  }
}
