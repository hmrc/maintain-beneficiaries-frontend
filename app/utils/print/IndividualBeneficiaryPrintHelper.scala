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
import models.{CheckMode, NormalMode, UserAnswers}
import pages.individualbeneficiary._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class IndividualBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                                 countryOptions: CountryOptions
                                            ) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages) = {

    val bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    val add: Seq[AnswerRow] = Seq(
      bound.nameQuestion(NamePage, "individualBeneficiary.name", controllers.individualbeneficiary.routes.NameController.onPageLoad(NormalMode).url),
      bound.roleInCompanyQuestion(RoleInCompanyPage, "individualBeneficiary.roleInCompany", controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage, "individualBeneficiary.dateOfBirthYesNo", controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(NormalMode).url),
      bound.dateQuestion(DateOfBirthPage, "individualBeneficiary.dateOfBirth", controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(IncomeDiscretionYesNoPage, "individualBeneficiary.incomeDiscretionYesNo", controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(NormalMode).url),
      bound.percentageQuestion(IncomePercentagePage, "individualBeneficiary.shareOfIncome", controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualBeneficiary.nationalInsuranceNumberYesNo", controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(NormalMode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individualBeneficiary.nationalInsuranceNumber", controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(AddressYesNoPage, "individualBeneficiary.addressYesNo", controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualBeneficiary.liveInTheUkYesNo", controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(NormalMode).url),
      bound.addressQuestion(UkAddressPage, "individualBeneficiary.ukAddress", controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(NormalMode).url),
      bound.addressQuestion(NonUkAddressPage, "individualBeneficiary.nonUkAddress", controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(PassportDetailsYesNoPage, "individualBeneficiary.passportDetailsYesNo", controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(NormalMode).url),
      bound.passportDetailsQuestion(PassportDetailsPage, "individualBeneficiary.passportDetails", controllers.individualbeneficiary.routes.PassportDetailsController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(IdCardDetailsYesNoPage, "individualBeneficiary.idCardDetailsYesNo", controllers.individualbeneficiary.routes.IdCardDetailsYesNoController.onPageLoad(NormalMode).url),
      bound.idCardDetailsQuestion(IdCardDetailsPage, "individualBeneficiary.idCardDetails", controllers.individualbeneficiary.routes.IdCardDetailsController.onPageLoad(NormalMode).url),
      bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "individualBeneficiary.startDate", controllers.individualbeneficiary.routes.StartDateController.onPageLoad().url)
    ).flatten

    val amend: Seq[AnswerRow] = Seq(
      bound.nameQuestion(NamePage, "individualBeneficiary.name", controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url),
      bound.roleInCompanyQuestion(RoleInCompanyPage, "individualBeneficiary.roleInCompany", controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage, "individualBeneficiary.dateOfBirthYesNo", controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(CheckMode).url),
      bound.dateQuestion(DateOfBirthPage, "individualBeneficiary.dateOfBirth", controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(IncomeDiscretionYesNoPage, "individualBeneficiary.incomeDiscretionYesNo", controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(CheckMode).url),
      bound.percentageQuestion(IncomePercentagePage, "individualBeneficiary.shareOfIncome", controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualBeneficiary.nationalInsuranceNumberYesNo", controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individualBeneficiary.nationalInsuranceNumber", controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(AddressYesNoPage, "individualBeneficiary.addressYesNo", controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualBeneficiary.liveInTheUkYesNo", controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(CheckMode).url),
      bound.addressQuestion(UkAddressPage, "individualBeneficiary.ukAddress", controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(CheckMode).url),
      bound.addressQuestion(NonUkAddressPage, "individualBeneficiary.nonUkAddress", controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(PassportDetailsYesNoPage, "individualBeneficiary.passportDetailsYesNo", controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(CheckMode).url),
      bound.passportDetailsQuestion(PassportDetailsPage, "individualBeneficiary.passportDetails", controllers.individualbeneficiary.routes.PassportDetailsController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(IdCardDetailsYesNoPage, "individualBeneficiary.idCardDetailsYesNo", controllers.individualbeneficiary.routes.IdCardDetailsYesNoController.onPageLoad(CheckMode).url),
      bound.idCardDetailsQuestion(IdCardDetailsPage, "individualBeneficiary.idCardDetails", controllers.individualbeneficiary.routes.IdCardDetailsController.onPageLoad(CheckMode).url),
      bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(CheckMode).url)
    ).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )
  }
}
