/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import controllers.individualbeneficiary.add.routes._
import controllers.individualbeneficiary.routes._
import models.YesNoDontKnow.{DontKnow, Yes}
import models.beneficiaries.RoleInCompany.NA
import models.{CheckMode, CombinedPassportOrIdCard, IdCard, Name, NonUkAddress, NormalMode, Passport, UkAddress}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add.StartDatePage
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class IndividualBeneficiaryPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val ukAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val nonUkAddress: NonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")
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
      .set(MentalCapacityYesNoPage, Yes).success.value
      .set(VPE1FormYesNoPage, true).success.value

    "generate individual beneficiary section" when {

      "added" in {

        val userAnswers = baseAnswers
          .set(PassportDetailsYesNoPage, true).success.value
          .set(PassportDetailsPage, Passport("GB", "1234", LocalDate.of(2030, 10, 10))).success.value
          .set(IdCardDetailsYesNoPage, true).success.value
          .set(IdCardDetailsPage, IdCard("GB", "1234567890", LocalDate.of(2030, 10, 10))).success.value
          .set(StartDatePage, LocalDate.of(2020, 1, 1)).success.value

        val result = helper(userAnswers, adding = true, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("individualBeneficiary.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(NameController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel", name.displayName), answer = Html("Not a director or employee"), changeUrl = Some(RoleInCompanyController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(DateOfBirthYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("10 October 2010"), changeUrl = Some(DateOfBirthController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(IncomeDiscretionYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.shareOfIncome.checkYourAnswersLabel", name.displayName), answer = Html("50%"), changeUrl = Some(IncomePercentageController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfNationalityUkYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfNationality.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(CountryOfNationalityController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(NationalInsuranceNumberYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), changeUrl = Some(NationalInsuranceNumberController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfResidence.checkYourAnswersLabel", name.displayName), answer = Html("France"), changeUrl = Some(CountryOfResidenceController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(LiveInTheUkYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(UkAddressController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(NonUkAddressController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.passportDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(PassportDetailsYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.passportDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />1234<br />10 October 2030"), changeUrl = Some(PassportDetailsController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.idCardDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(IdCardDetailsYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.idCardDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />1234567890<br />10 October 2030"), changeUrl = Some(IdCardDetailsController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(VPE1FormYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualBeneficiary.startDate.checkYourAnswersLabel", name.displayName), answer = Html("1 January 2020"), changeUrl = Some(StartDateController.onPageLoad().url))
          )
        )

      }

      "amended" in {

        val userAnswers = baseAnswers
          .set(PassportOrIdCardDetailsYesNoPage, true).success.value
          .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "7890", LocalDate.of(2030, 10, 10))).success.value

        val result = helper(userAnswers, adding = false, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("individualBeneficiary.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel", name.displayName), answer = Html("Not a director or employee"), changeUrl = Some(controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("10 October 2010"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.shareOfIncome.checkYourAnswersLabel", name.displayName), answer = Html("50%"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfNationalityUkYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfNationality.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(CountryOfNationalityController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.countryOfResidence.checkYourAnswersLabel", name.displayName), answer = Html("France"), changeUrl = Some(CountryOfResidenceController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.passportOrIdCardDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiary.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />Number ending 7890<br />10 October 2030"), changeUrl = None),
            AnswerRow(label = messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(CheckMode).url))
          )
        )
      }
    }

    "generate a change link for any passport question if it is not known to ETMP" in {
      val userAnswers = baseAnswers
        .set(PassportDetailsYesNoPage, true).success.value
        .set(PassportDetailsPage, Passport("GB", "1234567890", LocalDate.of(2030, 10, 10))).success.value

      val result = helper(userAnswers, adding = false, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("individualBeneficiary.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel", name.displayName), answer = Html("Not a director or employee"), changeUrl = Some(controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("10 October 2010"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.shareOfIncome.checkYourAnswersLabel", name.displayName), answer = Html("50%"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfNationalityUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationality.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(CountryOfNationalityController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidence.checkYourAnswersLabel", name.displayName), answer = Html("France"), changeUrl = Some(CountryOfResidenceController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.passportDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.passportDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />1234567890<br />10 October 2030"), changeUrl = Some(controllers.individualbeneficiary.routes.PassportDetailsController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(CheckMode).url))
        )
      )
    }

    "generate a change link for any id card question if it is not known to ETMP" in {
      val userAnswers = baseAnswers
        .set(IdCardDetailsYesNoPage, true).success.value
        .set(IdCardDetailsPage, IdCard("GB", "AB1234TFUX873B", LocalDate.of(2030, 10, 10))).success.value

      val result = helper(userAnswers, adding = false, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("individualBeneficiary.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel", name.displayName), answer = Html("Not a director or employee"), changeUrl = Some(controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("10 October 2010"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.shareOfIncome.checkYourAnswersLabel", name.displayName), answer = Html("50%"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfNationalityUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationality.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(CountryOfNationalityController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidence.checkYourAnswersLabel", name.displayName), answer = Html("France"), changeUrl = Some(CountryOfResidenceController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.idCardDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.IdCardDetailsYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.idCardDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />AB1234TFUX873B<br />10 October 2030"), changeUrl = Some(controllers.individualbeneficiary.routes.IdCardDetailsController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(CheckMode).url))

        )
      )
    }

    "not generate a change link for any combined passport/id question that is known to ETMP" in {
      val userAnswers = baseAnswers
        .set(PassportOrIdCardDetailsYesNoPage, true).success.value
        .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "1234567890", LocalDate.of(2030, 10, 10))).success.value

      val result = helper(userAnswers, adding = false, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("individualBeneficiary.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel", name.displayName), answer = Html("Not a director or employee"), changeUrl = Some(controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("10 October 2010"), changeUrl = Some(controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.shareOfIncome.checkYourAnswersLabel", name.displayName), answer = Html("50%"), changeUrl = Some(controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfNationalityUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfNationality.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(CountryOfNationalityController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), changeUrl = Some(controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.countryOfResidence.checkYourAnswersLabel", name.displayName), answer = Html("France"), changeUrl = Some(CountryOfResidenceController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.passportOrIdCardDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = None),
          AnswerRow(label = messages("individualBeneficiary.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />Number ending 7890<br />10 October 2030"), changeUrl = None),
          AnswerRow(label = messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(CheckMode).url))

        )
      )
    }

    "adding with unknown mental capacity data must show `I don’t know or not provided`" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(MentalCapacityYesNoPage, DontKnow).success.value

      val result = helper(userAnswers, adding = true, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("individualBeneficiary.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(controllers.individualbeneficiary.routes.NameController.onPageLoad(NormalMode).url)),
          AnswerRow(label = messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("I don’t know or not provided"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(NormalMode).url))
        )
      )
    }

    "amending with unknown mental capacity data must show `I don’t know or not provided`" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(MentalCapacityYesNoPage, DontKnow).success.value

      val result = helper(userAnswers, adding = false, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("individualBeneficiary.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualBeneficiary.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("I don’t know or not provided"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(CheckMode).url))
        )
      )
    }
  }
}
