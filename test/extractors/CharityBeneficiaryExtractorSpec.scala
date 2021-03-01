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
import models.{NonUkAddress, UkAddress}
import models.beneficiaries.CharityBeneficiary
import pages.charityortrust.charity._

import java.time.LocalDate

class CharityBeneficiaryExtractorSpec extends SpecBase {

  private val index: Int = 0

  private val name: String = "Charity"
  private val utr: String = "utr"
  private val income: Int = 50
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "AB1 1AB")
  private val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "FR")
  private val date: LocalDate = LocalDate.parse("1996-02-03")

  private val extractor: CharityBeneficiaryExtractor = injector.instanceOf[CharityBeneficiaryExtractor]

  "CharityBeneficiaryExtractor" must {

    "Populate user answers" when {

      "4mld" when {

        "has minimal data" in {

          val beneficiary = CharityBeneficiary(
            name = name,
            utr = None,
            address = None,
            income = None,
            incomeDiscretionYesNo = Some(true),
            countryOfResidence = None,
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(emptyUserAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(DiscretionYesNoPage).get mustBe true
          result.get(ShareOfIncomePage) mustBe None
          result.get(AddressYesNoPage).get mustBe false
          result.get(AddressUkYesNoPage) mustBe None
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }

        "has UK address" in {

          val beneficiary = CharityBeneficiary(
            name = name,
            utr = Some(utr),
            address = Some(ukAddress),
            income = Some(income.toString),
            incomeDiscretionYesNo = Some(false),
            countryOfResidence = None,
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(emptyUserAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage).get mustBe utr
          result.get(DiscretionYesNoPage).get mustBe false
          result.get(ShareOfIncomePage).get mustBe income
          result.get(AddressYesNoPage).get mustBe true
          result.get(AddressUkYesNoPage).get mustBe true
          result.get(UkAddressPage).get mustBe ukAddress
          result.get(NonUkAddressPage) mustBe None
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }

        "has non-UK address" in {

          val beneficiary = CharityBeneficiary(
            name = name,
            utr = None,
            address = Some(nonUkAddress),
            income = Some(income.toString),
            incomeDiscretionYesNo = Some(false),
            countryOfResidence = None,
            entityStart = date,
            provisional = false
          )

          val result = extractor.apply(emptyUserAnswers, beneficiary, index).get

          result.get(NamePage).get mustBe name
          result.get(UtrPage) mustBe None
          result.get(DiscretionYesNoPage).get mustBe false
          result.get(ShareOfIncomePage).get mustBe income
          result.get(AddressYesNoPage).get mustBe true
          result.get(AddressUkYesNoPage).get mustBe false
          result.get(UkAddressPage) mustBe None
          result.get(NonUkAddressPage).get mustBe nonUkAddress
          result.get(StartDatePage).get mustBe date
          result.get(IndexPage).get mustBe index
        }
      }
    }
  }
}
