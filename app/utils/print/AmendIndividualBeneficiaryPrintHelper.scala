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
import pages.individualbeneficiary._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class AmendIndividualBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                      countryOptions: CountryOptions) {

  def apply(userAnswers: UserAnswers, beneficiaryName: String)(implicit messages: Messages) = {

    val bound = answerRowConverter.bind(userAnswers, beneficiaryName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage, "individualBeneficiary.name", controllers.individualbeneficiary.amend.routes.NameController.onPageLoad().url),
        bound.yesNoQuestion(DateOfBirthYesNoPage, "individualBeneficiary.dateOfBirthYesNo", controllers.individualbeneficiary.amend.routes.DateOfBirthYesNoController.onPageLoad().url),
        bound.dateQuestion(DateOfBirthPage, "individualBeneficiary.dateOfBirth", controllers.individualbeneficiary.amend.routes.DateOfBirthController.onPageLoad().url),
        bound.yesNoQuestion(IncomeDiscretionYesNoPage, "individualBeneficiary.incomeDiscretionYesNo", controllers.individualbeneficiary.amend.routes.IncomeDiscretionYesNoController.onPageLoad().url),
        bound.intQuestion(IncomePercentagePage, "individualBeneficiary.incomePercentage", controllers.individualbeneficiary.amend.routes.IncomePercentageController.onPageLoad().url),
        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualBeneficiary.nationalInsuranceNumberYesNo", controllers.individualbeneficiary.amend.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.ninoQuestion(NationalInsuranceNumberPage, "individualBeneficiary.nationalInsuranceNumber", controllers.individualbeneficiary.amend.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressYesNoPage, "individualBeneficiary.addressYesNo", controllers.individualbeneficiary.amend.routes.AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualBeneficiary.liveInTheUkYesNo", controllers.individualbeneficiary.amend.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "individualBeneficiary.ukAddress", controllers.individualbeneficiary.amend.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "individualBeneficiary.nonUkAddress", controllers.individualbeneficiary.amend.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(PassportOrIdCardDetailsYesNoPage, "individualBeneficiary.passportOrIdCardDetailsYesNo", controllers.individualbeneficiary.amend.routes.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
        bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "individualBeneficiary.passportOrIdCardDetails", controllers.individualbeneficiary.amend.routes.PassportOrIdCardDetailsController.onPageLoad().url),
        bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", controllers.individualbeneficiary.amend.routes.VPE1FormYesNoController.onPageLoad().url)
      ).flatten
    )
  }
}
