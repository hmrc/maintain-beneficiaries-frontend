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

import com.google.inject.Inject
import models.{NormalMode, UserAnswers}
import pages.charityortrust.charity._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import controllers.charityortrust.charity.routes._

class CharityBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                              countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, name: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(NamePage, "charityBeneficiary.name", NameController.onPageLoad(NormalMode).url),
        bound.yesNoQuestion(DiscretionYesNoPage, "charityBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(NormalMode).url),
        bound.percentageQuestion(ShareOfIncomePage, "charityBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(NormalMode).url),
        bound.yesNoQuestion(AddressYesNoPage, "charityBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(NormalMode).url),
        bound.yesNoQuestion(AddressUkYesNoPage, "charityBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(NormalMode).url),
        bound.addressQuestion(UkAddressPage, "charityBeneficiary.ukAddress", UkAddressController.onPageLoad(NormalMode).url),
        bound.addressQuestion(NonUkAddressPage, "charityBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(NormalMode).url),
        bound.dateQuestion(StartDatePage, "charityBeneficiary.startDate", StartDateController.onPageLoad().url)
      ).flatten
    )
  }
}
