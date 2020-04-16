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

package extractors

import com.google.inject.Inject
import models.beneficiaries.CompanyBeneficiary
import models.{Address, NonUkAddress, UkAddress, UserAnswers}
import pages.charityortrust.charity.UtrPage
import pages.companyoremploymentrelated.company._

import scala.util.Try

class CompanyBeneficiaryExtractor @Inject()() {

  def apply(answers: UserAnswers, companyBeneficiary : CompanyBeneficiary, index: Int): Try[UserAnswers] = {
    answers
      .deleteAtPath(pages.companyoremploymentrelated.company.basePath)
      .flatMap(_.set(NamePage, companyBeneficiary.name))
      .flatMap(answers => extractAddress(companyBeneficiary.address, answers))
      .flatMap(answers => extractShareOfIncome(companyBeneficiary.income, answers))
      .flatMap(_.set(UtrPage, companyBeneficiary.utr))
      .flatMap(_.set(StartDatePage, companyBeneficiary.entityStart))
      .flatMap(_.set(IndexPage, index))
  }

  private def extractShareOfIncome(income: Option[String], answers: UserAnswers) : Try[UserAnswers] = {
    income match {
      case Some(income) =>
        answers.set(DiscretionYesNoPage, false)
          .flatMap(_.set(ShareOfIncomePage, income.toInt))
      case None =>
        answers.set(DiscretionYesNoPage, true)
    }
  }

  private def extractAddress(address: Option[Address], answers: UserAnswers) : Try[UserAnswers] = {
    address match {
      case Some(uk: UkAddress) =>
        answers.set(AddressYesNoPage, true)
          .flatMap(_.set(AddressUkYesNoPage, true))
          .flatMap(_.set(UkAddressPage, uk))
      case Some(nonUk: NonUkAddress) =>
        answers.set(AddressYesNoPage, true)
          .flatMap(_.set(AddressUkYesNoPage, false))
          .flatMap(_.set(NonUkAddressPage, nonUk))
      case _ =>
        answers.set(AddressYesNoPage, false)
    }
  }
}
