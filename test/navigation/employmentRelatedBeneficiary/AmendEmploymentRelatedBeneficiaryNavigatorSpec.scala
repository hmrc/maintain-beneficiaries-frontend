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

package navigation.employmentRelatedBeneficiary

import base.SpecBase
import controllers.companyoremploymentrelated.employment.amend.routes._
import models.HowManyBeneficiaries.Over1
import navigation.employmentBeneficiary.AmendEmploymentRelatedBeneficiaryNavigator
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.companyoremploymentrelated.employment._

class AmendEmploymentRelatedBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new AmendEmploymentRelatedBeneficiaryNavigator

  "Amend employment related beneficiary navigator" when {

    "Name page -> Address yes no page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(AddressYesNoController.onPageLoad())
    }

    "Address yes no page -> No -> Description page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(DescriptionController.onPageLoad())
    }

    "Address yes no page -> Yes -> Address in the UK yes no page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(AddressUkYesNoController.onPageLoad())
    }

    "Address in the UK yes no page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, true).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(UkAddressController.onPageLoad())
    }

    "Address in the UK yes no page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, false).success.value

      navigator.nextPage(AddressUkYesNoPage, answers)
        .mustBe(NonUkAddressController.onPageLoad())
    }

    "UK address page -> Description page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(DescriptionController.onPageLoad())
    }

    "Non-UK address page -> Description page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(DescriptionController.onPageLoad())
    }

    "Description page -> Number of beneficiaries page" in {
      navigator.nextPage(DescriptionPage, emptyUserAnswers)
        .mustBe(NumberOfBeneficiariesController.onPageLoad())
    }

    "Number of beneficiaries page -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(IndexPage, 0).success.value

      navigator.nextPage(NumberOfBeneficiariesPage, answers)
        .mustBe(CheckDetailsController.renderFromUserAnswers(0))
    }
  }
}
