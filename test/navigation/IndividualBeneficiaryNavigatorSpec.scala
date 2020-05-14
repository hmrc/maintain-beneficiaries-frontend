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

import java.time.LocalDate

import base.SpecBase
import models.{CheckMode, Mode, NormalMode, TypeOfTrust, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individualbeneficiary.{NamePage, _}

class IndividualBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new IndividualBeneficiaryNavigator

  "Individual beneficiary navigator" when {

    "add journey" must {

      val mode: Mode = NormalMode

      "employment related trust" must {

        "Name page -> Role in company page" in {
          navigator.nextPage(NamePage, mode, UserAnswers("id", "utr", LocalDate.parse("2019-02-10"), TypeOfTrust.EmployeeRelated))
            .mustBe(controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(mode))
        }

      }

      "not an employment related trust" must {

        "Name page -> Do you know date of birth page" in {
          navigator.nextPage(NamePage, mode, emptyUserAnswers)
            .mustBe(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(mode))
        }

      }

      "Role in company page -> Do you know date of birth page" in {
        navigator.nextPage(RoleInCompanyPage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(mode))
      }

      "Do you know date of birth page -> Yes -> Date of birth page" in {
        val answers = emptyUserAnswers
          .set(DateOfBirthYesNoPage, true).success.value

        navigator.nextPage(DateOfBirthYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(mode))
      }

      "Date of birth page -> Do you know IncomeDiscretion page" in {
        navigator.nextPage(DateOfBirthPage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(mode))
      }

      "Do you know date of birth page -> No -> IncomeDiscretion page" in {
        val answers = emptyUserAnswers
          .set(DateOfBirthYesNoPage, false).success.value

        navigator.nextPage(DateOfBirthYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(mode))
      }

      "Do you know IncomeDiscretion page -> No -> IncomeDiscretion page" in {
        val answers = emptyUserAnswers
          .set(IncomeDiscretionYesNoPage, false).success.value

        navigator.nextPage(IncomeDiscretionYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(mode))
      }

      "Do you know IncomeDiscretion page -> Yes -> Do you know NINO page" in {
        val answers = emptyUserAnswers
          .set(IncomeDiscretionYesNoPage, true).success.value

        navigator.nextPage(IncomeDiscretionYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
      }

      "IncomeDiscretion page -> Do you know NINO page" in {
        navigator.nextPage(IncomePercentagePage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
      }

      "Do you know NINO page -> Yes -> NINO page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(mode))
      }

      "NINO page -> VPE1 Yes No page" in {
        navigator.nextPage(NationalInsuranceNumberPage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Do you know NINO page -> No -> Do you know address page" in {
        val answers = emptyUserAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
      }

      "Do you know address page -> Yes -> Is address in UK page" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage, true).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Do you know address page -> No -> VPE1 Yes No page" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage, false).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Is address in UK page -> Yes -> UK address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, true).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(mode))
      }

      "UK address page -> Do you know passport details page" in {
        navigator.nextPage(UkAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(mode))
      }

      "Is address in UK page -> No -> Non-UK address page" in {
        val answers = emptyUserAnswers
          .set(LiveInTheUkYesNoPage, false).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(mode))
      }

      "Non-UK address page -> Do you know passport details page" in {
        navigator.nextPage(NonUkAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(mode))
      }

      "Do you know passport details page -> Yes -> Passport details page" in {
        val answers = emptyUserAnswers
          .set(PassportDetailsYesNoPage, true).success.value

        navigator.nextPage(PassportDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.PassportDetailsController.onPageLoad(mode))
      }

      "Passport details page -> VPE1 Yes No page" in {
        navigator.nextPage(PassportDetailsPage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Do you know passport details page -> No -> Do you know ID card details page" in {
        val answers = emptyUserAnswers
          .set(PassportDetailsYesNoPage, false).success.value

        navigator.nextPage(PassportDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IdCardDetailsYesNoController.onPageLoad(mode))
      }

      "Do you know ID card details page -> Yes -> ID card details page" in {
        val answers = emptyUserAnswers
          .set(IdCardDetailsYesNoPage, true).success.value

        navigator.nextPage(IdCardDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IdCardDetailsController.onPageLoad(mode))
      }

      "ID card details page -> VPE1 Yes No page" in {
        navigator.nextPage(IdCardDetailsPage, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Do you know ID card details page -> No -> VPE1 Yes No page" in {
        val answers = emptyUserAnswers
          .set(IdCardDetailsYesNoPage, false).success.value

        navigator.nextPage(IdCardDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "VPE1 Yes No page -> Start date page" in {
        navigator.nextPage(VPE1FormYesNoPage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.routes.StartDateController.onPageLoad())
      }

      "Start date page -> Check details page" in {
        navigator.nextPage(StartDatePage, mode, emptyUserAnswers)
          .mustBe(controllers.individualbeneficiary.add.routes.CheckDetailsController.onPageLoad())
      }
    }

    "amend journey" must {

      val mode: Mode = CheckMode
      val index = 0
      val baseAnswers = emptyUserAnswers.set(IndexPage, index).success.value

      "employment related trust" must {

        "Name page -> Role in company page" in {
          navigator.nextPage(NamePage, mode, UserAnswers("id", "utr", LocalDate.parse("2019-02-10"), TypeOfTrust.EmployeeRelated))
            .mustBe(controllers.individualbeneficiary.routes.RoleInCompanyController.onPageLoad(mode))
        }

      }

      "not an employment related trust" must {

        "Name page -> Do you know date of birth page" in {
          navigator.nextPage(NamePage, mode, baseAnswers)
            .mustBe(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(mode))
        }

      }

      "Role in company page -> Do you know date of birth page" in {
        navigator.nextPage(RoleInCompanyPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.DateOfBirthYesNoController.onPageLoad(mode))
      }

      "Do you know date of birth page -> Yes -> Date of birth page" in {
        val answers = baseAnswers
          .set(DateOfBirthYesNoPage, true).success.value

        navigator.nextPage(DateOfBirthYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.DateOfBirthController.onPageLoad(mode))
      }

      "Date of birth page -> Do you know IncomeDiscretion page" in {
        navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(mode))
      }

      "Do you know date of birth page -> No -> IncomeDiscretion page" in {
        val answers = baseAnswers
          .set(DateOfBirthYesNoPage, false).success.value

        navigator.nextPage(DateOfBirthYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IncomeDiscretionYesNoController.onPageLoad(mode))
      }

      "Do you know IncomeDiscretion page -> No -> IncomeDiscretion page" in {
        val answers = baseAnswers
          .set(IncomeDiscretionYesNoPage, false).success.value

        navigator.nextPage(IncomeDiscretionYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IncomePercentageController.onPageLoad(mode))
      }

      "Do you know IncomeDiscretion page -> Yes -> Do you know NINO page" in {
        val answers = baseAnswers
          .set(IncomeDiscretionYesNoPage, true).success.value

        navigator.nextPage(IncomeDiscretionYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
      }

      "IncomeDiscretion page -> Do you know NINO page" in {
        navigator.nextPage(IncomePercentagePage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
      }

      "Do you know NINO page -> Yes -> NINO page" in {
        val answers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(mode))
      }

      "NINO page -> VPE1 Yes No page" in {
        navigator.nextPage(NationalInsuranceNumberPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Do you know NINO page -> No -> Do you know address page" in {
        val answers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
      }

      "Do you know address page -> Yes -> Is address in UK page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage, true).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(mode))
      }

      "Do you know address page -> No -> VPE1 Yes No page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage, false).success.value

        navigator.nextPage(AddressYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Is address in UK page -> Yes -> UK address page" in {
        val answers = baseAnswers
          .set(LiveInTheUkYesNoPage, true).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(mode))
      }

      "UK address page -> Do you know passport details page" in {
        navigator.nextPage(UkAddressPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(mode))
      }

      "Is address in UK page -> No -> Non-UK address page" in {
        val answers = baseAnswers
          .set(LiveInTheUkYesNoPage, false).success.value

        navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(mode))
      }

      "Non-UK address page -> Do you know passport details page" in {
        navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(mode))
      }

      "Do you know passport details page -> Yes -> Passport details page" in {
        val answers = baseAnswers
          .set(PassportDetailsYesNoPage, true).success.value

        navigator.nextPage(PassportDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.PassportDetailsController.onPageLoad(mode))
      }

      "Passport details page -> VPE1 Yes No page" in {
        navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Do you know passport details page -> No -> Do you know ID card details page" in {
        val answers = baseAnswers
          .set(PassportDetailsYesNoPage, false).success.value

        navigator.nextPage(PassportDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IdCardDetailsYesNoController.onPageLoad(mode))
      }

      "Do you know ID card details page -> Yes -> ID card details page" in {
        val answers = baseAnswers
          .set(IdCardDetailsYesNoPage, true).success.value

        navigator.nextPage(IdCardDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.IdCardDetailsController.onPageLoad(mode))
      }

      "ID card details page -> VPE1 Yes No page" in {
        navigator.nextPage(IdCardDetailsPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "Do you know ID card details page -> No -> VPE1 Yes No page" in {
        val answers = baseAnswers
          .set(IdCardDetailsYesNoPage, false).success.value

        navigator.nextPage(IdCardDetailsYesNoPage, mode, answers)
          .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
      }

      "VPE1 Yes No page -> Check details page" in {
        navigator.nextPage(VPE1FormYesNoPage, mode, baseAnswers)
          .mustBe(controllers.individualbeneficiary.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
      }
    }
  }
}
