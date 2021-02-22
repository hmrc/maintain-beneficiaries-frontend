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
import controllers.individualbeneficiary.add.routes._
import controllers.individualbeneficiary.amend.routes._
import controllers.individualbeneficiary.routes._
import models.{CheckMode, NormalMode, UserAnswers}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add._
import pages.individualbeneficiary.amend.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class IndividualBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, name)

    lazy val add: Seq[AnswerRow] = Seq(
      bound.nameQuestion(NamePage, "individualBeneficiary.name", NameController.onPageLoad(NormalMode).url),
      bound.roleInCompanyQuestion(RoleInCompanyPage, "individualBeneficiary.roleInCompany", RoleInCompanyController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage, "individualBeneficiary.dateOfBirthYesNo", DateOfBirthYesNoController.onPageLoad(NormalMode).url),
      bound.dateQuestion(DateOfBirthPage, "individualBeneficiary.dateOfBirth", DateOfBirthController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(IncomeDiscretionYesNoPage, "individualBeneficiary.incomeDiscretionYesNo", IncomeDiscretionYesNoController.onPageLoad(NormalMode).url),
      bound.percentageQuestion(IncomePercentagePage, "individualBeneficiary.shareOfIncome", IncomePercentageController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualBeneficiary.nationalInsuranceNumberYesNo", NationalInsuranceNumberYesNoController.onPageLoad(NormalMode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individualBeneficiary.nationalInsuranceNumber", NationalInsuranceNumberController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressYesNoPage, "individualBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualBeneficiary.liveInTheUkYesNo", LiveInTheUkYesNoController.onPageLoad(NormalMode).url),
      bound.addressQuestion(UkAddressPage, "individualBeneficiary.ukAddress", UkAddressController.onPageLoad(NormalMode).url),
      bound.addressQuestion(NonUkAddressPage, "individualBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(PassportDetailsYesNoPage, "individualBeneficiary.passportDetailsYesNo", PassportDetailsYesNoController.onPageLoad().url),
      bound.passportDetailsQuestion(PassportDetailsPage, "individualBeneficiary.passportDetails", PassportDetailsController.onPageLoad().url),
      bound.yesNoQuestion(IdCardDetailsYesNoPage, "individualBeneficiary.idCardDetailsYesNo", IdCardDetailsYesNoController.onPageLoad().url),
      bound.idCardDetailsQuestion(IdCardDetailsPage, "individualBeneficiary.idCardDetails", IdCardDetailsController.onPageLoad().url),
      bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", VPE1FormYesNoController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "individualBeneficiary.startDate", StartDateController.onPageLoad().url)
    ).flatten

    lazy val amend: Seq[AnswerRow] = Seq(
      bound.nameQuestion(NamePage, "individualBeneficiary.name", NameController.onPageLoad(CheckMode).url),
      bound.roleInCompanyQuestion(RoleInCompanyPage, "individualBeneficiary.roleInCompany", RoleInCompanyController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage, "individualBeneficiary.dateOfBirthYesNo", DateOfBirthYesNoController.onPageLoad(CheckMode).url),
      bound.dateQuestion(DateOfBirthPage, "individualBeneficiary.dateOfBirth", DateOfBirthController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(IncomeDiscretionYesNoPage, "individualBeneficiary.incomeDiscretionYesNo", IncomeDiscretionYesNoController.onPageLoad(CheckMode).url),
      bound.percentageQuestion(IncomePercentagePage, "individualBeneficiary.shareOfIncome", IncomePercentageController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualBeneficiary.nationalInsuranceNumberYesNo", NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individualBeneficiary.nationalInsuranceNumber", NationalInsuranceNumberController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(AddressYesNoPage, "individualBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualBeneficiary.liveInTheUkYesNo", LiveInTheUkYesNoController.onPageLoad(CheckMode).url),
      bound.addressQuestion(UkAddressPage, "individualBeneficiary.ukAddress", UkAddressController.onPageLoad(CheckMode).url),
      bound.addressQuestion(NonUkAddressPage, "individualBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(PassportOrIdCardDetailsYesNoPage, "individualBeneficiary.passportOrIdCardDetailsYesNo", PassportOrIdCardDetailsYesNoController.onPageLoad().url),
      bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "individualBeneficiary.passportOrIdCardDetails", PassportOrIdCardDetailsController.onPageLoad().url),
      bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", VPE1FormYesNoController.onPageLoad(CheckMode).url)
    ).flatten

    AnswerSection(
      headingKey = None,
      rows = if (provisional) add else amend
    )
  }
}
