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
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individualbeneficiary.{NamePage, _}

class IndividualBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new IndividualBeneficiaryNavigator

  "Individual beneficiary navigator" when {

    "Name page -> Do you know date of birth page" in {
      navigator.nextPage(NamePage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.DateOfBirthYesNoController.onPageLoad())
    }

    "Do you know date of birth page -> Yes -> Date of birth page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage, true).success.value

      navigator.nextPage(DateOfBirthYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.DateOfBirthController.onPageLoad())
    }

    "Date of birth page -> Do you know IncomeDiscretion page" in {
      navigator.nextPage(DateOfBirthPage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.IncomeDiscretionYesNoController.onPageLoad())
    }

    "Do you know date of birth page -> No -> IncomeDiscretion page" in {
      val answers = emptyUserAnswers
        .set(DateOfBirthYesNoPage, false).success.value

      navigator.nextPage(DateOfBirthYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.IncomeDiscretionYesNoController.onPageLoad())
    }

    "Do you know IncomeDiscretion page -> No -> IncomeDiscretion page" in {
      val answers = emptyUserAnswers
        .set(IncomeDiscretionYesNoPage, false).success.value

      navigator.nextPage(IncomeDiscretionYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.IncomePercentageController.onPageLoad())
    }

    "Do you know IncomeDiscretion page -> Yes -> Do you know NINO page" in {
      val answers = emptyUserAnswers
        .set(IncomeDiscretionYesNoPage, true).success.value

      navigator.nextPage(IncomeDiscretionYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.NationalInsuranceNumberYesNoController.onPageLoad())
    }

    "IncomeDiscretion page -> Do you know NINO page" in {
      navigator.nextPage(IncomePercentagePage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.NationalInsuranceNumberYesNoController.onPageLoad())
    }

    "Do you know NINO page -> Yes -> NINO page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage, true).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.NationalInsuranceNumberController.onPageLoad())
    }

    "NINO page -> VPE1 Yes No page" in {
      navigator.nextPage(NationalInsuranceNumberPage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.VPE1FormYesNoController.onPageLoad())
    }

    "Do you know NINO page -> No -> Do you know address page" in {
      val answers = emptyUserAnswers
        .set(NationalInsuranceNumberYesNoPage, false).success.value

      navigator.nextPage(NationalInsuranceNumberYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.AddressYesNoController.onPageLoad())
    }

    "Do you know address page -> Yes -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, true).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.LiveInTheUkYesNoController.onPageLoad())
    }

    "Do you know address page -> No -> VPE1 Yes No page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage, false).success.value

      navigator.nextPage(AddressYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.VPE1FormYesNoController.onPageLoad())
    }

    "Is address in UK page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, true).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.UkAddressController.onPageLoad())
    }

    "UK address page -> Do you know passport details page" in {
      navigator.nextPage(UkAddressPage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.PassportDetailsYesNoController.onPageLoad())
    }

    "Is address in UK page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(LiveInTheUkYesNoPage, false).success.value

      navigator.nextPage(LiveInTheUkYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.NonUkAddressController.onPageLoad())
    }

    "Non-UK address page -> Do you know passport details page" in {
      navigator.nextPage(NonUkAddressPage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.PassportDetailsYesNoController.onPageLoad())
    }

    "Do you know passport details page -> Yes -> Passport details page" in {
      val answers = emptyUserAnswers
        .set(PassportDetailsYesNoPage, true).success.value

      navigator.nextPage(PassportDetailsYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.PassportDetailsController.onPageLoad())
    }

    "Passport details page -> VPE1 Yes No page" in {
      navigator.nextPage(PassportDetailsPage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.VPE1FormYesNoController.onPageLoad())
    }

    "Do you know passport details page -> No -> Do you know ID card details page" in {
      val answers = emptyUserAnswers
        .set(PassportDetailsYesNoPage, false).success.value

      navigator.nextPage(PassportDetailsYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.IdCardDetailsYesNoController.onPageLoad())
    }

    "Do you know ID card details page -> Yes -> ID card details page" in {
      val answers = emptyUserAnswers
        .set(IdCardDetailsYesNoPage, true).success.value

      navigator.nextPage(IdCardDetailsYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.IdCardDetailsController.onPageLoad())
    }

    "ID card details page -> VPE1 Yes No page" in {
      navigator.nextPage(IdCardDetailsPage, emptyUserAnswers)
        .mustBe(controllers.individualbeneficiary.add.routes.VPE1FormYesNoController.onPageLoad())
    }

    "Do you know ID card details page -> No -> VPE1 Yes No page" in {
      val answers = emptyUserAnswers
        .set(IdCardDetailsYesNoPage, false).success.value

      navigator.nextPage(IdCardDetailsYesNoPage, answers)
        .mustBe(controllers.individualbeneficiary.add.routes.VPE1FormYesNoController.onPageLoad())
    }
  }
}
