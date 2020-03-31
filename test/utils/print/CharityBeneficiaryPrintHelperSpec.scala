/*
 * Copyright 2020 HM Revenue & Customs
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
import models.{NonUkAddress, UkAddress}
import pages.charityortrust.charity._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class CharityBeneficiaryPrintHelperSpec extends SpecBase {

  val name: String = "Charity"
  val share: String = "50"
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "DE")

  "CharityBeneficiaryPrintHelper" must {

    "generate class of beneficiary section" in {

      val helper = injector.instanceOf[CharityBeneficiaryPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DiscretionYesNoPage, false).success.value
        .set(ShareOfIncomePage, share).success.value
        .set(AddressYesNoPage, true).success.value
        .set(AddressUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(NonUkAddressPage, nonUKAddress).success.value
        .set(StartDatePage, date).success.value

      val result = helper(userAnswers, name)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("charityBeneficiary.name.checkYourAnswersLabel")), answer = Html("Charity"), changeUrl = controllers.charityortrust.add.charity.routes.NameController.onPageLoad().url),
          AnswerRow(label = Html(messages("charityBeneficiary.discretionYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = controllers.charityortrust.add.charity.routes.DiscretionYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("charityBeneficiary.shareOfIncome.checkYourAnswersLabel", name)), answer = Html("50%"), changeUrl = controllers.charityortrust.add.charity.routes.ShareOfIncomeController.onPageLoad().url),
          AnswerRow(label = Html(messages("charityBeneficiary.addressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = controllers.charityortrust.add.charity.routes.AddressYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("charityBeneficiary.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = controllers.charityortrust.add.charity.routes.AddressUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("charityBeneficiary.ukAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = controllers.charityortrust.add.charity.routes.UkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("charityBeneficiary.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = controllers.charityortrust.add.charity.routes.NonUkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("charityBeneficiary.startDate.checkYourAnswersLabel", name)), answer = Html("3 February 2019"), changeUrl = controllers.charityortrust.add.charity.routes.StartDateController.onPageLoad().url)
        )
      )
    }
  }
}
