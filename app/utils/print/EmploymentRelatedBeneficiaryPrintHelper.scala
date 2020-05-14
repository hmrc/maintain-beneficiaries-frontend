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
import controllers.companyoremploymentrelated.employment.routes._
import models.UserAnswers
import pages.companyoremploymentrelated.employment._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class EmploymentRelatedBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                        countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, name: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(NamePage, "employmentBeneficiary.name", NameController.onPageLoad().url),
        bound.yesNoQuestion(AddressYesNoPage, "employmentBeneficiary.addressYesNo", AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressUkYesNoPage, "employmentBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "employmentBeneficiary.ukAddress", UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "employmentBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad().url),
        bound.descriptionQuestion(DescriptionPage, "employmentBeneficiary.description", DescriptionController.onPageLoad().url),
        bound.numberOfBeneficiariesQuestion(NumberOfBeneficiariesPage, "employmentBeneficiary.numberOfBeneficiaries", NumberOfBeneficiariesController.onPageLoad().url),
        bound.dateQuestion(StartDatePage, "employmentBeneficiary.startDate", StartDateController.onPageLoad().url)
      ).flatten
    )
  }
}
