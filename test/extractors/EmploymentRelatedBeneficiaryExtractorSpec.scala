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
import models.HowManyBeneficiaries.Over1
import models.beneficiaries.EmploymentRelatedBeneficiary
import models.{Description, NonUkAddress, UkAddress}
import pages.companyoremploymentrelated.employment._

import java.time.LocalDate

class EmploymentRelatedBeneficiaryExtractorSpec extends SpecBase {

  val answers = emptyUserAnswers

  val index = 0

  val name = "Employment Related Name"
  val description = Description("Employment Related Description", None,  None, None, None)
  val howManyBeneficiaries = Over1
  val date = LocalDate.parse("1996-02-03")
  val address = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val nonUkAddress = NonUkAddress("Line 1", "Line 2", None, "DE")

  val extractor = new EmploymentRelatedBeneficiaryExtractor()

  "should populate user answers when an employment related beneficiary has no address" in {
    val beneficiary = EmploymentRelatedBeneficiary(
      name = name,
      utr = None,
      address = None,
      description = description,
      howManyBeneficiaries = howManyBeneficiaries.toString,
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, beneficiary, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(UtrPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe false
    result.get(AddressUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(DescriptionPage).get mustBe description
    result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
  }

  "should populate user answers when employment related beneficiary has a UK address" in {
    val beneficiary = EmploymentRelatedBeneficiary(
      name = name,
      utr = None,
      address = Some(address),
      description = description,
      howManyBeneficiaries = howManyBeneficiaries.toString,
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, beneficiary, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(UtrPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe true
    result.get(AddressUkYesNoPage).get mustBe true
    result.get(UkAddressPage).get mustBe address
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(DescriptionPage).get mustBe description
    result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
  }

  "should populate user answers when employment related beneficiary has a non UK address" in {
    val beneficiary = EmploymentRelatedBeneficiary(
      name = name,
      utr = None,
      address = Some(nonUkAddress),
      description = description,
      howManyBeneficiaries = howManyBeneficiaries.toString,
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, beneficiary, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(UtrPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe true
    result.get(AddressUkYesNoPage).get mustBe false
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage).get mustBe nonUkAddress
    result.get(DescriptionPage).get mustBe description
    result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
  }

  "should populate user answers when employment related beneficiary has a UTR" in {
    val utr = "UTRUTRUTR"

    val beneficiary = EmploymentRelatedBeneficiary(
      name = name,
      utr = Some(utr),
      address = None,
      description = description,
      howManyBeneficiaries = howManyBeneficiaries.toString,
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, beneficiary, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(UtrPage).get mustBe utr
    result.get(AddressYesNoPage).get mustBe false
    result.get(AddressYesNoPage).get mustBe false
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(DescriptionPage).get mustBe description
    result.get(NumberOfBeneficiariesPage).get mustBe howManyBeneficiaries
  }
}
