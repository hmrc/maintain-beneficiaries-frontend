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
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add._
import pages.individualbeneficiary.amend.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class IndividualBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, name: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, name)

    def answerRows(mode: Mode): Seq[Option[AnswerRow]] = Seq(
      bound.nameQuestion(NamePage, "individualBeneficiary.name", NameController.onPageLoad(mode).url),
      bound.roleInCompanyQuestion(RoleInCompanyPage, "individualBeneficiary.roleInCompany", RoleInCompanyController.onPageLoad(mode).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage, "individualBeneficiary.dateOfBirthYesNo", DateOfBirthYesNoController.onPageLoad(mode).url),
      bound.dateQuestion(DateOfBirthPage, "individualBeneficiary.dateOfBirth", DateOfBirthController.onPageLoad(mode).url),
      bound.yesNoQuestion(IncomeDiscretionYesNoPage, "individualBeneficiary.incomeDiscretionYesNo", IncomeDiscretionYesNoController.onPageLoad(mode).url),
      bound.percentageQuestion(IncomePercentagePage, "individualBeneficiary.shareOfIncome", IncomePercentageController.onPageLoad(mode).url),

      bound.yesNoQuestion(CountryOfNationalityYesNoPage, "individualBeneficiary.countryOfNationalityYesNo", CountryOfNationalityYesNoController.onPageLoad(mode).url),
      bound.yesNoQuestion(CountryOfNationalityUkYesNoPage, "individualBeneficiary.countryOfNationalityUkYesNo", CountryOfNationalityUkYesNoController.onPageLoad(mode).url),
      bound.countryQuestion(CountryOfNationalityUkYesNoPage, CountryOfResidencePage, "individualBeneficiary.countryOfNationality", CountryOfNationalityController.onPageLoad(mode).url),

      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualBeneficiary.nationalInsuranceNumberYesNo", NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individualBeneficiary.nationalInsuranceNumber", NationalInsuranceNumberController.onPageLoad(mode).url),

      bound.yesNoQuestion(CountryOfResidenceYesNoPage, "individualBeneficiary.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(mode).url),
      bound.yesNoQuestion(CountryOfResidenceUkYesNoPage, "individualBeneficiary.countryOfResidenceUkYesNo", CountryOfResidenceUkYesNoController.onPageLoad(mode).url),
      bound.countryQuestion(CountryOfResidenceUkYesNoPage, CountryOfResidencePage, "individualBeneficiary.countryOfResidence", CountryOfResidenceController.onPageLoad(mode).url),

      bound.yesNoQuestion(AddressYesNoPage, "individualBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(mode).url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualBeneficiary.liveInTheUkYesNo", LiveInTheUkYesNoController.onPageLoad(mode).url),
      bound.addressQuestion(UkAddressPage, "individualBeneficiary.ukAddress", UkAddressController.onPageLoad(mode).url),
      bound.addressQuestion(NonUkAddressPage, "individualBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(mode).url)
    )

    lazy val additionalAdd: Seq[Option[AnswerRow]] = Seq(
      bound.yesNoQuestion(PassportDetailsYesNoPage, "individualBeneficiary.passportDetailsYesNo", PassportDetailsYesNoController.onPageLoad().url),
      bound.passportDetailsQuestion(PassportDetailsPage, "individualBeneficiary.passportDetails", PassportDetailsController.onPageLoad().url),
      bound.yesNoQuestion(IdCardDetailsYesNoPage, "individualBeneficiary.idCardDetailsYesNo", IdCardDetailsYesNoController.onPageLoad().url),
      bound.idCardDetailsQuestion(IdCardDetailsPage, "individualBeneficiary.idCardDetails", IdCardDetailsController.onPageLoad().url),
      bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", VPE1FormYesNoController.onPageLoad(NormalMode).url),
      bound.dateQuestion(StartDatePage, "individualBeneficiary.startDate", StartDateController.onPageLoad().url)
    )

    lazy val add: Seq[AnswerRow] = (answerRows(NormalMode) ++ additionalAdd).flatten

    lazy val additionalAmend: Seq[Option[AnswerRow]] = Seq(
        bound.yesNoQuestion(PassportOrIdCardDetailsYesNoPage, "individualBeneficiary.passportOrIdCardDetailsYesNo", PassportOrIdCardDetailsYesNoController.onPageLoad().url),
        bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "individualBeneficiary.passportOrIdCardDetails", PassportOrIdCardDetailsController.onPageLoad().url),
        bound.yesNoQuestion(VPE1FormYesNoPage, "individualBeneficiary.vpe1FormYesNo", VPE1FormYesNoController.onPageLoad(CheckMode).url)
      )

    lazy val amend: Seq[AnswerRow] = (answerRows(CheckMode) ++ additionalAmend).flatten

    AnswerSection(
      headingKey = None,
      rows = if (provisional) add else amend
    )

  }
}
