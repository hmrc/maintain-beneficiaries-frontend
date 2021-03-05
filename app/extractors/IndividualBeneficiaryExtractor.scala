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

package extractors

import com.google.inject.Inject
import models.Constant.GB
import models.beneficiaries.IndividualBeneficiary
import models.{Address, CombinedPassportOrIdCard, IdCard, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress, UserAnswers}
import pages.individualbeneficiary.add.StartDatePage
import pages.individualbeneficiary.amend.{IndexPage, PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}

import scala.util.{Success, Try}

class IndividualBeneficiaryExtractor @Inject()() {

  import pages.individualbeneficiary._

  def apply(answers: UserAnswers,
            individual : IndividualBeneficiary,
            index: Int): Try[UserAnswers] = {

    answers.deleteAtPath(pages.individualbeneficiary.basePath)
      .flatMap(_.set(RoleInCompanyPage, individual.roleInCompany))
      .flatMap(_.set(NamePage, individual.name))
      .flatMap(answers => extractDateOfBirth(individual, answers))
      .flatMap(answers => extractShareOfIncome(individual, answers))
      .flatMap(answers => extractCountryOfNationality(individual, answers))
      .flatMap(answers => extractCountryOfResidence(individual, answers))
      .flatMap(answers => extractAddress(individual.address, answers))
      .flatMap(answers => extractIdentification(individual, answers))
      .flatMap(_.set(MentalCapacityYesNoPage, individual.mentalCapacityYesNo))
      .flatMap(_.set(VPE1FormYesNoPage, individual.vulnerableYesNo))
      .flatMap(_.set(StartDatePage, individual.entityStart))
      .flatMap(_.set(IndexPage, index))
  }


  def extractCountryOfNationality(individualBeneficiary: IndividualBeneficiary,
                                answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      individualBeneficiary.nationality match {
        case Some(GB) => answers
          .set(CountryOfNationalityYesNoPage, true)
          .flatMap(_.set(CountryOfNationalityUkYesNoPage, true))
          .flatMap(_.set(CountryOfNationalityPage, GB))
        case Some(country) => answers
          .set(CountryOfNationalityYesNoPage, true)
          .flatMap(_.set(CountryOfNationalityUkYesNoPage, false))
          .flatMap(_.set(CountryOfNationalityPage, country))
        case None => answers
          .set(CountryOfNationalityYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  def extractCountryOfResidence(individualBeneficiary: IndividualBeneficiary,
                                answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      individualBeneficiary.countryOfResidence match {
        case Some(GB) => answers
          .set(CountryOfResidenceYesNoPage, true)
          .flatMap(_.set(CountryOfResidenceUkYesNoPage, true))
          .flatMap(_.set(CountryOfResidencePage, GB))
        case Some(country) => answers
          .set(CountryOfResidenceYesNoPage, true)
          .flatMap(_.set(CountryOfResidenceUkYesNoPage, false))
          .flatMap(_.set(CountryOfResidencePage, country))
        case None => answers
          .set(CountryOfResidenceYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  private def extractIdentification(individualBeneficiary: IndividualBeneficiary,
                                    answers: UserAnswers) : Try[UserAnswers] = {
    if (answers.isTaxable) {
      individualBeneficiary.identification match {
        case Some(NationalInsuranceNumber(nino)) =>
          answers.set(NationalInsuranceNumberYesNoPage, true)
            .flatMap(_.set(NationalInsuranceNumberPage, nino))
        case Some(p: Passport) =>
          answers.set(NationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(PassportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(PassportOrIdCardDetailsPage, p.asCombined))
        case Some(id: IdCard) =>
          answers.set(NationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(PassportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(PassportOrIdCardDetailsPage, id.asCombined))
        case Some(combined: CombinedPassportOrIdCard) =>
          answers.set(NationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(PassportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(PassportOrIdCardDetailsPage, combined))
        case _ =>
          answers.set(NationalInsuranceNumberYesNoPage, false)
            .flatMap(answers => extractPassportOrIdCardDetailsYesNo(individualBeneficiary.address.isDefined, answers))
      }
    } else {
      Success(answers)
    }
  }

  private def extractDateOfBirth(individualBeneficiary: IndividualBeneficiary,
                                 answers: UserAnswers) : Try[UserAnswers] = {
    individualBeneficiary.dateOfBirth match {
      case Some(dob) =>
        answers.set(DateOfBirthYesNoPage, true)
          .flatMap(_.set(DateOfBirthPage, dob))
      case None =>
        // Assumption that user answered no as dob is not provided
        answers.set(DateOfBirthYesNoPage, false)
    }
  }

  private def extractShareOfIncome(individualBeneficiary: IndividualBeneficiary,
                                   answers: UserAnswers) : Try[UserAnswers] = {
    if (answers.isTaxable) {
      individualBeneficiary.income match {
        case Some(income) =>
          answers.set(IncomeDiscretionYesNoPage, false)
            .flatMap(_.set(IncomePercentagePage, income.toInt))
        case None =>
          // Assumption that user answered yes as the share of income is not provided
          answers.set(IncomeDiscretionYesNoPage, true)
      }
    } else {
      Success(answers)
    }
  }

  private def extractAddress(address: Option[Address],
                             answers: UserAnswers) : Try[UserAnswers] = {
    if (answers.isTaxable) {
      address match {
        case Some(uk: UkAddress) =>
          answers.set(AddressYesNoPage, true)
            .flatMap(_.set(LiveInTheUkYesNoPage, true))
            .flatMap(_.set(UkAddressPage, uk))
        case Some(nonUk: NonUkAddress) =>
          answers.set(AddressYesNoPage, true)
            .flatMap(_.set(LiveInTheUkYesNoPage, false))
            .flatMap(_.set(NonUkAddressPage, nonUk))
        case _ =>
          answers.set(AddressYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  private def extractPassportOrIdCardDetailsYesNo(hasAddress: Boolean, answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable && hasAddress) {
      answers.set(PassportOrIdCardDetailsYesNoPage, false)
    } else {
      Success(answers)
    }
  }

}

