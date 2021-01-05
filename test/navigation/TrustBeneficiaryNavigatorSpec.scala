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
import controllers.charityortrust.trust.routes._
import models.{CheckMode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.charityortrust.trust._

class TrustBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new TrustBeneficiaryNavigator

  "Charity beneficiary navigator" when {

    "Name page -> Discretion yes no page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(DiscretionYesNoController.onPageLoad(NormalMode))
    }

    "Discretion yes no page -> Yes -> Address yes no page" in {
      val answers = emptyUserAnswers
        .set(DiscretionYesNoPage, true).success.value

      navigator.nextPage(DiscretionYesNoPage, answers)
        .mustBe(AddressYesNoController.onPageLoad(NormalMode))
    }

    "Discretion yes no page -> No -> Share of income page" in {
      val answers = emptyUserAnswers
        .set(DiscretionYesNoPage, false).success.value

      navigator.nextPage(DiscretionYesNoPage, answers)
        .mustBe(ShareOfIncomeController.onPageLoad(NormalMode))
    }

    "Share of income page -> Address yes no page" in {
      navigator.nextPage(ShareOfIncomePage, emptyUserAnswers)
        .mustBe(AddressYesNoController.onPageLoad(NormalMode))
    }

    "Address yes no page -> No -> Start date page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(StartDateController.onPageLoad())
    }

    "Address yes no page -> Yes -> Address in the UK yes no page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(AddressUkYesNoController.onPageLoad(NormalMode))
    }

    "Address in the UK yes no page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, true).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(UkAddressController.onPageLoad(NormalMode))
    }

    "Address in the UK yes no page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, false).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(NonUkAddressController.onPageLoad(NormalMode))
    }

    "UK address page -> Start date page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(StartDateController.onPageLoad())
    }

    "Non-UK address page -> Start date page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(StartDateController.onPageLoad())
    }

    "Start date page -> Check your answers page" in {
      navigator.nextPage(StartDatePage, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad())
    }

    "Address yes no page -> No -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value
        .set(IndexPage, 0).success.value

      navigator.nextPage(AddressYesNoPage, CheckMode, answers)
        .mustBe(controllers.charityortrust.trust.amend.routes.CheckDetailsController.renderFromUserAnswers(0))
    }

    "UK address page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(IndexPage, 0).success.value

      navigator.nextPage(UkAddressPage, CheckMode, answers)
        .mustBe(controllers.charityortrust.trust.amend.routes.CheckDetailsController.renderFromUserAnswers(0))
    }

    "Non-UK address page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value
        .set(IndexPage, 0).success.value

      navigator.nextPage(NonUkAddressPage, CheckMode, answers)
        .mustBe(controllers.charityortrust.trust.amend.routes.CheckDetailsController.renderFromUserAnswers(0))
    }
  }
}
