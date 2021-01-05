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
import controllers.charityortrust.trust.routes._
import models.{CheckMode, NonUkAddress, NormalMode, UkAddress}
import pages.charityortrust.trust._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class TrustBeneficiaryPrintHelperSpec extends SpecBase {

  val name: String = "Trust"
  val share: Int = 50
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "DE")

  "trustBeneficiaryPrintHelper" must {

    val helper = injector.instanceOf[TrustBeneficiaryPrintHelper]

    val userAnswers = emptyUserAnswers
      .set(NamePage, name).success.value
      .set(DiscretionYesNoPage, false).success.value
      .set(ShareOfIncomePage, share).success.value
      .set(AddressYesNoPage, true).success.value
      .set(AddressUkYesNoPage, true).success.value
      .set(UkAddressPage, ukAddress).success.value
      .set(NonUkAddressPage, nonUKAddress).success.value
      .set(StartDatePage, date).success.value

    "generate class of beneficiary section" when {
      "add" in {

        val result = helper(userAnswers, true, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustBeneficiary.name.checkYourAnswersLabel")), answer = Html("Trust"), changeUrl = NameController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.discretionYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = DiscretionYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.shareOfIncome.checkYourAnswersLabel", name)), answer = Html("50%"), changeUrl = ShareOfIncomeController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.addressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.ukAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.startDate.checkYourAnswersLabel", name)), answer = Html("3 February 2019"), changeUrl = StartDateController.onPageLoad().url)
          )
        )
      }

      "amend" in {

        val result = helper(userAnswers, false, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("trustBeneficiary.name.checkYourAnswersLabel")), answer = Html("Trust"), changeUrl = NameController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.discretionYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = DiscretionYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.shareOfIncome.checkYourAnswersLabel", name)), answer = Html("50%"), changeUrl = ShareOfIncomeController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.addressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.ukAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("trustBeneficiary.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad(CheckMode).url)
          )
        )
      }
    }
  }
}
