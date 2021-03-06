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

import java.time.LocalDate

import base.SpecBase
import models.beneficiaries.RoleInCompany.NA
import models.{CheckMode, CombinedPassportOrIdCard, IdCard, Name, NonUkAddress, NormalMode, Passport, UkAddress}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add.{IdCardDetailsPage, IdCardDetailsYesNoPage, PassportDetailsPage, PassportDetailsYesNoPage, StartDatePage}
import pages.individualbeneficiary.amend.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}
import controllers.individualbeneficiary.routes._
import controllers.individualbeneficiary.add.routes._

class IndividualBeneficiaryPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val ukAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val nonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
  val nationality: String = "DE"
  val residence: String = "FR"

  "IndividualBeneficiaryPrintHelper" must {

    val helper = injector.instanceOf[IndividualBeneficiaryPrintHelper]

    val baseAnswers = emptyUserAnswers
      .set(NamePage, name).success.value
      .set(RoleInCompanyPage, NA).success.value
      .set(DateOfBirthYesNoPage, true).success.value
      .set(DateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
      .set(IncomeDiscretionYesNoPage, false).success.value
      .set(IncomePercentagePage, 50).success.value
      .set(CountryOfNationalityYesNoPage, true).success.value
      .set(CountryOfNationalityUkYesNoPage, false).success.value
      .set(CountryOfNationalityPage, nationality).success.value
      .set(NationalInsuranceNumberYesNoPage, true).success.value
      .set(NationalInsuranceNumberPage, "AA000000A").success.value
      .set(CountryOfResidenceYesNoPage, true).success.value
      .set(CountryOfResidenceUkYesNoPage, false).success.value
      .set(CountryOfResidencePage, residence).success.value
      .set(AddressYesNoPage, true).success.value
      .set(LiveInTheUkYesNoPage, true).success.value
      .set(UkAddressPage, ukAddress).success.value
      .set(NonUkAddressPage, nonUkAddress).success.value
      .set(MentalCapacityYesNoPage, true).success.value
      .set(VPE1FormYesNoPage, true).success.value

    "generate individual beneficiary section" when {

      "added" in {

        val userAnswers = baseAnswers
          .set(PassportDetailsYesNoPage, true).success.value
          .set(PassportDetailsPage, Passport("GB", "1", LocalDate.of(2030, 10, 10))).success.value
          .set(IdCardDetailsYesNoPage, true).success.value
          .set(IdCardDetailsPage, IdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value
          .set(StartDatePage, LocalDate.of(2020, 1, 1)).success.value

        val result = helper(userAnswers, provisional = true, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualBeneficiary.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = NameController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel", name.displayName)), answer = Html("Not a director or employee"), changeUrl = RoleInCompanyController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = DateOfBirthYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = DateOfBirthController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = IncomeDiscretionYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.shareOfIncome.checkYourAnswersLabel", name.displayName)), answer = Html("50%"), changeUrl = IncomePercentageController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = CountryOfNationalityYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = CountryOfNationalityUkYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfNationality.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = CountryOfNationalityController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = NationalInsuranceNumberYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = NationalInsuranceNumberController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = CountryOfResidenceYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = CountryOfResidenceUkYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfResidence.checkYourAnswersLabel", name.displayName)), answer = Html("France"), changeUrl = CountryOfResidenceController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = LiveInTheUkYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = UkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.passportDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = PassportDetailsYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("individualBeneficiary.passportDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = PassportDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("individualBeneficiary.idCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = IdCardDetailsYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("individualBeneficiary.idCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = IdCardDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = MentalCapacityYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = VPE1FormYesNoController.onPageLoad(NormalMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.startDate.checkYourAnswersLabel", name.displayName)), answer = Html("1 January 2020"), changeUrl = StartDateController.onPageLoad().url)
          )
        )

      }

      "amended" in {

        val userAnswers = baseAnswers
          .set(PassportOrIdCardDetailsYesNoPage, true).success.value
          .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value

        val result = helper(userAnswers, provisional = false, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = Html(messages("individualBeneficiary.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel", name.displayName)), answer = Html("Not a director or employee"), changeUrl = controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.shareOfIncome.checkYourAnswersLabel", name.displayName)), answer = Html("50%"), changeUrl = controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = CountryOfNationalityYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = CountryOfNationalityUkYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfNationality.checkYourAnswersLabel", name.displayName)), answer = Html("Germany"), changeUrl = CountryOfNationalityController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = CountryOfResidenceYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.countryOfResidence.checkYourAnswersLabel", name.displayName)), answer = Html("France"), changeUrl = CountryOfResidenceController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.passportOrIdCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.amend.routes.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
            AnswerRow(label = Html(messages("individualBeneficiary.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = controllers.individualbeneficiary.amend.routes.PassportOrIdCardDetailsController.onPageLoad().url),
            AnswerRow(label = Html(messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = MentalCapacityYesNoController.onPageLoad(CheckMode).url),
            AnswerRow(label = Html(messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(CheckMode).url)
          )
        )

      }
    }
  }
}
