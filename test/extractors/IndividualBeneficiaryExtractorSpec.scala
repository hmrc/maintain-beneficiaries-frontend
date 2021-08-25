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

package extractors

import base.SpecBase
import models.beneficiaries.{IndividualBeneficiary, RoleInCompany}
import models.{CombinedPassportOrIdCard, DetailsType, IdCard, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress, UserAnswers}
import pages.individualbeneficiary._
import pages.individualbeneficiary.amend._
import java.time.LocalDate

import utils.Constants.GB
import pages.individualbeneficiary.add._

class IndividualBeneficiaryExtractorSpec extends SpecBase {

  private val index = 0
  private val name: Name = Name("First", None, "Last")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val income: Int = 50
  private val nino = "nino"
  private val country: String = "FR"
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  private val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "FR")

  private val extractor = new IndividualBeneficiaryExtractor()

  "IndividualBeneficiaryExtractor" must {

    "Populate user answers" when {

      "4mld" when {

        val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true, isUnderlyingData5mld = false)

        "has minimal data" in {

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
          result.get(NationalInsuranceNumberYesNoPage).get mustBe false
          result.get(NationalInsuranceNumberPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
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

        "should populate user answers when an individual has a NINO" in {

          val individual = IndividualBeneficiary(
            name = name,
            dateOfBirth = Some(date),
            identification = Some(NationalInsuranceNumber(nino)),
            address = None,
            vulnerableYesNo = Some(false),
            roleInCompany = None,
            income = Some(income.toString),
            incomeDiscretionYesNo = Some(false),
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
          result.get(IncomeDiscretionYesNoPage).get mustBe false
          result.get(IncomePercentagePage).get mustBe income
          result.get(CountryOfNationalityYesNoPage) mustBe None
          result.get(CountryOfNationalityUkYesNoPage) mustBe None
          result.get(CountryOfNationalityPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(NationalInsuranceNumberYesNoPage).get mustBe true
          result.get(NationalInsuranceNumberPage).get mustBe nino
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

        "should populate user answers when individual has a role in the company" in {

          val individual = IndividualBeneficiary(
            name = name,
            dateOfBirth = Some(date),
            identification = Some(NationalInsuranceNumber(nino)),
            address = None,
            vulnerableYesNo = Some(false),
            roleInCompany = Some(RoleInCompany.Director),
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
          result.get(RoleInCompanyPage).get mustBe RoleInCompany.Director
          result.get(NationalInsuranceNumberYesNoPage).get mustBe true
          result.get(NationalInsuranceNumberPage).get mustBe nino
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

        "should populate user answers when individual has a passport/ID card" in {

          val combined = CombinedPassportOrIdCard("country", "number", date)

          val individual = IndividualBeneficiary(
            name = name,
            dateOfBirth = Some(date),
            identification = Some(combined),
            address = Some(ukAddress),
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
          result.get(AddressYesNoPage).get mustBe true
          result.get(LiveInTheUkYesNoPage).get mustBe true
          result.get(UkAddressPage).get mustBe ukAddress
          result.get(NonUkAddressPage) mustBe None
          result.get(PassportDetailsYesNoPage) mustBe None
          result.get(PassportDetailsPage) mustBe None
          result.get(IdCardDetailsYesNoPage) mustBe None
          result.get(IdCardDetailsPage) mustBe None
          result.get(PassportOrIdCardDetailsYesNoPage).get mustBe true
          result.get(PassportOrIdCardDetailsPage).get mustBe combined
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }

        "should populate user answers when individual has a non-UK address and no NINO or passport/ID card" in {

          val individual = IndividualBeneficiary(
            name = name,
            dateOfBirth = Some(date),
            identification = None,
            address = Some(nonUkAddress),
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
          result.get(AddressYesNoPage).get mustBe true
          result.get(LiveInTheUkYesNoPage).get mustBe false
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage).get mustBe nonUkAddress
          result.get(PassportDetailsYesNoPage) mustBe None
          result.get(PassportDetailsPage) mustBe None
          result.get(IdCardDetailsYesNoPage) mustBe None
          result.get(IdCardDetailsPage) mustBe None
          result.get(PassportOrIdCardDetailsYesNoPage).get mustBe false
          result.get(PassportOrIdCardDetailsPage) mustBe None
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }

        "should populate user answers when individual has no identification or address" in {

          val individual = IndividualBeneficiary(
            name = name,
            dateOfBirth = None,
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
          result.get(DateOfBirthYesNoPage).get mustBe false
          result.get(DateOfBirthPage) mustBe None
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

      "5mld" when {

        "taxable" when {

          "underlying trust data is 4mld" when {

            val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true, isUnderlyingData5mld = false)

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

            val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true, isUnderlyingData5mld = true)

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
                mentalCapacityYesNo = Some(true),
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
              result.get(MentalCapacityYesNoPage).get mustBe true
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
                mentalCapacityYesNo = Some(false),
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
              result.get(MentalCapacityYesNoPage).get mustBe false
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }
          }
        }

        "non-taxable" when {

          val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false, isUnderlyingData5mld = true)

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
              mentalCapacityYesNo = Some(true),
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
            result.get(MentalCapacityYesNoPage).get mustBe true
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
              mentalCapacityYesNo = Some(false),
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
            result.get(MentalCapacityYesNoPage).get mustBe false
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }
        }
        
        "migrating from non-taxable to taxable" when {

          val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isUnderlyingData5mld = true, migratingFromNonTaxableToTaxable = true)

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
              mentalCapacityYesNo = Some(true),
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
            result.get(MentalCapacityYesNoPage).get mustBe true
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
              mentalCapacityYesNo = Some(true),
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
            result.get(MentalCapacityYesNoPage).get mustBe true
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
              mentalCapacityYesNo = Some(true),
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
            result.get(MentalCapacityYesNoPage).get mustBe true
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index
          }
        }
      }

    }

    "id extraction" when {
      val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true, isUnderlyingData5mld = true)

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
          mentalCapacityYesNo = Some(true),
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
        result.get(PassportOrIdCardDetailsPage) mustBe Some(CombinedPassportOrIdCard("country", "number", date, DetailsType.Passport))
        result.get(MentalCapacityYesNoPage).get mustBe true
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
          mentalCapacityYesNo = Some(true),
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
        result.get(PassportOrIdCardDetailsPage) mustBe Some(CombinedPassportOrIdCard("country", "number", date, DetailsType.IdCard))
        result.get(MentalCapacityYesNoPage).get mustBe true
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
          mentalCapacityYesNo = Some(true),
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
        result.get(MentalCapacityYesNoPage).get mustBe true
        result.get(StartDatePage).get mustBe date
        result.get(IndexPage).get mustBe index
      }
    }

  }
}
