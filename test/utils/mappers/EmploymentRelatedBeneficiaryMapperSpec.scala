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

package utils.mappers

import base.SpecBase
import models.beneficiaries.EmploymentRelatedBeneficiary
import models.{Description, HowManyBeneficiaries, NonUkAddress, UkAddress}
import pages.companyoremploymentrelated.employment._
import utils.Constants.GB

import java.time.LocalDate

class EmploymentRelatedBeneficiaryMapperSpec extends SpecBase {

  val name: String = "Large"
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val country: String = "FR"
  val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, country)
  val description: Description = Description("Description", None, None, None, None)
  val numberOfBeneficiaries: HowManyBeneficiaries = HowManyBeneficiaries.Over201
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "EmploymentRelatedBeneficiaryMapper" must {

    val mapper = injector.instanceOf[EmploymentRelatedBeneficiaryMapper]

    "return None for empty user answers" in {

      val result = mapper(emptyUserAnswers)
      result mustBe None
    }

    "generate employment related beneficiary model" when {

        "taxable" when {

          "no country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value
              .set(AddressYesNoPage, false).success.value
              .set(DescriptionPage, description).success.value
              .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = None,
              description = description,
              howManyBeneficiaries = numberOfBeneficiaries,
              countryOfResidence = None,
              entityStart = date,
              provisional = true
            )
          }

          "UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, true).success.value
              .set(AddressYesNoPage, true).success.value
              .set(AddressUkYesNoPage, true).success.value
              .set(UkAddressPage, ukAddress).success.value
              .set(DescriptionPage, description).success.value
              .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = Some(ukAddress),
              description = description,
              howManyBeneficiaries = numberOfBeneficiaries,
              countryOfResidence = Some(GB),
              entityStart = date,
              provisional = true
            )
          }

          "non-UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, false).success.value
              .set(CountryOfResidencePage, country).success.value
              .set(AddressYesNoPage, true).success.value
              .set(AddressUkYesNoPage, false).success.value
              .set(NonUkAddressPage, nonUkAddress).success.value
              .set(DescriptionPage, description).success.value
              .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = Some(nonUkAddress),
              description = description,
              howManyBeneficiaries = numberOfBeneficiaries,
              countryOfResidence = Some(country),
              entityStart = date,
              provisional = true
            )
          }
        }

        "non-taxable" when {

          "no country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(CountryOfResidenceYesNoPage, false).success.value
              .set(DescriptionPage, description).success.value
              .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = None,
              description = description,
              howManyBeneficiaries = numberOfBeneficiaries,
              countryOfResidence = None,
              entityStart = date,
              provisional = true
            )
          }

          "UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, true).success.value
              .set(DescriptionPage, description).success.value
              .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = None,
              description = description,
              howManyBeneficiaries = numberOfBeneficiaries,
              countryOfResidence = Some(GB),
              entityStart = date,
              provisional = true
            )
          }

          "non-UK country of residence" in {

            val userAnswers = emptyUserAnswers
              .set(NamePage, name).success.value
              .set(CountryOfResidenceYesNoPage, true).success.value
              .set(CountryOfResidenceUkYesNoPage, false).success.value
              .set(CountryOfResidencePage, country).success.value
              .set(DescriptionPage, description).success.value
              .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
              .set(StartDatePage, date).success.value

            val result = mapper(userAnswers).get

            result mustBe EmploymentRelatedBeneficiary(
              name = name,
              utr = None,
              address = None,
              description = description,
              howManyBeneficiaries = numberOfBeneficiaries,
              countryOfResidence = Some(country),
              entityStart = date,
              provisional = true
            )
          }
        }
    }
  }
}
