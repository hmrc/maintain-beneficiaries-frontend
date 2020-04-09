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
import controllers.other.add.routes._
import models.UserAnswers
import pages.other._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class OtherBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                            countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, name: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(DescriptionPage, "otherBeneficiary.description", DescriptionController.onPageLoad().url),
        bound.yesNoQuestion(DiscretionYesNoPage, "otherBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad().url),
        bound.percentageQuestion(ShareOfIncomePage, "otherBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad().url),
        bound.yesNoQuestion(AddressYesNoPage, "otherBeneficiary.addressYesNo", AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressUkYesNoPage, "otherBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "otherBeneficiary.ukAddress", UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "otherBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad().url),
        bound.dateQuestion(StartDatePage, "otherBeneficiary.startDate", StartDateController.onPageLoad().url)
      ).flatten
    )
  }
}
