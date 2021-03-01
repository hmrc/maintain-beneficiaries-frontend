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

import models.beneficiaries.{Beneficiary, OrgBeneficiary}
import models.{Address, NonUkAddress, UkAddress, UserAnswers}
import pages.{EmptyPage, QuestionPage}
import play.api.libs.json.JsPath

import java.time.LocalDate
import scala.util.Try

trait BeneficiaryExtractor[T <: Beneficiary] {

  def apply(userAnswers: UserAnswers, beneficiary: T, index: Int): Try[UserAnswers]

  def namePage: QuestionPage[String] = new EmptyPage[String]

  def utrPage: QuestionPage[String] = new EmptyPage[String]

  def shareOfIncomeYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def shareOfIncomePage: QuestionPage[Int] = new EmptyPage[Int]

  def addressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressYesNoPage: QuestionPage[Boolean] = new EmptyPage[Boolean]
  def ukAddressPage: QuestionPage[UkAddress] = new EmptyPage[UkAddress]
  def nonUkAddressPage: QuestionPage[NonUkAddress] = new EmptyPage[NonUkAddress]

  def startDatePage: QuestionPage[LocalDate] = new EmptyPage[LocalDate]

  def indexPage: QuestionPage[Int] = new EmptyPage[Int]

  def basePath: JsPath

  def extractShareOfIncome(shareOfIncome: Option[String], answers: UserAnswers): Try[UserAnswers] = {
    shareOfIncome match {
      case Some(income) => answers
        .set(shareOfIncomeYesNoPage, false)
        .flatMap(_.set(shareOfIncomePage, income.toInt))
      case None => answers
        .set(shareOfIncomeYesNoPage, true)
    }
  }

  def extractUserAnswersForOrgBeneficiary(answers: UserAnswers,
                                          entity: OrgBeneficiary,
                                          index: Int): Try[UserAnswers] = {
    answers.deleteAtPath(basePath)
      .flatMap(_.set(namePage, entity.name))
      .flatMap(answers => extractAddress(entity.address, answers))
      .flatMap(answers => extractShareOfIncome(entity.income, answers))
      .flatMap(_.set(utrPage, entity.utr))
      .flatMap(_.set(startDatePage, entity.entityStart))
      .flatMap(_.set(indexPage, index))
  }

  def extractAddress(address: Option[Address], answers: UserAnswers): Try[UserAnswers] = {
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
  }

}
