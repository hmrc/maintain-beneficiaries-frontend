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

package navigation

import base.SpecBase
import controllers.companyoremploymentrelated.employment.routes._
import models.{CheckMode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.companyoremploymentrelated.employment._

class EmploymentRelatedBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new EmploymentRelatedBeneficiaryNavigator

  "Employment related beneficiary navigator" when {

    "Name page -> Address yes no page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(AddressYesNoController.onPageLoad(NormalMode))
    }

    "Address yes no page -> No -> Description page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(DescriptionController.onPageLoad(NormalMode))
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

    "UK address page -> Description page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(DescriptionController.onPageLoad(NormalMode))
    }

    "Non-UK address page -> Description page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(DescriptionController.onPageLoad(NormalMode))
    }

    "Description page -> Number of beneficiaries page" in {
      navigator.nextPage(DescriptionPage, emptyUserAnswers)
        .mustBe(NumberOfBeneficiariesController.onPageLoad(NormalMode))
    }

    "Number of beneficiaries page -> Start date page" in {
      navigator.nextPage(NumberOfBeneficiariesPage, emptyUserAnswers)
        .mustBe(StartDateController.onPageLoad())
    }

    "Start date page -> Check your answers page" in {
      navigator.nextPage(StartDatePage, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad())
    }

    "Number of beneficiaries page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(IndexPage, 0).success.value

      navigator.nextPage(NumberOfBeneficiariesPage, CheckMode, answers)
        .mustBe(controllers.companyoremploymentrelated.employment.amend.routes.CheckDetailsController.renderFromUserAnswers(0))
    }
  }
}
