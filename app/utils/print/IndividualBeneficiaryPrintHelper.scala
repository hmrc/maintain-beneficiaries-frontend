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

class IndividualBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                 countryOptions: CountryOptions
                                            ) {

  def apply(userAnswers: UserAnswers, beneficiaryName: String)(implicit messages: Messages) = {

    val bound = answerRowConverter.bind(userAnswers, beneficiaryName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage, "individualBeneficiary.name", controllers.individualbeneficiary.add.routes.NameController.onPageLoad().url),
        bound.yesNoQuestion(DateOfBirthYesNoPage, "individualBeneficiary.dateOfBirthYesNo", controllers.individualbeneficiary.add.routes.DateOfBirthYesNoController.onPageLoad().url),
        bound.dateQuestion(DateOfBirthPage, "individualBeneficiary.dateOfBirth", controllers.individualbeneficiary.add.routes.DateOfBirthController.onPageLoad().url),
        bound.yesNoQuestion(IncomeDiscretionYesNoPage, "individualBeneficiary.incomeDiscretionYesNo", controllers.individualbeneficiary.add.routes.IncomeDiscretionYesNoController.onPageLoad().url),
        bound.intQuestion(IncomePercentagePage, "individualBeneficiary.shareOfIncome", controllers.individualbeneficiary.add.routes.IncomePercentageController.onPageLoad().url),
        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualBeneficiary.nationalInsuranceNumberYesNo", controllers.individualbeneficiary.add.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.ninoQuestion(NationalInsuranceNumberPage, "individualBeneficiary.nationalInsuranceNumber", controllers.individualbeneficiary.add.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
        bound.yesNoQuestion(AddressYesNoPage, "individualBeneficiary.addressYesNo", controllers.individualbeneficiary.add.routes.AddressYesNoController.onPageLoad().url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualBeneficiary.liveInTheUkYesNo", controllers.individualbeneficiary.add.routes.LiveInTheUkYesNoController.onPageLoad().url),
        bound.addressQuestion(UkAddressPage, "individualBeneficiary.ukAddress", controllers.individualbeneficiary.add.routes.UkAddressController.onPageLoad().url),
        bound.addressQuestion(NonUkAddressPage, "individualBeneficiary.nonUkAddress", controllers.individualbeneficiary.add.routes.NonUkAddressController.onPageLoad().url),
        bound.yesNoQuestion(PassportDetailsYesNoPage, "individualBeneficiary.passportDetailsYesNo", controllers.individualbeneficiary.add.routes.PassportDetailsYesNoController.onPageLoad().url),
        bound.passportDetailsQuestion(PassportDetailsPage, "individualBeneficiary.passportDetails", controllers.individualbeneficiary.add.routes.PassportDetailsController.onPageLoad().url),
        bound.yesNoQuestion(IdCardDetailsYesNoPage, "individualBeneficiary.idCardDetailsYesNo", controllers.individualbeneficiary.add.routes.IdCardDetailsYesNoController.onPageLoad().url),
        bound.idCardDetailsQuestion(IdCardDetailsPage, "individualBeneficiary.idCardDetails", controllers.individualbeneficiary.add.routes.IdCardDetailsController.onPageLoad().url),
        bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", controllers.individualbeneficiary.add.routes.VPE1FormYesNoController.onPageLoad().url),
        bound.dateQuestion(StartDatePage, "individualBeneficiary.startDate", controllers.individualbeneficiary.add.routes.StartDateController.onPageLoad().url)
      ).flatten
    )
  }
}
