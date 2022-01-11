/*
 * Copyright 2022 HM Revenue & Customs
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
import models.{CheckMode, CombinedPassportOrIdCard, Mode, NormalMode, TypeOfTrust}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individualbeneficiary.add._
import pages.individualbeneficiary.amend.IndexPage
import pages.individualbeneficiary._
import utils.Constants.ES

import java.time.LocalDate

class IndividualBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new IndividualBeneficiaryNavigator

  "Individual beneficiary navigator" when {
    
      "taxable" must {

        "add journey" must {

          val mode: Mode = NormalMode
          val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

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
          val passportOrId = CombinedPassportOrIdCard("FR", "num", LocalDate.parse("2020-01-01"))

          val baseAnswers = emptyUserAnswers.copy(isTaxable = true).set(IndexPage, index).success.value
          val combinedBaseAnswers = baseAnswers.set(PassportOrIdCardDetailsPage, passportOrId).success.value

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

          "UK address page -> Do you know passport or ID card details page" must {
            "combined passport/id card details not present" in {
              navigator.nextPage(UkAddressPage, mode, baseAnswers)
                .mustBe(controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(mode))
            }

            "combined passport/id card details present" in {
              navigator.nextPage(UkAddressPage, mode, combinedBaseAnswers)
                .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "Is address in UK page -> No -> Non-UK address page" in {
            val answers = baseAnswers
              .set(LiveInTheUkYesNoPage, false).success.value

            navigator.nextPage(LiveInTheUkYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.NonUkAddressController.onPageLoad(mode))
          }

          "Non-UK address page -> Do you know passport or ID card details page" must {
            "combined passport/id card details not present" in {
              navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
                .mustBe(controllers.individualbeneficiary.routes.PassportDetailsYesNoController.onPageLoad(mode))
            }

            "combined passport/id card details present" in {
              navigator.nextPage(NonUkAddressPage, mode, combinedBaseAnswers)
                .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
            }

            "no combined passport/id present but we do have the combined yes no page (the user has made edits to get to this)" in {
              val combinedYesNoBaseAnswers = baseAnswers.set(PassportOrIdCardDetailsYesNoPage, false).success.value
              navigator.nextPage(NonUkAddressPage, mode, combinedYesNoBaseAnswers)
                .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "Do you know passport or ID card details page -> Yes -> Check details page" in {
            val answers = baseAnswers
              .set(PassportOrIdCardDetailsYesNoPage, true).success.value

            navigator.nextPage(PassportOrIdCardDetailsYesNoPage, mode, answers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Passport or ID card details page -> Check details page" in {
            navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
              .mustBe(controllers.individualbeneficiary.routes.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Do you know passport or ID card details page -> No -> Check details page" in {
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
          val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

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
          val baseAnswers = emptyUserAnswers.copy(isTaxable = false).set(IndexPage, index).success.value

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
