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
import controllers.companyoremploymentrelated.company.routes._
import models.{CheckMode, NormalMode, UserAnswers}
import pages.companyoremploymentrelated.company._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class CompanyBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                              countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    val add = Seq(
        bound.stringQuestion(NamePage, "companyBeneficiary.name", NameController.onPageLoad(NormalMode).url),
        bound.yesNoQuestion(DiscretionYesNoPage, "companyBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(NormalMode).url),
        bound.percentageQuestion(ShareOfIncomePage, "companyBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(NormalMode).url),
        bound.yesNoQuestion(AddressYesNoPage, "companyBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(NormalMode).url),
        bound.yesNoQuestion(AddressUkYesNoPage, "companyBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(NormalMode).url),
        bound.addressQuestion(UkAddressPage, "companyBeneficiary.ukAddress", UkAddressController.onPageLoad(NormalMode).url),
        bound.addressQuestion(NonUkAddressPage, "companyBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(NormalMode).url),
        bound.dateQuestion(StartDatePage, "companyBeneficiary.startDate", StartDateController.onPageLoad().url)
      ).flatten

    val amend = Seq(
        bound.stringQuestion(NamePage, "companyBeneficiary.name", NameController.onPageLoad(CheckMode).url),
        bound.yesNoQuestion(DiscretionYesNoPage, "companyBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(CheckMode).url),
        bound.percentageQuestion(ShareOfIncomePage, "companyBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(CheckMode).url),
        bound.yesNoQuestion(AddressYesNoPage, "companyBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(CheckMode).url),
        bound.yesNoQuestion(AddressUkYesNoPage, "companyBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(CheckMode).url),
        bound.addressQuestion(UkAddressPage, "companyBeneficiary.ukAddress", UkAddressController.onPageLoad(CheckMode).url),
        bound.addressQuestion(NonUkAddressPage, "companyBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(CheckMode).url)
      ).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )
  }
}
