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

package utils.mappers

import base.SpecBase
import models.beneficiaries.OtherBeneficiary
import models.{NonUkAddress, UkAddress}
import pages.other._
import pages.other.add.StartDatePage
import utils.Constants.GB

import java.time.LocalDate

class OtherBeneficiaryMapperSpec extends SpecBase {

  val description: String = "Other"
  val share: Int = 50
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val country: String = "FR"
  val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, country)

  "OtherBeneficiaryMapper" must {

    val mapper = injector.instanceOf[OtherBeneficiaryMapper]

    "return None for empty user answers" in {

      val result = mapper(emptyUserAnswers)
      result mustBe None
    }

    "generate other beneficiary model" when {

        "taxable" when {

          "no country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage, description).success.value
              .set(DiscretionYesNoPage, true).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value
              .set(AddressYesNoPage, false).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe OtherBeneficiary(
              description = description,
              address = None,
              income = None,
              incomeDiscretionYesNo = Some(true),
              countryOfResidence = None,
              entityStart = date,
              provisional = true
            )
          }

          "UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage, description).success.value
              .set(DiscretionYesNoPage, false).success.value
              .set(ShareOfIncomePage, share).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, true).success.value
              .set(AddressYesNoPage, true).success.value
              .set(AddressUkYesNoPage, true).success.value
              .set(UkAddressPage, ukAddress).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe OtherBeneficiary(
              description = description,
              address = Some(ukAddress),
              income = Some(share.toString),
              incomeDiscretionYesNo = Some(false),
              countryOfResidence = Some(GB),
              entityStart = date,
              provisional = true
            )
          }

          "non-UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage, description).success.value
              .set(DiscretionYesNoPage, false).success.value
              .set(ShareOfIncomePage, share).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, false).success.value
              .set(CountryOfResidencePage, country).success.value
              .set(AddressYesNoPage, true).success.value
              .set(AddressUkYesNoPage, false).success.value
              .set(NonUkAddressPage, nonUkAddress).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe OtherBeneficiary(
              description = description,
              address = Some(nonUkAddress),
              income = Some(share.toString),
              incomeDiscretionYesNo = Some(false),
              countryOfResidence = Some(country),
              entityStart = date,
              provisional = true
            )
          }
        }

        "non-taxable" when {

          "no country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage, description).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value
              .set(AddressYesNoPage, false).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe OtherBeneficiary(
              description = description,
              address = None,
              income = None,
              incomeDiscretionYesNo = None,
              countryOfResidence = None,
              entityStart = date,
              provisional = true
            )
          }

          "UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage, description).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, true).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe OtherBeneficiary(
              description = description,
              address = None,
              income = None,
              incomeDiscretionYesNo = None,
              countryOfResidence = Some(GB),
              entityStart = date,
              provisional = true
            )
          }

          "non-UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage, description).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, false).success.value
              .set(CountryOfResidencePage, country).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe OtherBeneficiary(
              description = description,
              address = None,
              income = None,
              incomeDiscretionYesNo = None,
              countryOfResidence = Some(country),
              entityStart = date,
              provisional = true
            )
          }
        }
    }
  }
}
