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
import controllers.charityortrust.trust.routes._
import models.{CheckMode, NonUkAddress, NormalMode, UkAddress}
import pages.charityortrust.trust._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class TrustBeneficiaryPrintHelperSpec extends SpecBase {

  val name: String = "Trust"
  val share: Int = 50
  val utr: String = "1234567890"
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val country: String = "DE"
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, country)

  "trustBeneficiaryPrintHelper" must {

    val helper = injector.instanceOf[TrustBeneficiaryPrintHelper]

    val userAnswers = emptyUserAnswers
      .set(NamePage, name).success.value
      .set(DiscretionYesNoPage, false).success.value
      .set(ShareOfIncomePage, share).success.value
      .set(UtrPage, utr).success.value
      .set(CountryOfResidenceYesNoPage, true).success.value
      .set(CountryOfResidenceUkYesNoPage, false).success.value
      .set(CountryOfResidencePage, country).success.value
      .set(AddressYesNoPage, true).success.value
      .set(AddressUkYesNoPage, true).success.value
      .set(UkAddressPage, ukAddress).success.value
      .set(NonUkAddressPage, nonUKAddress).success.value
      .set(StartDatePage, date).success.value

    "generate class of beneficiary section" when {
      "add" in {

        val result = helper(userAnswers, provisional = true, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("trustBeneficiary.name.checkYourAnswersLabel"), answer = Html("Trust"), changeUrl = Some(NameController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.discretionYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(DiscretionYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.shareOfIncome.checkYourAnswersLabel", name), answer = Html("50%"), changeUrl = Some(ShareOfIncomeController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.countryOfResidence.checkYourAnswersLabel", name), answer = Html("Germany"), changeUrl = Some(CountryOfResidenceController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.addressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressUkYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.ukAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = Some(UkAddressController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.nonUkAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = Some(NonUkAddressController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("trustBeneficiary.startDate.checkYourAnswersLabel", name), answer = Html("3 February 2019"), changeUrl = Some(StartDateController.onPageLoad().url))
          )
        )
      }

      "amend" in {

        val result = helper(userAnswers, provisional = false, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("trustBeneficiary.name.checkYourAnswersLabel"), answer = Html("Trust"), changeUrl = Some(NameController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.discretionYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(DiscretionYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.shareOfIncome.checkYourAnswersLabel", name), answer = Html("50%"), changeUrl = Some(ShareOfIncomeController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.checkDetails.utr.checkYourAnswersLabel", name), answer = Html("1234567890"), changeUrl = Some("")),
            AnswerRow(label = messages("trustBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.countryOfResidence.checkYourAnswersLabel", name), answer = Html("Germany"), changeUrl = Some(CountryOfResidenceController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.addressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressUkYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.ukAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = Some(UkAddressController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("trustBeneficiary.nonUkAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = Some(NonUkAddressController.onPageLoad(CheckMode).url))
          )
        )
      }
    }
  }
}
