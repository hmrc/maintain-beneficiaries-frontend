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
import controllers.companyoremploymentrelated.employment.routes._
import models.{CheckMode, NormalMode, UserAnswers}
import pages.companyoremploymentrelated.employment._
import play.api.i18n.Messages
import viewmodels.AnswerSection

class EmploymentRelatedBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    val add = Seq(
      bound.stringQuestion(NamePage, "employmentBeneficiary.name", NameController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressYesNoPage, "employmentBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressUkYesNoPage, "employmentBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(NormalMode).url),
      bound.addressQuestion(UkAddressPage, "employmentBeneficiary.ukAddress", UkAddressController.onPageLoad(NormalMode).url),
      bound.addressQuestion(NonUkAddressPage, "employmentBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(NormalMode).url),
      bound.descriptionQuestion(DescriptionPage, "employmentBeneficiary.description", DescriptionController.onPageLoad(NormalMode).url),
      bound.numberOfBeneficiariesQuestion(NumberOfBeneficiariesPage, "employmentBeneficiary.numberOfBeneficiaries", NumberOfBeneficiariesController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "employmentBeneficiary.startDate", StartDateController.onPageLoad().url)
    ).flatten

    val amend = Seq(
      bound.stringQuestion(NamePage, "employmentBeneficiary.name", NameController.onPageLoad(CheckMode).url),
      bound.stringQuestion(UtrPage, "employmentBeneficiary.checkDetails.utr",""),
      bound.yesNoQuestion(AddressYesNoPage, "employmentBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(AddressUkYesNoPage, "employmentBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(CheckMode).url),
      bound.addressQuestion(UkAddressPage, "employmentBeneficiary.ukAddress", UkAddressController.onPageLoad(CheckMode).url),
      bound.addressQuestion(NonUkAddressPage, "employmentBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(CheckMode).url),
      bound.descriptionQuestion(DescriptionPage, "employmentBeneficiary.description", DescriptionController.onPageLoad(CheckMode).url),
      bound.numberOfBeneficiariesQuestion(NumberOfBeneficiariesPage, "employmentBeneficiary.numberOfBeneficiaries", NumberOfBeneficiariesController.onPageLoad(CheckMode).url)
    ).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )
  }
}
