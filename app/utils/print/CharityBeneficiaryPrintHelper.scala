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

package utils.print

import com.google.inject.Inject
import controllers.charityortrust.charity.routes._
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.charityortrust.charity._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class CharityBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    def answerRows: Seq[AnswerRow] = {
      val mode: Mode = if (provisional) NormalMode else CheckMode
      Seq(
        bound.stringQuestion(NamePage, "charityBeneficiary.name", NameController.onPageLoad(mode).url),
        bound.yesNoQuestion(DiscretionYesNoPage, "charityBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(mode).url),
        bound.percentageQuestion(ShareOfIncomePage, "charityBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(mode).url),
        if (mode == CheckMode) bound.stringQuestion(UtrPage, "charityBeneficiary.checkDetails.utr", "") else None,
        bound.yesNoQuestion(CountryOfResidenceYesNoPage, "charityBeneficiary.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfResidenceUkYesNoPage, "charityBeneficiary.countryOfResidenceUkYesNo", CountryOfResidenceUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfResidenceUkYesNoPage, CountryOfResidencePage, "charityBeneficiary.countryOfResidence", CountryOfResidenceController.onPageLoad(mode).url),
        bound.yesNoQuestion(AddressYesNoPage, "charityBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(AddressUkYesNoPage, "charityBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(mode).url),
        bound.addressQuestion(UkAddressPage, "charityBeneficiary.ukAddress", UkAddressController.onPageLoad(mode).url),
        bound.addressQuestion(NonUkAddressPage, "charityBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(mode).url),
        if (mode == NormalMode) bound.dateQuestion(StartDatePage, "charityBeneficiary.startDate", StartDateController.onPageLoad().url) else None
      ).flatten
    }

    AnswerSection(headingKey = None, rows = answerRows)
  }

}
