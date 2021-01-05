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

import com.google.inject.Inject
import controllers.other.routes._
import models.{CheckMode, NormalMode, UserAnswers}
import pages.other._
import pages.other.add.StartDatePage
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class OtherBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                            countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    val add: Seq[AnswerRow] = Seq(
      bound.stringQuestion(DescriptionPage, "otherBeneficiary.description", DescriptionController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(DiscretionYesNoPage, "otherBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(NormalMode).url),
      bound.percentageQuestion(ShareOfIncomePage, "otherBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressYesNoPage, "otherBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressUkYesNoPage, "otherBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(NormalMode).url),
      bound.addressQuestion(UkAddressPage, "otherBeneficiary.ukAddress", UkAddressController.onPageLoad(NormalMode).url),
      bound.addressQuestion(NonUkAddressPage, "otherBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "otherBeneficiary.startDate", controllers.other.add.routes.StartDateController.onPageLoad().url)
    ).flatten

    val amend: Seq[AnswerRow] = Seq(
      bound.stringQuestion(DescriptionPage, "otherBeneficiary.description", DescriptionController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(DiscretionYesNoPage, "otherBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(CheckMode).url),
      bound.percentageQuestion(ShareOfIncomePage, "otherBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(AddressYesNoPage, "otherBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(AddressUkYesNoPage, "otherBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(CheckMode).url),
      bound.addressQuestion(UkAddressPage, "otherBeneficiary.ukAddress", UkAddressController.onPageLoad(CheckMode).url),
      bound.addressQuestion(NonUkAddressPage, "otherBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(CheckMode).url)
    ).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )
  }
}
