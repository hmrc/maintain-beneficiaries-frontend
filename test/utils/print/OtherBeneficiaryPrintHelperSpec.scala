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
import controllers.other.routes._
import models.{CheckMode, NonUkAddress, NormalMode, UkAddress}
import pages.other._
import pages.other.add.StartDatePage
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class OtherBeneficiaryPrintHelperSpec extends SpecBase {

  val description: String = "Other"
  val share: Int = 50
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "DE")

  "OtherBeneficiaryPrintHelper" must {

    val helper = injector.instanceOf[OtherBeneficiaryPrintHelper]

    val userAnswers = emptyUserAnswers
      .set(DescriptionPage, description).success.value
      .set(DiscretionYesNoPage, false).success.value
      .set(ShareOfIncomePage, share).success.value
      .set(AddressYesNoPage, true).success.value
      .set(AddressUkYesNoPage, true).success.value
      .set(UkAddressPage, ukAddress).success.value
      .set(NonUkAddressPage, nonUKAddress).success.value
      .set(StartDatePage, date).success.value

    "generate other beneficiary section" when {

      "added" in {

        val result = helper(userAnswers, provisional = true, description)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("otherBeneficiary.description.checkYourAnswersLabel")), answer = Html("Other"), changeUrl = DescriptionController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.discretionYesNo.checkYourAnswersLabel", description)), answer = Html("No"), changeUrl = DiscretionYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.shareOfIncome.checkYourAnswersLabel", description)), answer = Html("50%"), changeUrl = ShareOfIncomeController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.addressYesNo.checkYourAnswersLabel", description)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.addressUkYesNo.checkYourAnswersLabel", description)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.ukAddress.checkYourAnswersLabel", description)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.nonUkAddress.checkYourAnswersLabel", description)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.startDate.checkYourAnswersLabel", description)), answer = Html("3 February 2019"), changeUrl = controllers.other.add.routes.StartDateController.onPageLoad().url)
          )
        )

      }

      "amended" in {

        val result = helper(userAnswers, provisional = false, description)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("otherBeneficiary.description.checkYourAnswersLabel")), answer = Html("Other"), changeUrl = DescriptionController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.discretionYesNo.checkYourAnswersLabel", description)), answer = Html("No"), changeUrl = DiscretionYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.shareOfIncome.checkYourAnswersLabel", description)), answer = Html("50%"), changeUrl = ShareOfIncomeController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.addressYesNo.checkYourAnswersLabel", description)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.addressUkYesNo.checkYourAnswersLabel", description)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.ukAddress.checkYourAnswersLabel", description)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("otherBeneficiary.nonUkAddress.checkYourAnswersLabel", description)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad(CheckMode).url)
          )
        )

      }

    }
  }
}
