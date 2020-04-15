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
import controllers.companyoremploymentrelated.company.amend.routes._
import models.{NonUkAddress, UkAddress}
import pages.companyoremploymentrelated.company._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class AmendCompanyBeneficiaryPrintHelperSpec extends SpecBase {

  val name: String = "Company"
  val share: Int = 50
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val nonUKAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "DE")

  "CompanyBeneficiaryPrintHelper" must {

    "generate company beneficiary section" in {

      val helper = injector.instanceOf[AmendCompanyBeneficiaryPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DiscretionYesNoPage, false).success.value
        .set(ShareOfIncomePage, share).success.value
        .set(AddressYesNoPage, true).success.value
        .set(AddressUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(NonUkAddressPage, nonUKAddress).success.value

      val result = helper(userAnswers, name)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("companyBeneficiary.name.checkYourAnswersLabel")), answer = Html("Company"), changeUrl = NameController.onPageLoad().url),
          AnswerRow(label = Html(messages("companyBeneficiary.discretionYesNo.checkYourAnswersLabel", name)), answer = Html("No"), changeUrl = DiscretionYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("companyBeneficiary.shareOfIncome.checkYourAnswersLabel", name)), answer = Html("50%"), changeUrl = ShareOfIncomeController.onPageLoad().url),
          AnswerRow(label = Html(messages("companyBeneficiary.addressYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("companyBeneficiary.addressUkYesNo.checkYourAnswersLabel", name)), answer = Html("Yes"), changeUrl = AddressUkYesNoController.onPageLoad().url),
          AnswerRow(label = Html(messages("companyBeneficiary.ukAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />postcode"), changeUrl = UkAddressController.onPageLoad().url),
          AnswerRow(label = Html(messages("companyBeneficiary.nonUkAddress.checkYourAnswersLabel", name)), answer = Html("Line 1<br />Line 2<br />Germany"), changeUrl = NonUkAddressController.onPageLoad().url)
        )
      )
    }
  }
}