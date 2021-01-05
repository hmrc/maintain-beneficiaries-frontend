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

package navigation

import base.SpecBase
import controllers.other.routes._
import models.{CheckMode, Mode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.other._
import pages.other.add.StartDatePage
import pages.other.amend.IndexPage

class OtherBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new OtherBeneficiaryNavigator

  "Other beneficiary navigator" when {

    "add journey" must {

      val mode: Mode = NormalMode

      "Name page -> Discretion yes no page" in {
        navigator.nextPage(DescriptionPage, mode, emptyUserAnswers)
          .mustBe(DiscretionYesNoController.onPageLoad(mode))
      }

      "Discretion yes no page -> Yes -> Address yes no page" in {
        val answers = emptyUserAnswers
          .set(DiscretionYesNoPage, true).success.value

        navigator.nextPage(DiscretionYesNoPage, mode, answers)
          .mustBe(AddressYesNoController.onPageLoad(mode))
      }

      "Discretion yes no page -> No -> Share of income page" in {
        val answers = emptyUserAnswers
          .set(DiscretionYesNoPage, false).success.value

        navigator.nextPage(DiscretionYesNoPage, mode, answers)
          .mustBe(ShareOfIncomeController.onPageLoad(mode))
      }

      "Share of income page -> Address yes no page" in {
        navigator.nextPage(ShareOfIncomePage, mode, emptyUserAnswers)
          .mustBe(AddressYesNoController.onPageLoad(mode))
      }

      "Address yes no page -> No -> Start date page" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage, false).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(controllers.other.add.routes.StartDateController.onPageLoad())
      }

      "Address yes no page -> Yes -> Address in the UK yes no page" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage, true).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(AddressUkYesNoController.onPageLoad(mode))
      }

      "Address in the UK yes no page -> Yes -> UK address page" in {
        val answers = emptyUserAnswers
          .set(AddressUkYesNoPage, true).success.value

        navigator.nextPage(AddressUkYesNoPage, mode, answers)
          .mustBe(UkAddressController.onPageLoad(mode))
      }

      "Address in the UK yes no page -> No -> Non-UK address page" in {
        val answers = emptyUserAnswers
          .set(AddressUkYesNoPage, false).success.value

        navigator.nextPage(AddressUkYesNoPage, mode, answers)
          .mustBe(NonUkAddressController.onPageLoad(mode))
      }

      "UK address page -> Start date page" in {
        navigator.nextPage(UkAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.other.add.routes.StartDateController.onPageLoad())
      }

      "Non-UK address page -> Start date page" in {
        navigator.nextPage(NonUkAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.other.add.routes.StartDateController.onPageLoad())
      }

      "Start date page -> Check your answers page" in {
        navigator.nextPage(StartDatePage, mode, emptyUserAnswers)
          .mustBe(controllers.other.add.routes.CheckDetailsController.onPageLoad())
      }

    }

    "amend journey" must {

      val mode: Mode = CheckMode
      val index = 0
      val baseAnswers = emptyUserAnswers.set(IndexPage, index).success.value

      "Name page -> Discretion yes no page" in {
        navigator.nextPage(DescriptionPage, mode, baseAnswers)
          .mustBe(DiscretionYesNoController.onPageLoad(mode))
      }

      "Discretion yes no page -> Yes -> Address yes no page" in {
        val answers = baseAnswers
          .set(DiscretionYesNoPage, true).success.value

        navigator.nextPage(DiscretionYesNoPage, mode, answers)
          .mustBe(AddressYesNoController.onPageLoad(mode))
      }

      "Discretion yes no page -> No -> Share of income page" in {
        val answers = baseAnswers
          .set(DiscretionYesNoPage, false).success.value

        navigator.nextPage(DiscretionYesNoPage, mode, answers)
          .mustBe(ShareOfIncomeController.onPageLoad(mode))
      }

      "Share of income page -> Address yes no page" in {
        navigator.nextPage(ShareOfIncomePage, mode, baseAnswers)
          .mustBe(AddressYesNoController.onPageLoad(mode))
      }

      "Address yes no page -> No -> Check details page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage, false).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(controllers.other.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
      }

      "Address yes no page -> Yes -> Address in the UK yes no page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage, true).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(AddressUkYesNoController.onPageLoad(mode))
      }

      "Address in the UK yes no page -> Yes -> UK address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage, true).success.value

        navigator.nextPage(AddressUkYesNoPage, mode, answers)
          .mustBe(UkAddressController.onPageLoad(mode))
      }

      "Address in the UK yes no page -> No -> Non-UK address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage, false).success.value

        navigator.nextPage(AddressUkYesNoPage, mode, answers)
          .mustBe(NonUkAddressController.onPageLoad(mode))
      }

      "UK address page -> Check details page" in {
        navigator.nextPage(UkAddressPage, mode, baseAnswers)
          .mustBe(controllers.other.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
      }

      "Non-UK address page -> Check details page" in {
        navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
          .mustBe(controllers.other.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
      }

    }
  }
}
