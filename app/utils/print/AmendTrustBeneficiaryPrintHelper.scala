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
import controllers.charityortrust.trust.amend.routes._
import models.UserAnswers
import pages.charityortrust.trust._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class AmendTrustBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                 countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, name: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(NamePage, "trustBeneficiary.name", NameController.onPageLoad().url),
        bound.yesNoQuestion(DiscretionYesNoPage, "trustBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad().url),
        bound.percentageQuestion(ShareOfIncomePage, "trustBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad().url),
        bound.stringQuestion(UtrPage, "trustBeneficiary.checkDetails.utr",""),
        bound.yesNoQuestion(AddressYesNoPage, "trustBeneficiary.addressYesNo", AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressUkYesNoPage, "trustBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "trustBeneficiary.ukAddress", UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "trustBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad().url)
      ).flatten
    )
  }
}
