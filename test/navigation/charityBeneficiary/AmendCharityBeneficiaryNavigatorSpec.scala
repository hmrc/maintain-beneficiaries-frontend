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

package navigation.charityBeneficiary

import base.SpecBase
import controllers.charityortrust.charity.{routes => rts}
import models.CheckMode
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.charityortrust.charity._

class AmendCharityBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new AmendCharityBeneficiaryNavigator

  "Charity beneficiary navigator" when {

    "Name page -> Discretion yes no page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(rts.DiscretionYesNoController.onPageLoad(CheckMode))
    }

    "Discretion yes no page -> Yes -> Address yes no page" in {
      val answers = emptyUserAnswers
        .set(DiscretionYesNoPage, true).success.value

      navigator.nextPage(DiscretionYesNoPage, answers)
        .mustBe(rts.AddressYesNoController.onPageLoad(CheckMode))
    }

    "Discretion yes no page -> No -> Share of income page" in {
      val answers = emptyUserAnswers
        .set(DiscretionYesNoPage, false).success.value

      navigator.nextPage(DiscretionYesNoPage, answers)
        .mustBe(rts.ShareOfIncomeController.onPageLoad(CheckMode))
    }

    "Share of income page -> Address yes no page" in {
      navigator.nextPage(ShareOfIncomePage, emptyUserAnswers)
        .mustBe(rts.AddressYesNoController.onPageLoad(CheckMode))
    }

    "Address yes no page -> No -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value
        .set(IndexPage, 0).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.charityortrust.charity.amend.routes.CheckDetailsController.renderFromUserAnswers(0))
    }

    "Address yes no page -> Yes -> Address in the UK yes no page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(rts.AddressUkYesNoController.onPageLoad(CheckMode))
    }

    "Address in the UK yes no page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, true).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(rts.UkAddressController.onPageLoad(CheckMode))
    }

    "Address in the UK yes no page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, false).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(rts.NonUkAddressController.onPageLoad(CheckMode))
    }

    "UK address page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(IndexPage, 0).success.value

      navigator.nextPage(UkAddressPage, answers)
        .mustBe(controllers.charityortrust.charity.amend.routes.CheckDetailsController.renderFromUserAnswers(0))
    }

    "Non-UK address page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value
        .set(IndexPage, 0).success.value

      navigator.nextPage(NonUkAddressPage, answers)
        .mustBe(controllers.charityortrust.charity.amend.routes.CheckDetailsController.renderFromUserAnswers(0))
    }
  }
}
