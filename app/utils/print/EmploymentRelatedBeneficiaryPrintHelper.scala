/*
 * Copyright 2025 HM Revenue & Customs
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
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.companyoremploymentrelated.employment._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class EmploymentRelatedBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    def answerRows: Seq[AnswerRow] = {
      val mode: Mode = if (provisional) NormalMode else CheckMode
      Seq(
        bound.stringQuestion(NamePage, "employmentBeneficiary.name", NameController.onPageLoad(mode).url),
        if (mode == CheckMode) bound.stringQuestion(UtrPage, "employmentBeneficiary.checkDetails.utr", "") else None,
        bound.yesNoQuestion(CountryOfResidenceYesNoPage, "employmentBeneficiary.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfResidenceUkYesNoPage, "employmentBeneficiary.countryOfResidenceUkYesNo", CountryOfResidenceUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfResidenceUkYesNoPage, CountryOfResidencePage, "employmentBeneficiary.countryOfResidence", CountryOfResidenceController.onPageLoad(mode).url),
        bound.yesNoQuestion(AddressYesNoPage, "employmentBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(AddressUkYesNoPage, "employmentBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(mode).url),
        bound.addressQuestion(UkAddressPage, "employmentBeneficiary.ukAddress", UkAddressController.onPageLoad(mode).url),
        bound.addressQuestion(NonUkAddressPage, "employmentBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(mode).url),
        bound.descriptionQuestion(DescriptionPage, "employmentBeneficiary.description", DescriptionController.onPageLoad(mode).url),
        bound.numberOfBeneficiariesQuestion(NumberOfBeneficiariesPage, "employmentBeneficiary.numberOfBeneficiaries", NumberOfBeneficiariesController.onPageLoad(mode).url),
        if (mode == NormalMode) bound.dateQuestion(StartDatePage, "employmentBeneficiary.startDate", StartDateController.onPageLoad().url) else None
      ).flatten
    }

    AnswerSection(headingKey = None, rows = answerRows)
  }
}
