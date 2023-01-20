/*
 * Copyright 2023 HM Revenue & Customs
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
import models.UserAnswers
import models.beneficiaries.TrustBeneficiary
import pages.charityortrust.trust._
import utils.Constants.GB

import java.time.LocalDate

class TrustBeneficiaryExtractorSpec extends SpecBase {

  private val index: Int = 0

  private val name: String = "Trust"
  private val income: Int = 50
  private val country: String = "FR"
  private val date: LocalDate = LocalDate.parse("1996-02-03")

  private val extractor: TrustBeneficiaryExtractor = injector.instanceOf[TrustBeneficiaryExtractor]

  "TrustBeneficiaryExtractor" must {

    "Populate user answers" when {

        "taxable" when {

          "underlying trust data is 4mld" when {

            val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = true, isUnderlyingData5mld = false)

            "has no country of residence" in {

              val beneficiary = TrustBeneficiary(
                name = name,
                utr = None,
                address = None,
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = None,
                entityStart = date,
                provisional = false
              )

              val result = extractor.apply(baseAnswers, beneficiary, index).get

              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(DiscretionYesNoPage).get mustBe true
              result.get(ShareOfIncomePage) mustBe None
              result.get(CountryOfResidenceYesNoPage) mustBe None
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(AddressUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }
          }

          "underlying trust data is 5mld" when {

            val baseAnswers = emptyUserAnswers.copy(isTaxable = true, isUnderlyingData5mld = true)

            "has no country of residence" in {

              val beneficiary = TrustBeneficiary(
                name = name,
                utr = None,
                address = None,
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = None,
                entityStart = date,
                provisional = false
              )

              val result = extractor.apply(baseAnswers, beneficiary, index).get

              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(DiscretionYesNoPage).get mustBe true
              result.get(ShareOfIncomePage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe false
              result.get(CountryOfResidenceUkYesNoPage) mustBe None
              result.get(CountryOfResidencePage) mustBe None
              result.get(AddressYesNoPage).get mustBe false
              result.get(AddressUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }

            "has UK country of residence" in {

              val beneficiary = TrustBeneficiary(
                name = name,
                utr = None,
                address = None,
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = Some(GB),
                entityStart = date,
                provisional = false
              )

              val result = extractor.apply(baseAnswers, beneficiary, index).get

              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(DiscretionYesNoPage).get mustBe true
              result.get(ShareOfIncomePage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe true
              result.get(CountryOfResidencePage).get mustBe GB
              result.get(AddressYesNoPage).get mustBe false
              result.get(AddressUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }

            "has non-UK country of residence" in {

              val beneficiary = TrustBeneficiary(
                name = name,
                utr = None,
                address = None,
                income = None,
                incomeDiscretionYesNo = Some(true),
                countryOfResidence = Some(country),
                entityStart = date,
                provisional = false
              )

              val result = extractor.apply(baseAnswers, beneficiary, index).get

              result.get(NamePage).get mustBe name
              result.get(UtrPage) mustBe None
              result.get(DiscretionYesNoPage).get mustBe true
              result.get(ShareOfIncomePage) mustBe None
              result.get(CountryOfResidenceYesNoPage).get mustBe true
              result.get(CountryOfResidenceUkYesNoPage).get mustBe false
              result.get(CountryOfResidencePage).get mustBe country
              result.get(AddressYesNoPage).get mustBe false
              result.get(AddressUkYesNoPage) mustBe None
              result.get(UkAddressPage) mustBe None
              result.get(NonUkAddressPage) mustBe None
              result.get(StartDatePage).get mustBe date
              result.get(IndexPage).get mustBe index
            }
          }
        }

      "non-taxable" when {

        val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = false, isUnderlyingData5mld = true)

        "has no country of residence" in {

          val beneficiary = TrustBeneficiary(
            name = name,
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = None,
            countryOfResidence = None,
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(baseAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(DiscretionYesNoPage) mustBe None
          result.get(ShareOfIncomePage) mustBe None
          result.get(CountryOfResidenceYesNoPage).get mustBe false
          result.get(CountryOfResidenceUkYesNoPage) mustBe None
          result.get(CountryOfResidencePage) mustBe None
          result.get(AddressYesNoPage) mustBe None
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }

        "has country of residence" in {

          val beneficiary = TrustBeneficiary(
            name = name,
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = None,
            countryOfResidence = Some(country),
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(baseAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(DiscretionYesNoPage) mustBe None
          result.get(ShareOfIncomePage) mustBe None
          result.get(CountryOfResidenceYesNoPage).get mustBe true
          result.get(CountryOfResidenceUkYesNoPage).get mustBe false
          result.get(CountryOfResidencePage).get mustBe country
          result.get(AddressYesNoPage) mustBe None
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }
      }

      "migrating from non-taxable to taxable" when {

        val baseAnswers: UserAnswers = emptyUserAnswers.copy(isUnderlyingData5mld = true, migratingFromNonTaxableToTaxable = true)

        "discretion and income undefined" in {

          val beneficiary = TrustBeneficiary(
            name = name,
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = None,
            countryOfResidence = Some(country),
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(baseAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(DiscretionYesNoPage) mustBe None
          result.get(ShareOfIncomePage) mustBe None
          result.get(CountryOfResidenceYesNoPage).get mustBe true
          result.get(CountryOfResidenceUkYesNoPage).get mustBe false
          result.get(CountryOfResidencePage).get mustBe country
          result.get(AddressYesNoPage).get mustBe false
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }

        "has discretion" in {

          val beneficiary = TrustBeneficiary(
            name = name,
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = Some(true),
            countryOfResidence = Some(country),
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(baseAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(DiscretionYesNoPage).get mustBe true
          result.get(ShareOfIncomePage) mustBe None
          result.get(CountryOfResidenceYesNoPage).get mustBe true
          result.get(CountryOfResidenceUkYesNoPage).get mustBe false
          result.get(CountryOfResidencePage).get mustBe country
          result.get(AddressYesNoPage).get mustBe false
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }

        "has income" in {

          val beneficiary = TrustBeneficiary(
            name = name,
            utr = None,
            address = None,
            income = Some(income.toString),
            incomeDiscretionYesNo = Some(false),
            countryOfResidence = Some(country),
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(baseAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(DiscretionYesNoPage).get mustBe false
          result.get(ShareOfIncomePage).get mustBe income
          result.get(CountryOfResidenceYesNoPage).get mustBe true
          result.get(CountryOfResidenceUkYesNoPage).get mustBe false
          result.get(CountryOfResidencePage).get mustBe country
          result.get(AddressYesNoPage).get mustBe false
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }
      }
    }
  }
}
