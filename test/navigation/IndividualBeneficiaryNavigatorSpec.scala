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
import models.{CheckMode, Mode, NormalMode, TypeOfTrust}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individualbeneficiary.add._
import pages.individualbeneficiary.amend.IndexPage
import pages.individualbeneficiary.{NamePage, _}
import utils.Constants.ES

class IndividualBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new IndividualBeneficiaryNavigator

  "Individual beneficiary navigator" when {

    "4mld" must {

      "add journey" must {

        val mode: Mode = NormalMode
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true)

        "employment related trust" must {

          "Name page -> Role in company page" in {
            navigator.nextPage(NamePage, mode, baseAnswers.copy(trustType = Some(TypeOfTrust.EmployeeRelated)))
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
          navigator.nextPage(IdCardDetailsPage, baseAnswers)
            .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
        }

        "Do you know ID card details page -> No -> VPE1 Yes No page" in {
          val answers = baseAnswers
            .set(IdCardDetailsYesNoPage, false).success.value

          navigator.nextPage(IdCardDetailsYesNoPage, mode, answers)
            .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
        }

        "VPE1 Yes No page -> Start date page" in {
          navigator.nextPage(VPE1FormYesNoPage, mode, baseAnswers)
            .mustBe(controllers.individualbeneficiary.add.routes.StartDateController.onPageLoad())
        }

        "Start date page -> Check details page" in {
          navigator.nextPage(StartDatePage, mode, baseAnswers)
            .mustBe(controllers.individualbeneficiary.add.routes.CheckDetailsController.onPageLoad())
        }
      }

      "amend journey" must {

        val mode: Mode = CheckMode
        val index = 0
        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true).set(IndexPage, index).success.value

        "employment related trust" must {

          "Name page -> Role in company page" in {
            navigator.nextPage(NamePage, mode, emptyUserAnswers.copy(trustType = Some(TypeOfTrust.EmployeeRelated)))
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

        "UK address page -> Do you know passport or ID card details page" in {
          navigator.nextPage(UkAddressPage, mode, baseAnswers)
            .mustBe(controllers.individualbeneficiary.routes.PassportOrIdCardDetailsYesNoController.onPageLoad(mode))
        }

        "Is address in UK page -> No -> Non-UK address page" in {
          val answers = baseAnswers
            .set(LiveInTheUkYesNoPage, false).success.value

          navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
            .mustBe(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(mode))
        }

        "Non-UK address page -> Do you know passport or ID card details page" in {
          navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
            .mustBe(controllers.individualbeneficiary.routes.PassportOrIdCardDetailsYesNoController.onPageLoad(mode))
        }

        "Do you know passport or ID card details page -> Yes -> Passport or ID card details page" in {
          val answers = baseAnswers
            .set(PassportOrIdCardDetailsYesNoPage, true).success.value

          navigator.nextPage(PassportOrIdCardDetailsYesNoPage, mode, answers)
            .mustBe(controllers.individualbeneficiary.routes.PassportOrIdCardDetailsController.onPageLoad(mode))
        }

        "Passport or ID card details page -> VPE1 Yes No page" in {
          navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
            .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
        }

        "Do you know passport or ID card details page -> No -> VPE1 Yes No page" in {
          val answers = baseAnswers
            .set(PassportOrIdCardDetailsYesNoPage, false).success.value

          navigator.nextPage(PassportOrIdCardDetailsYesNoPage, mode, answers)
            .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
        }

        "VPE1 Yes No page -> Check details page" in {
          navigator.nextPage(VPE1FormYesNoPage, mode, baseAnswers)
            .mustBe(controllers.individualbeneficiary.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
        }
      }

    }


    "5mld" when {

      "taxable" must {

        "add journey" must {


          val mode: Mode = NormalMode
          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)

          "employment related trust" must {

            "Name page -> Role in company page" in {
              navigator.nextPage(NamePage, mode, baseAnswers.copy(trustType = Some(TypeOfTrust.EmployeeRelated)))
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

          "Do you know IncomeDiscretion page -> Yes -> Do you know Country of Nationality page" in {
            val answers = baseAnswers
              .set(IncomeDiscretionYesNoPage, true).success.value

            navigator.nextPage(IncomeDiscretionYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "IncomeDiscretion page -> Do you know Country of Nationality page" in {
            navigator.nextPage(IncomePercentagePage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }


          "CountryOfNationality yes no page -> No -> Nino yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityUkYesNoController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> Yes -> Nino yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality -> Nino Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityPage, ES).success.value

            navigator.nextPage(CountryOfNationalityPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "NationalInsuranceNumber yes no page -> No -> CountryOfResidence yes no page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value

            navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> No -> Address yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceUkYesNoController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> Yes -> Address yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence (with Nino) -> MentalCapacityYesNo page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value
              .set(CountryOfResidencePage, ES).success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence (with No Nino) -> Address Yes No page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value
              .set(CountryOfResidencePage, ES).success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
          }

          "Address Yes No page -> No -> MentalCapacityYesNo page" in {
            val answers = baseAnswers
              .set(AddressYesNoPage, false).success.value

            navigator.nextPage(AddressYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "PassportDetailsPage -> MentalCapacityYesNo page" in {
            navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "IDCardDetails Yes No page -> No -> MentalCapacityYesNo page" in {
            val answers = baseAnswers
              .set(IdCardDetailsYesNoPage, false).success.value

            navigator.nextPage(IdCardDetailsYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "IDCardDetails page -> MentalCapacityYesNo page" in {
            navigator.nextPage(IdCardDetailsPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "MentalCapacityYesNo page -> Has VPE1 been submitted page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
          }

          "Has VPE1 been submitted page -> Start date page" in {
            navigator.nextPage(VPE1FormYesNoPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.add.routes.StartDateController.onPageLoad())
          }

          "Start date page -> Check details page" in {
            navigator.nextPage(StartDatePage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.add.routes.CheckDetailsController.onPageLoad())
          }

        }


        "amend journey" must {

          val mode: Mode = CheckMode
          val index = 0
          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true).set(IndexPage, index).success.value

          "employment related trust" must {

            "Name page -> Role in company page" in {
              navigator.nextPage(NamePage, mode, emptyUserAnswers.copy(trustType = Some(TypeOfTrust.EmployeeRelated)))
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

          "Do you know IncomeDiscretion page -> Yes -> Do you know Country of Nationality page" in {
            val answers = baseAnswers
              .set(IncomeDiscretionYesNoPage, true).success.value

            navigator.nextPage(IncomeDiscretionYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "IncomeDiscretion page -> Do you know Country of Nationality page" in {
            navigator.nextPage(IncomePercentagePage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality yes no page -> No -> Nino yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityUkYesNoController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> Yes -> Nino yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality -> Nino Yes No page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityPage, ES).success.value

            navigator.nextPage(CountryOfNationalityPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }


          "Do you know NINO page -> Yes -> NINO page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value

            navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NationalInsuranceNumberController.onPageLoad(mode))
          }

          "NINO page -> Do you know Country of Residence page" in {
            navigator.nextPage(NationalInsuranceNumberPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Do you know NINO page -> No -> Do you know Country of Residence page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value

            navigator.nextPage(NationalInsuranceNumberYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> No -> Address yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceUkYesNoController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> Yes -> Address yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence (with Nino) -> MentalCapacityYesNo page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, true).success.value
              .set(CountryOfResidencePage, ES).success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence (with No Nino) -> Address Yes No page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberYesNoPage, false).success.value
              .set(CountryOfResidencePage, ES).success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.AddressYesNoController.onPageLoad(mode))
          }

          "Do you know address page -> Yes -> Is address in UK page" in {
            val answers = baseAnswers
              .set(AddressYesNoPage, true).success.value

            navigator.nextPage(AddressYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.LiveInTheUkYesNoController.onPageLoad(mode))
          }

          "Do you know address page -> No -> Mental Capacity Yes No page" in {
            val answers = baseAnswers
              .set(AddressYesNoPage, false).success.value

            navigator.nextPage(AddressYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Is address in UK page -> Yes -> UK address page" in {
            val answers = baseAnswers
              .set(LiveInTheUkYesNoPage, true).success.value

            navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.UkAddressController.onPageLoad(mode))
          }

          "UK address page -> Do you know passport or ID card details page" in {
            navigator.nextPage(UkAddressPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.PassportOrIdCardDetailsYesNoController.onPageLoad(mode))
          }

          "Is address in UK page -> No -> Non-UK address page" in {
            val answers = baseAnswers
              .set(LiveInTheUkYesNoPage, false).success.value

            navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(mode))
          }

          "Non-UK address page -> Do you know passport or ID card details page" in {
            navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.PassportOrIdCardDetailsYesNoController.onPageLoad(mode))
          }

          "Do you know passport or ID card details page -> Yes -> Passport or ID card details page" in {
            val answers = baseAnswers
              .set(PassportOrIdCardDetailsYesNoPage, true).success.value

            navigator.nextPage(PassportOrIdCardDetailsYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.PassportOrIdCardDetailsController.onPageLoad(mode))
          }

          "Passport or ID card details page -> Mental Capacity Yes No page" in {
            navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Do you know passport or ID card details page -> No -> Mental Capacity Yes No page" in {
            val answers = baseAnswers
              .set(PassportOrIdCardDetailsYesNoPage, false).success.value

            navigator.nextPage(PassportOrIdCardDetailsYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "MentalCapacityYesNo page -> Has VPE1 been submitted page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.VPE1FormYesNoController.onPageLoad(mode))
          }

          "VPE1 Yes No page -> Check details page" in {
            navigator.nextPage(VPE1FormYesNoPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
          }

        }


      }


      "non-taxable" must {

        "add journey" must {


          val mode: Mode = NormalMode
          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)

          "employment related trust" must {

            "Name page -> Role in company page" in {
              navigator.nextPage(NamePage, mode, baseAnswers.copy(trustType = Some(TypeOfTrust.EmployeeRelated)))
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

          "Date of birth page -> Do you know Country of Nationality page" in {
            navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "Do you know date of birth page -> No -> Do you know Country of Nationality page" in {
            val answers = baseAnswers
              .set(DateOfBirthYesNoPage, false).success.value

            navigator.nextPage(DateOfBirthYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality yes no page -> No -> Do you know Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityUkYesNoController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> Yes -> Do you know Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality -> Do you know Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityPage, ES).success.value

            navigator.nextPage(CountryOfNationalityPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> No -> Mental Capacity yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceUkYesNoController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> Yes -> Mental Capacity yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence -> Mental Capacity yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidencePage, ES).success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "MentalCapacityYesNo page -> Start date page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.add.routes.StartDateController.onPageLoad())
          }

          "Start date page -> Check details page" in {
            navigator.nextPage(StartDatePage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.add.routes.CheckDetailsController.onPageLoad())
          }

        }


        "amend journey" must {

          val mode: Mode = CheckMode
          val index = 0
          val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false).set(IndexPage, index).success.value

          "employment related trust" must {

            "Name page -> Role in company page" in {
              navigator.nextPage(NamePage, mode, emptyUserAnswers.copy(trustType = Some(TypeOfTrust.EmployeeRelated)))
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

          "Date of birth page -> Do you know Country of Nationality page" in {
            navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "Do you know date of birth page -> No -> Do you know Country of Nationality page" in {
            val answers = baseAnswers
              .set(DateOfBirthYesNoPage, false).success.value

            navigator.nextPage(DateOfBirthYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality yes no page -> No -> Do you know Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityUkYesNoController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfNationalityController.onPageLoad(mode))
          }

          "CountryOfNationalityInUK yes no page -> Yes -> Do you know Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfNationalityUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfNationality -> Do you know Country of Residence page" in {
            val answers = baseAnswers
              .set(CountryOfNationalityPage, ES).success.value

            navigator.nextPage(CountryOfNationalityPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> No -> Mental Capacity yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceUkYesNoController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.CountryOfResidenceController.onPageLoad(mode))
          }

          "CountryOfResidenceInUK yes no page -> Yes -> Mental Capacity yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "CountryOfResidence -> Mental Capacity yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidencePage, ES).success.value

            navigator.nextPage(CountryOfResidencePage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "MentalCapacityYesNo page -> Check details page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
          }


        }

      }

    }

  }
}
