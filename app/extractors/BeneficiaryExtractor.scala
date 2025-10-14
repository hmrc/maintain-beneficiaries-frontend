/*
 * Copyright 2025 HM Revenue & Customs
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

import models.beneficiaries.{Beneficiary, IncomeBeneficiary, OrgBeneficiary}
import models.{Address, NonUkAddress, UkAddress, UserAnswers}
import pages.{EmptyPage, QuestionPage}
import play.api.libs.json.JsPath
import utils.Constants.GB

import java.time.LocalDate
import scala.util.{Success, Try}

trait BeneficiaryExtractor[T <: Beneficiary] {

  def apply(answers: UserAnswers, beneficiary: T, index: Int): Try[UserAnswers] = {
    answers.deleteAtPath(basePath)
      .flatMap(_.set(startDatePage, beneficiary.entityStart))
      .flatMap(_.set(indexPage, index))
  }

  def namePage: QuestionPage[String] = new EmptyPage[String]

  def utrPage: QuestionPage[String] = new EmptyPage[String]

  def shareOfIncomeYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def shareOfIncomePage: QuestionPage[Int] = new EmptyPage[Int]

  def countryOfResidenceYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def countryOfResidencePage: QuestionPage[String] = new EmptyPage[String]

  def addressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressPage: QuestionPage[UkAddress] = new EmptyPage[UkAddress]
  def nonUkAddressPage: QuestionPage[NonUkAddress] = new EmptyPage[NonUkAddress]

  def startDatePage: QuestionPage[LocalDate] = new EmptyPage[LocalDate]

  def indexPage: QuestionPage[Int] = new EmptyPage[Int]

  def basePath: JsPath

  def extractUserAnswersForOrgBeneficiary(answers: UserAnswers,
                                          entity: OrgBeneficiary): Try[UserAnswers] = {
    answers
      .set(namePage, entity.name)
      .flatMap(_.set(utrPage, entity.utr))
      .flatMap(answers => extractUserAnswersForIncomeBeneficiary(answers, entity))
  }

  def extractUserAnswersForIncomeBeneficiary(answers: UserAnswers,
                                             entity: IncomeBeneficiary): Try[UserAnswers] = {
    extractShareOfIncome(entity.incomeDiscretionYesNo, entity.income, answers)
      .flatMap(answers => extractCountryOfResidence(entity.countryOfResidence, answers))
      .flatMap(answers => extractAddress(entity.address, answers))
  }

  def extractShareOfIncome(hasDiscretion: Option[Boolean], shareOfIncome: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    if (answers.migratingFromNonTaxableToTaxable) {
      (hasDiscretion, shareOfIncome) match {
        case (Some(true), None) => answers
          .set(shareOfIncomeYesNoPage, true)
        case (_, Some(income)) => answers
          .set(shareOfIncomeYesNoPage, false)
          .flatMap(_.set(shareOfIncomePage, income.toInt))
        case _ => Success(answers)
      }
    } else if (answers.isTaxable) {
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

  def extractCountryOfResidence(countryOfResidence: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    extractCountryOfResidenceOrNationality(
      country = countryOfResidence,
      answers = answers,
      yesNoPage = countryOfResidenceYesNoPage,
      ukYesNoPage = ukCountryOfResidenceYesNoPage,
      page = countryOfResidencePage
    )
  }

  def extractCountryOfResidenceOrNationality(country: Option[String],
                                             answers: UserAnswers,
                                             yesNoPage: QuestionPage[Boolean],
                                             ukYesNoPage: QuestionPage[Boolean],
                                             page: QuestionPage[String]): Try[UserAnswers] = {
    if (answers.isUnderlyingData5mld) {
      country match {
        case Some(GB) =>
          answers.set(yesNoPage, true)
            .flatMap(_.set(ukYesNoPage, true))
            .flatMap(_.set(page, GB))
        case Some(country) =>
          answers.set(yesNoPage, true)
            .flatMap(_.set(ukYesNoPage, false))
            .flatMap(_.set(page, country))
        case None =>
          answers.set(yesNoPage, false)
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

}
