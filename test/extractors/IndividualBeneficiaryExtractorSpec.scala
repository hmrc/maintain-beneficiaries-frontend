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
import models.{CombinedPassportOrIdCard, Name, NationalInsuranceNumber, NonUkAddress, UkAddress, UserAnswers}
import pages.individualbeneficiary._
import pages.individualbeneficiary.amend.{IndexPage, PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import java.time.LocalDate
import utils.Constants.GB

import pages.individualbeneficiary.add.StartDatePage

class IndividualBeneficiaryExtractorSpec extends SpecBase {

  private val index = 0
  private val name: Name = Name("First", None, "Last")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
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

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(DateOfBirthYesNoPage).get mustBe true
          result.get(DateOfBirthPage).get mustBe date
          result.get(CountryOfNationalityYesNoPage) mustBe None
          result.get(CountryOfNationalityUkYesNoPage) mustBe None
          result.get(CountryOfNationalityPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(NationalInsuranceNumberYesNoPage).get mustBe false
          result.get(NationalInsuranceNumberPage) mustBe None
          result.get(AddressYesNoPage).get mustBe false
          result.get(LiveInTheUkYesNoPage) mustNot be(defined)
          result.get(UkAddressPage) mustNot be(defined)
          result.get(NonUkAddressPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index

        }

        "should populate user answers when an individual has a NINO" in {

          val nino = NationalInsuranceNumber("nino")

          val individual = IndividualBeneficiary(
            name = name,
            dateOfBirth = Some(date),
            identification = Some(nino),
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

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(DateOfBirthYesNoPage).get mustBe true
          result.get(DateOfBirthPage).get mustBe date
          result.get(CountryOfNationalityYesNoPage) mustBe None
          result.get(CountryOfNationalityUkYesNoPage) mustBe None
          result.get(CountryOfNationalityPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(NationalInsuranceNumberYesNoPage).get mustBe true
          result.get(NationalInsuranceNumberPage).get mustBe "nino"
          result.get(AddressYesNoPage).get mustBe false
          result.get(LiveInTheUkYesNoPage) mustNot be(defined)
          result.get(UkAddressPage) mustNot be(defined)
          result.get(NonUkAddressPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index

        }

        "should populate user answers when individual has a role in the company" in {
          val nino = NationalInsuranceNumber("nino")

          val individual = IndividualBeneficiary(
            name = name,
            dateOfBirth = Some(date),
            identification = Some(nino),
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

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(DateOfBirthYesNoPage).get mustBe true
          result.get(DateOfBirthPage).get mustBe date
          result.get(CountryOfNationalityYesNoPage) mustBe None
          result.get(CountryOfNationalityUkYesNoPage) mustBe None
          result.get(CountryOfNationalityPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(RoleInCompanyPage).get mustBe RoleInCompany.Director
          result.get(NationalInsuranceNumberYesNoPage).get mustBe true
          result.get(NationalInsuranceNumberPage).get mustBe "nino"
          result.get(AddressYesNoPage).get mustBe false
          result.get(LiveInTheUkYesNoPage) mustNot be(defined)
          result.get(UkAddressPage) mustNot be(defined)
          result.get(NonUkAddressPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(DateOfBirthYesNoPage).get mustBe true
          result.get(DateOfBirthPage).get mustBe date
          result.get(CountryOfNationalityYesNoPage) mustBe None
          result.get(CountryOfNationalityUkYesNoPage) mustBe None
          result.get(CountryOfNationalityPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(NationalInsuranceNumberYesNoPage).get mustBe false
          result.get(NationalInsuranceNumberPage) mustNot be(defined)
          result.get(AddressYesNoPage).get mustBe true
          result.get(LiveInTheUkYesNoPage).get mustBe true
          result.get(UkAddressPage).get mustBe ukAddress
          result.get(NonUkAddressPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsYesNoPage).get mustBe true
          result.get(PassportOrIdCardDetailsPage).get mustBe combined
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

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(DateOfBirthYesNoPage).get mustBe true
          result.get(DateOfBirthPage).get mustBe date
          result.get(CountryOfNationalityYesNoPage) mustBe None
          result.get(CountryOfNationalityUkYesNoPage) mustBe None
          result.get(CountryOfNationalityPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(NationalInsuranceNumberYesNoPage).get mustBe false
          result.get(NationalInsuranceNumberPage) mustNot be(defined)
          result.get(AddressYesNoPage).get mustBe true
          result.get(LiveInTheUkYesNoPage).get mustBe false
          result.get(UkAddressPage) mustNot be(defined)
          result.get(NonUkAddressPage).get mustBe nonUkAddress
          result.get(PassportOrIdCardDetailsYesNoPage).get mustBe false
          result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(DateOfBirthYesNoPage).get mustBe false
          result.get(DateOfBirthPage) mustNot be(defined)
          result.get(CountryOfNationalityYesNoPage) mustBe None
          result.get(CountryOfNationalityUkYesNoPage) mustBe None
          result.get(CountryOfNationalityPage) mustBe None
          result.get(CountryOfResidenceYesNoPage) mustBe None
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(MentalCapacityYesNoPage) mustBe None
          result.get(NationalInsuranceNumberYesNoPage).get mustBe false
          result.get(NationalInsuranceNumberPage) mustNot be(defined)
          result.get(AddressYesNoPage).get mustBe false
          result.get(LiveInTheUkYesNoPage) mustNot be(defined)
          result.get(UkAddressPage) mustNot be(defined)
          result.get(NonUkAddressPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
          result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(CountryOfNationalityYesNoPage) mustBe None
              result.get(CountryOfNationalityUkYesNoPage) mustBe None
              result.get(CountryOfNationalityPage) mustBe None
              result.get(CountryOfResidenceYesNoPage) mustBe None
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(MentalCapacityYesNoPage) mustBe None
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustNot be(defined)
              result.get(UkAddressPage) mustNot be(defined)
              result.get(NonUkAddressPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(CountryOfNationalityYesNoPage).get mustBe false
              result.get(CountryOfNationalityUkYesNoPage) mustBe None
              result.get(CountryOfNationalityPage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe false
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(MentalCapacityYesNoPage) mustBe None
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustNot be(defined)
              result.get(UkAddressPage) mustNot be(defined)
              result.get(NonUkAddressPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = Some(GB),
                nationality = Some(GB),
                mentalCapacityYesNo = Some(true),
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, individual, index).get

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(CountryOfNationalityYesNoPage).get mustBe true
              result.get(CountryOfNationalityUkYesNoPage).get mustBe true
              result.get(CountryOfNationalityPage).get mustBe GB
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe true
              result.get(CountryOfResidencePage).get mustBe GB
              result.get(MentalCapacityYesNoPage).get mustBe true
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustNot be(defined)
              result.get(UkAddressPage) mustNot be(defined)
              result.get(NonUkAddressPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(DateOfBirthYesNoPage).get mustBe true
              result.get(DateOfBirthPage).get mustBe date
              result.get(CountryOfNationalityYesNoPage).get mustBe true
              result.get(CountryOfNationalityUkYesNoPage).get mustBe false
              result.get(CountryOfNationalityPage).get mustBe country
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe false
              result.get(CountryOfResidencePage).get mustBe country
              result.get(MentalCapacityYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberYesNoPage).get mustBe false
              result.get(NationalInsuranceNumberPage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(LiveInTheUkYesNoPage) mustNot be(defined)
              result.get(UkAddressPage) mustNot be(defined)
              result.get(NonUkAddressPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
              result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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

            result.get(IndexPage).get mustBe index
            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(CountryOfNationalityYesNoPage).get mustBe false
            result.get(CountryOfNationalityUkYesNoPage) mustBe None
            result.get(CountryOfNationalityPage) mustBe None
            result.get(CountryOfResidenceYesNoPage).get mustBe false
            result.get(CountryOfResidenceUkYesNoPage) mustBe None
            result.get(CountryOfResidencePage) mustBe None
            result.get(MentalCapacityYesNoPage) mustBe None
            result.get(NationalInsuranceNumberYesNoPage) mustNot be(defined)
            result.get(NationalInsuranceNumberPage) mustNot be(defined)
            result.get(AddressYesNoPage) mustNot be(defined)
            result.get(LiveInTheUkYesNoPage) mustNot be(defined)
            result.get(UkAddressPage) mustNot be(defined)
            result.get(NonUkAddressPage) mustNot be(defined)
            result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
            result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index

          }


          "has UK country of nationality,  UK country of residence and mental capacity" in {

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

            result.get(IndexPage).get mustBe index
            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(CountryOfNationalityYesNoPage).get mustBe true
            result.get(CountryOfNationalityUkYesNoPage).get mustBe true
            result.get(CountryOfNationalityPage).get mustBe GB
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe true
            result.get(CountryOfResidencePage).get mustBe GB
            result.get(MentalCapacityYesNoPage).get mustBe true
            result.get(NationalInsuranceNumberYesNoPage) mustNot be(defined)
            result.get(NationalInsuranceNumberPage) mustNot be(defined)
            result.get(AddressYesNoPage) mustNot be(defined)
            result.get(LiveInTheUkYesNoPage) mustNot be(defined)
            result.get(UkAddressPage) mustNot be(defined)
            result.get(NonUkAddressPage) mustNot be(defined)
            result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
            result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
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

            result.get(IndexPage).get mustBe index
            result.get(NamePage).get mustBe name
            result.get(DateOfBirthYesNoPage).get mustBe true
            result.get(DateOfBirthPage).get mustBe date
            result.get(CountryOfNationalityYesNoPage).get mustBe true
            result.get(CountryOfNationalityUkYesNoPage).get mustBe false
            result.get(CountryOfNationalityPage).get mustBe country
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe false
            result.get(CountryOfResidencePage).get mustBe country
            result.get(MentalCapacityYesNoPage).get mustBe false
            result.get(NationalInsuranceNumberYesNoPage) mustNot be(defined)
            result.get(NationalInsuranceNumberPage) mustNot be(defined)
            result.get(AddressYesNoPage) mustNot be(defined)
            result.get(LiveInTheUkYesNoPage) mustNot be(defined)
            result.get(UkAddressPage) mustNot be(defined)
            result.get(NonUkAddressPage) mustNot be(defined)
            result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
            result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
            result.get(StartDatePage).get mustBe date
            result.get(IndexPage).get mustBe index

          }

        }

      }

    }
  }

}
