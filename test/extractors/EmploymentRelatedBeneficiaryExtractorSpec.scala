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
import utils.Constants.GB
import models.HowManyBeneficiaries.Over1
import models.beneficiaries.EmploymentRelatedBeneficiary
import models.{Description, HowManyBeneficiaries, NonUkAddress, UkAddress, UserAnswers}
import pages.companyoremploymentrelated.employment._

import java.time.LocalDate

class EmploymentRelatedBeneficiaryExtractorSpec extends SpecBase {

  private val index: Int = 0

  private val name: String = "Employment Related Name"
  private val utr: String = "1234567890"
  private val description: Description = Description("Employment Related Description", None,  None, None, None)
  private val howManyBeneficiaries: HowManyBeneficiaries = Over1
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  private val country: String = "DE"
  private val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, country)

  private val extractor = new EmploymentRelatedBeneficiaryExtractor()

  "EmploymentRelatedBeneficiaryExtractor" must {

    "Populate user answers" when {

      "4mld" when {

        val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true, isUnderlyingData5mld = false)

        "has no address" in {

          val beneficiary = EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = None,
            description = description,
            howManyBeneficiaries = howManyBeneficiaries,
            entityStart = date,
            provisional = true
          )

          val result = extractor(baseAnswers, beneficiary, index).get

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(AddressYesNoPage).get mustBe false
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(DescriptionPage).get mustBe description
          result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
        }

        "has a UK address" in {

          val beneficiary = EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = Some(ukAddress),
            description = description,
            howManyBeneficiaries = howManyBeneficiaries,
            entityStart = date,
            provisional = true
          )

          val result = extractor(baseAnswers, beneficiary, index).get

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(AddressYesNoPage).get mustBe true
          result.get(AddressUkYesNoPage).get mustBe true
          result.get(UkAddressPage).get mustBe ukAddress
          result.get(NonUkAddressPage) mustBe None
          result.get(DescriptionPage).get mustBe description
          result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
        }

        "has a non UK address" in {

          val beneficiary = EmploymentRelatedBeneficiary(
            name = name,
            utr = None,
            address = Some(nonUkAddress),
            description = description,
            howManyBeneficiaries = howManyBeneficiaries,
            entityStart = date,
            provisional = true
          )

          val result = extractor(baseAnswers, beneficiary, index).get

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(AddressYesNoPage).get mustBe true
          result.get(AddressUkYesNoPage).get mustBe false
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage).get mustBe nonUkAddress
          result.get(DescriptionPage).get mustBe description
          result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
        }

        "has a UTR" in {

          val beneficiary = EmploymentRelatedBeneficiary(
            name = name,
            utr = Some(utr),
            address = None,
            description = description,
            howManyBeneficiaries = howManyBeneficiaries,
            entityStart = date,
            provisional = true
          )

          val result = extractor(baseAnswers, beneficiary, index).get

          result.get(IndexPage).get mustBe index
          result.get(NamePage).get mustBe name
          result.get(UtrPage).get mustBe utr
          result.get(AddressYesNoPage).get mustBe false
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(DescriptionPage).get mustBe description
          result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
        }
      }

      "5mld" when {

        "taxable" when {

          "underlying trust data is 4mld" when {

            val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true, isUnderlyingData5mld = false)

            "has no country of residence" in {

              val beneficiary = EmploymentRelatedBeneficiary(
                name = name,
                utr = None,
                address = None,
                description = description,
                howManyBeneficiaries = howManyBeneficiaries,
                countryOfResidence = None,
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, beneficiary, index).get

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(CountryOfResidenceYesNoPage) mustBe None
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(AddressUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(DescriptionPage).get mustBe description
              result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
            }
          }

          "underlying trust data is 5mld" when {

            val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true, isUnderlyingData5mld = true)

            "has no country of residence" in {

              val beneficiary = EmploymentRelatedBeneficiary(
                name = name,
                utr = None,
                address = None,
                description = description,
                howManyBeneficiaries = howManyBeneficiaries,
                countryOfResidence = None,
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, beneficiary, index).get

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe false
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(AddressUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(DescriptionPage).get mustBe description
              result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
            }

            "has UK country of residence" in {

              val beneficiary = EmploymentRelatedBeneficiary(
                name = name,
                utr = None,
                address = Some(ukAddress),
                description = description,
                howManyBeneficiaries = howManyBeneficiaries,
                countryOfResidence = Some(GB),
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, beneficiary, index).get

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe true
              result.get(CountryOfResidencePage).get mustBe GB
              result.get(AddressYesNoPage).get mustBe true
              result.get(AddressUkYesNoPage).get mustBe true
              result.get(UkAddressPage).get mustBe ukAddress
              result.get(NonUkAddressPage) mustBe None
              result.get(DescriptionPage).get mustBe description
              result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
            }

            "has non-UK country of residence" in {

              val beneficiary = EmploymentRelatedBeneficiary(
                name = name,
                utr = None,
                address = Some(nonUkAddress),
                description = description,
                howManyBeneficiaries = howManyBeneficiaries,
                countryOfResidence = Some(country),
                entityStart = date,
                provisional = true
              )

              val result = extractor(baseAnswers, beneficiary, index).get

              result.get(IndexPage).get mustBe index
              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe false
              result.get(CountryOfResidencePage).get mustBe country
              result.get(AddressYesNoPage).get mustBe true
              result.get(AddressUkYesNoPage).get mustBe false
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage).get mustBe nonUkAddress
              result.get(DescriptionPage).get mustBe description
              result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
            }
          }
        }

        "non-taxable" when {

          val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false, isUnderlyingData5mld = true)

          "has no country of residence" in {

            val beneficiary = EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = None,
              description = description,
              howManyBeneficiaries = howManyBeneficiaries,
              countryOfResidence = None,
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, beneficiary, index).get

            result.get(IndexPage).get mustBe index
            result.get(NamePage).get mustBe name
            result.get(UtrPage) mustBe None
            result.get(CountryOfResidenceYesNoPage).get mustBe false
            result.get(CountryOfResidenceUkYesNoPage) mustBe None
            result.get(CountryOfResidencePage) mustBe None
            result.get(AddressYesNoPage) mustBe None
            result.get(AddressUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(DescriptionPage).get mustBe description
            result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
          }

          "has country of residence" in {

            val beneficiary = EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = None,
              description = description,
              howManyBeneficiaries = howManyBeneficiaries,
              countryOfResidence = Some(country),
              entityStart = date,
              provisional = true
            )

            val result = extractor(baseAnswers, beneficiary, index).get

            result.get(IndexPage).get mustBe index
            result.get(NamePage).get mustBe name
            result.get(UtrPage) mustBe None
            result.get(CountryOfResidenceYesNoPage).get mustBe true
            result.get(CountryOfResidenceUkYesNoPage).get mustBe false
            result.get(CountryOfResidencePage).get mustBe country
            result.get(AddressYesNoPage) mustBe None
            result.get(AddressUkYesNoPage) mustBe None
            result.get(UkAddressPage) mustBe None
            result.get(NonUkAddressPage) mustBe None
            result.get(DescriptionPage).get mustBe description
            result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
          }
        }
      }
    }
  }
}
