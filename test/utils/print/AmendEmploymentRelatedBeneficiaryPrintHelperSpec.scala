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
import controllers.companyoremploymentrelated.employment.amend.routes._
import models.{Description, HowManyBeneficiaries, NonUkAddress, UkAddress}
import pages.companyoremploymentrelated.employment._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AmendEmploymentRelatedBeneficiaryPrintHelperSpec extends SpecBase {

  val name: String = "Large"
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "DE")
  val description: Description = Description("Description", Some("Another description"), None, None, None)
  val numberOfBeneficiaries: HowManyBeneficiaries = HowManyBeneficiaries.Over201
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "AmendEmploymentRelatedBeneficiaryPrintHelper" must {

    "generate employment related beneficiary section" in {

      val helper = injector.instanceOf[AmendEmploymentRelatedBeneficiaryPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(AddressYesNoPage, true).success.value
        .set(AddressUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(NonUkAddressPage, nonUKAddress).success.value
        .set(DescriptionPage, description).success.value
        .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value

      val result = helper(userAnswers, name)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("employmentBeneficiary.name.checkYourAnswersLabel")), answer = Html("Large"), changeUrl = NameController.onPageLoad().url),
          AnswerRow(label = Html(messages("employmentBeneficiary.addressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("employmentBeneficiary.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("employmentBeneficiary.ukAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("employmentBeneficiary.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("employmentBeneficiary.description.checkYourAnswersLabel", name)), answer = Html("Description<br />Another description"), changeUrl = DescriptionController.onPageLoad().url),
          AnswerRow(label = Html(messages("employmentBeneficiary.numberOfBeneficiaries.checkYourAnswersLabel", name)), answer = Html("201 to 500"), changeUrl = NumberOfBeneficiariesController.onPageLoad().url)
        )
      )
    }
  }
}
