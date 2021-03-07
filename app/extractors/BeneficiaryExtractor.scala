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

import utils.Constants.GB
import models.beneficiaries.{Beneficiary, IndBeneficiary, OrgBeneficiary, RoleInCompany}
import models.{Address, CombinedPassportOrIdCard, IdCard, IndividualIdentification, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress, UserAnswers}
import pages.{EmptyPage, QuestionPage}
import play.api.libs.json.JsPath
import java.time.LocalDate

import scala.util.{Success, Try}

trait BeneficiaryExtractor[T <: Beneficiary] {

  def apply(answers: UserAnswers, beneficiary: T, index: Int): Try[UserAnswers] = {
    answers.deleteAtPath(basePath)
      .flatMap(_.set(startDatePage, beneficiary.entityStart))
      .flatMap(_.set(indexPage, index))
  }

  def namePage: QuestionPage[String] = new EmptyPage[String]

  def fullNamePage: QuestionPage[Name] = new EmptyPage[Name]
  def dateOfBirthYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def dateOfBirthPage: QuestionPage[LocalDate] = new EmptyPage[LocalDate]

  def utrPage: QuestionPage[String] = new EmptyPage[String]

  def roleInCompanyPage: QuestionPage[RoleInCompany] = new EmptyPage[RoleInCompany]

  def shareOfIncomeYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def shareOfIncomePage: QuestionPage[Int] = new EmptyPage[Int]

  def countryOfNationalityYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukCountryOfNationalityYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def countryOfNationalityPage: QuestionPage[String] = new EmptyPage[String]

  def nationalInsuranceNumberYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def nationalInsuranceNumberPage: QuestionPage[String] = new EmptyPage[String]

  def passportOrIdCardDetailsYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def passportOrIdCardDetailsPage: QuestionPage[CombinedPassportOrIdCard] = new EmptyPage[CombinedPassportOrIdCard]

  def countryOfResidenceYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def countryOfResidencePage: QuestionPage[String] = new EmptyPage[String]

  def addressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressPage: QuestionPage[UkAddress] = new EmptyPage[UkAddress]
  def nonUkAddressPage: QuestionPage[NonUkAddress] = new EmptyPage[NonUkAddress]

