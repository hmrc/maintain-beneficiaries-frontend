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

package utils.print

import java.time.LocalDate

import base.SpecBase
import models.{CheckMode, NonUkAddress, NormalMode, UkAddress}
import pages.charityortrust.charity._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}
import controllers.charityortrust.charity.routes._

class CharityBeneficiaryPrintHelperSpec extends SpecBase {

  val name: String = "Charity"
  val share: Int = 50
  val utr: String = "1234567890"
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val country: String = "DE"
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, country)

  "CharityBeneficiaryPrintHelper" must {

    val helper = injector.instanceOf[CharityBeneficiaryPrintHelper]

    val userAnswers = emptyUserAnswers
      .set(NamePage, name).success.value
      .set(DiscretionYesNoPage, false).success.value
      .set(ShareOfIncomePage, share).success.value
      .set(UtrPage, utr).success.value
      .set(CountryOfResidenceYesNoPage, true).success.value
      .set(CountryOfResidenceUkYesNoPage, false).success.value
      .set(CountryOfResidencePage, country).success.value
      .set(AddressYesNoPage, true).success.value
      .set(AddressUkYesNoPage, true).success.value
      .set(UkAddressPage, ukAddress).success.value
      .set(NonUkAddressPage, nonUKAddress).success.value
      .set(StartDatePage, date).success.value

    "generate class of beneficiary section" when {

      "added" in {

        val result = helper(userAnswers, provisional = true, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("charityBeneficiary.name.checkYourAnswersLabel")), answer = Html("Charity"), changeUrl = NameController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.discretionYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = DiscretionYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.shareOfIncome.checkYourAnswersLabel", name)), answer = Html("50%"), changeUrl = ShareOfIncomeController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = CountryOfResidenceYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = CountryOfResidenceUkYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.countryOfResidence.checkYourAnswersLabel", name)), answer = Html("Germany"), changeUrl = CountryOfResidenceController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.addressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.ukAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.startDate.checkYourAnswersLabel", name)), answer = Html("3 February 2019"), changeUrl = StartDateController.onPageLoad().url)
          )
        )
      }

      "amended" in {

        val result = helper(userAnswers, provisional = false, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("charityBeneficiary.name.checkYourAnswersLabel")), answer = Html("Charity"), changeUrl = NameController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.discretionYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = DiscretionYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.shareOfIncome.checkYourAnswersLabel", name)), answer = Html("50%"), changeUrl = ShareOfIncomeController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.checkDetails.utr.checkYourAnswersLabel", name)), answer = Html("1234567890"), changeUrl = ""),
            AnswerRow(label = Html(messages("charityBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = CountryOfResidenceYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.countryOfResidence.checkYourAnswersLabel", name)), answer = Html("Germany"), changeUrl = CountryOfResidenceController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.addressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.ukAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("charityBeneficiary.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad(CheckMode).url)
          )
        )
      }

    }
  }
}
