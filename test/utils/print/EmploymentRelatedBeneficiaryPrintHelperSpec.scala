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
import controllers.companyoremploymentrelated.employment.routes._
import models.{CheckMode, Description, HowManyBeneficiaries, NonUkAddress, NormalMode, UkAddress}
import pages.companyoremploymentrelated.employment._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class EmploymentRelatedBeneficiaryPrintHelperSpec extends SpecBase {

  val name: String = "Large"
  val utr: String = "1234567890"
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val country: String = "DE"
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, country)
  val description: Description = Description("Description", Some("Another description"), None, None, None)
  val numberOfBeneficiaries: HowManyBeneficiaries = HowManyBeneficiaries.Over201
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "EmploymentRelatedBeneficiaryPrintHelper" must {

    val helper = injector.instanceOf[EmploymentRelatedBeneficiaryPrintHelper]

    val userAnswers = emptyUserAnswers
      .set(NamePage, name).success.value
      .set(UtrPage, utr).success.value
      .set(CountryOfResidenceYesNoPage, true).success.value
      .set(CountryOfResidenceUkYesNoPage, false).success.value
      .set(CountryOfResidencePage, country).success.value
      .set(AddressYesNoPage, true).success.value
      .set(AddressUkYesNoPage, true).success.value
      .set(UkAddressPage, ukAddress).success.value
      .set(NonUkAddressPage, nonUKAddress).success.value
      .set(DescriptionPage, description).success.value
      .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
      .set(StartDatePage, date).success.value

    "generate employment related beneficiary section" when {

      "added" in {

        val result = helper(userAnswers, provisional = true, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("employmentBeneficiary.name.checkYourAnswersLabel"), answer = Html("Large"), changeUrl = Some(NameController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.countryOfResidence.checkYourAnswersLabel", name), answer = Html("Germany"), changeUrl = Some(CountryOfResidenceController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.addressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressUkYesNoController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.ukAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = Some(UkAddressController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.nonUkAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = Some(NonUkAddressController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.description.checkYourAnswersLabel", name), answer = Html("Description<br />Another description"), changeUrl = Some(DescriptionController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.numberOfBeneficiaries.checkYourAnswersLabel", name), answer = Html("201 to 500"), changeUrl = Some(NumberOfBeneficiariesController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.startDate.checkYourAnswersLabel", name), answer = Html("3 February 2019"), changeUrl = Some(StartDateController.onPageLoad().url))
          )
        )
      }

      "amended" in {

        val result = helper(userAnswers, provisional = false, name)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("employmentBeneficiary.name.checkYourAnswersLabel"), answer = Html("Large"), changeUrl = Some(NameController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.checkDetails.utr.checkYourAnswersLabel", name), answer = Html("1234567890"), changeUrl = Some("")),
            AnswerRow(label = messages("employmentBeneficiary.countryOfResidenceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.countryOfResidenceUkYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(CountryOfResidenceUkYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.countryOfResidence.checkYourAnswersLabel", name), answer = Html("Germany"), changeUrl = Some(CountryOfResidenceController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.addressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(AddressUkYesNoController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.ukAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = Some(UkAddressController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.nonUkAddress.checkYourAnswersLabel", name), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = Some(NonUkAddressController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.description.checkYourAnswersLabel", name), answer = Html("Description<br />Another description"), changeUrl = Some(DescriptionController.onPageLoad(CheckMode).url)),
            AnswerRow(label = messages("employmentBeneficiary.numberOfBeneficiaries.checkYourAnswersLabel", name), answer = Html("201 to 500"), changeUrl = Some(NumberOfBeneficiariesController.onPageLoad(CheckMode).url))
          )
        )
      }
    }
  }
}
