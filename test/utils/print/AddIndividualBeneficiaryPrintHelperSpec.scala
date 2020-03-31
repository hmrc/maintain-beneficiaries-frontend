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

import java.time.LocalDate

import base.SpecBase
import models.{IdCard, Name, NonUkAddress, Passport, UkAddress}
import pages.individualbeneficiary._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AddIndividualBeneficiaryPrintHelperSpec extends SpecBase {

  val name: Name = Name("First", Some("Middle"), "Last")
  val trusteeUkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  val trusteeNonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")

  "IndividualBeneficiaryPrintHelper" must {

    "generate individual beneficiary section for all possible data" in {

      val helper = injector.instanceOf[IndividualBeneficiaryPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthYesNoPage, true).success.value
        .set(DateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
        .set(IncomeDiscretionYesNoPage, false).success.value
        .set(IncomePercentagePage, 50).success.value
        .set(NationalInsuranceNumberYesNoPage, true).success.value
        .set(NationalInsuranceNumberPage, "AA000000A").success.value
        .set(AddressYesNoPage, true).success.value
        .set(LiveInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, trusteeUkAddress).success.value
        .set(NonUkAddressPage, trusteeNonUkAddress).success.value
        .set(PassportDetailsYesNoPage, true).success.value
        .set(PassportDetailsPage, Passport("GB", "1", LocalDate.of(2030, 10, 10))).success.value
        .set(IdCardDetailsYesNoPage, true).success.value
        .set(IdCardDetailsPage, IdCard("GB", "1", LocalDate.of(2030, 10, 10))).success.value
        .set(VPE1FormYesNoPage, true).success.value
        .set(StartDatePage, LocalDate.of(2020, 1, 1)).success.value

      val result = helper(userAnswers, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("individualBeneficiary.name.checkYourAnswersLabel")), answer = Html("First Middle Last"), changeUrl = controllers.individualbeneficiary.add.routes.NameController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.add.routes.DateOfBirthYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.dateOfBirth.checkYourAnswersLabel", name.displayName)), answer = Html("10 October 2010"), changeUrl = controllers.individualbeneficiary.add.routes.DateOfBirthController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.incomeDiscretionYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("No"), changeUrl = controllers.individualbeneficiary.add.routes.IncomeDiscretionYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.incomePercentage.checkYourAnswersLabel", name.displayName)), answer = Html("50"), changeUrl = controllers.individualbeneficiary.add.routes.IncomePercentageController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.add.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName)), answer = Html("AA 00 00 00 A"), changeUrl = controllers.individualbeneficiary.add.routes.NationalInsuranceNumberYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.addressYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.add.routes.AddressYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.add.routes.LiveInTheUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.ukAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = controllers.individualbeneficiary.add.routes.UkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.nonUkAddress.checkYourAnswersLabel", name.displayName)), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = controllers.individualbeneficiary.add.routes.NonUkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.passportDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.add.routes.PassportDetailsYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.passportDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = controllers.individualbeneficiary.add.routes.PassportDetailsController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.idCardDetailsYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.add.routes.IdCardDetailsYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.idCardDetails.checkYourAnswersLabel", name.displayName)), answer = Html("United Kingdom<br />1<br />10 October 2030"), changeUrl = controllers.individualbeneficiary.add.routes.IdCardDetailsController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.vpe1FormYesNo.checkYourAnswersLabel", name.displayName)), answer = Html("Yes"), changeUrl = controllers.individualbeneficiary.add.routes.VPE1FormYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("individualBeneficiary.startDate.checkYourAnswersLabel", name.displayName)), answer = Html("1 January 2020"), changeUrl = controllers.individualbeneficiary.add.routes.StartDateController.onPageLoad().url)
        )
      )
    }
  }
}