  def mentalCapacityYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def vulnerableYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]

  def startDatePage: QuestionPage[LocalDate] = new EmptyPage[LocalDate]

  def indexPage: QuestionPage[Int] = new EmptyPage[Int]

  def basePath: JsPath

  def extractUserAnswersForOrgBeneficiary(answers: UserAnswers,
                                          entity: OrgBeneficiary): Try[UserAnswers] = {
    answers
      .set(namePage, entity.name)
      .flatMap(_.set(utrPage, entity.utr))
      .flatMap(answers => extractShareOfIncome(entity.income, answers))
      .flatMap(answers => extractCountryOfResidence(entity.countryOfResidence, answers))
      .flatMap(answers => extractAddress(entity.address, answers))
  }

  def extractUserAnswersForIndBeneficiary(answers: UserAnswers,
                                          entity: IndBeneficiary): Try[UserAnswers] = {
    answers
      .set(fullNamePage, entity.name)
      .flatMap(_.set(roleInCompanyPage, entity.roleInCompany))
      .flatMap(_.set(mentalCapacityYesNoPage, entity.mentalCapacityYesNo))
      .flatMap(_.set(vulnerableYesNoPage, entity.vulnerableYesNo))
      .flatMap(answers => extractDateOfBirth(entity.dateOfBirth, answers))
      .flatMap(answers => extractShareOfIncome(entity.income, answers))
      .flatMap(answers => extractCountryOfNationality(entity.nationality, answers))
      .flatMap(answers => extractIdentification(entity.identification, entity.address, answers))
      .flatMap(answers => extractCountryOfResidence(entity.countryOfResidence, answers))
      .flatMap(answers => extractAddress(entity.address, answers))
  }

  def extractShareOfIncome(shareOfIncome: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable) {
      shareOfIncome match {
        case Some(income) => answers
          .set(shareOfIncomeYesNoPage, false)
          .flatMap(_.set(shareOfIncomePage, income.toInt))
        case None => answers
          .set(shareOfIncomeYesNoPage, true)
      }
    } else {
      Success(answers)
    }
  }

  def extractDateOfBirth(dateOfBirth: Option[LocalDate], answers: UserAnswers) : Try[UserAnswers] = {
    dateOfBirth match {
      case Some(dob) =>
        answers.set(dateOfBirthYesNoPage, true)
          .flatMap(_.set(dateOfBirthPage, dob))
      case None =>
        // Assumption that user answered no as dob is not provided
        answers.set(dateOfBirthYesNoPage, false)
    }
  }

  def extractCountryOfNationality(countryOfNationality: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      countryOfNationality match {
        case Some(GB) => answers
          .set(countryOfNationalityYesNoPage, true)
          .flatMap(_.set(ukCountryOfNationalityYesNoPage, true))
          .flatMap(_.set(countryOfNationalityPage, GB))
        case Some(country) => answers
          .set(countryOfNationalityYesNoPage, true)
          .flatMap(_.set(ukCountryOfNationalityYesNoPage, false))
          .flatMap(_.set(countryOfNationalityPage, country))
        case None => answers
          .set(countryOfNationalityYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  def extractCountryOfResidence(countryOfResidence: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.is5mldEnabled && answers.isUnderlyingData5mld) {
      countryOfResidence match {
        case Some(GB) => answers
          .set(countryOfResidenceYesNoPage, true)
          .flatMap(_.set(ukCountryOfResidenceYesNoPage, true))
          .flatMap(_.set(countryOfResidencePage, GB))
        case Some(country) => answers
          .set(countryOfResidenceYesNoPage, true)
          .flatMap(_.set(ukCountryOfResidenceYesNoPage, false))
          .flatMap(_.set(countryOfResidencePage, country))
        case None => answers
          .set(countryOfResidenceYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  def extractAddress(address: Option[Address], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable) {
      address match {
        case Some(uk: UkAddress) => answers
          .set(addressYesNoPage, true)
          .flatMap(_.set(ukAddressYesNoPage, true))
          .flatMap(_.set(ukAddressPage, uk))
        case Some(nonUk: NonUkAddress) => answers
          .set(addressYesNoPage, true)
          .flatMap(_.set(ukAddressYesNoPage, false))
          .flatMap(_.set(nonUkAddressPage, nonUk))
        case _ => answers
          .set(addressYesNoPage, false)
      }
    } else {
      Success(answers)
    }
  }

  def extractIdentification(identification: Option[IndividualIdentification],
                            address: Option[Address],
                            answers: UserAnswers) : Try[UserAnswers] = {
    if (answers.isTaxable) {
      identification match {
        case Some(NationalInsuranceNumber(nino)) =>
          answers.set(nationalInsuranceNumberYesNoPage, true)
            .flatMap(_.set(nationalInsuranceNumberPage, nino))
        case Some(p: Passport) =>
          answers.set(nationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(passportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(passportOrIdCardDetailsPage, p.asCombined))
        case Some(id: IdCard) =>
          answers.set(nationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(passportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(passportOrIdCardDetailsPage, id.asCombined))
        case Some(combined: CombinedPassportOrIdCard) =>
          answers.set(nationalInsuranceNumberYesNoPage, false)
            .flatMap(_.set(passportOrIdCardDetailsYesNoPage, true))
            .flatMap(_.set(passportOrIdCardDetailsPage, combined))
        case _ =>
          answers.set(nationalInsuranceNumberYesNoPage, false)
            .flatMap(answers => extractPassportOrIdCardDetailsYesNo(address.isDefined, answers))
      }
    } else {
      Success(answers)
    }
  }

  def extractPassportOrIdCardDetailsYesNo(hasAddress: Boolean, answers: UserAnswers): Try[UserAnswers] = {
    if (answers.isTaxable && hasAddress) {
      answers.set(passportOrIdCardDetailsYesNoPage, false)
    } else {
      Success(answers)
    }
  }


}
