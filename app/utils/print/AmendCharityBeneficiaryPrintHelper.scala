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
import models.UserAnswers
import pages.charityortrust.charity._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class AmendCharityBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                   countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, name: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(NamePage, "charityBeneficiary.name", controllers.charityortrust.amend.charity.routes.NameController.onPageLoad().url),
        bound.yesNoQuestion(DiscretionYesNoPage, "charityBeneficiary.discretionYesNo", controllers.charityortrust.amend.charity.routes.DiscretionYesNoController.onPageLoad().url),
        bound.percentageQuestion(ShareOfIncomePage, "charityBeneficiary.shareOfIncome", controllers.charityortrust.amend.charity.routes.ShareOfIncomeController.onPageLoad().url),
        bound.yesNoQuestion(AddressYesNoPage, "charityBeneficiary.addressYesNo", controllers.charityortrust.amend.charity.routes.AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressUkYesNoPage, "charityBeneficiary.addressUkYesNo", controllers.charityortrust.amend.charity.routes.AddressUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "charityBeneficiary.ukAddress", controllers.charityortrust.amend.charity.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "charityBeneficiary.nonUkAddress", controllers.charityortrust.amend.charity.routes.NonUkAddressController.onPageLoad().url)
      ).flatten
    )
  }
}
