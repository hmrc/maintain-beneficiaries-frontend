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
import models.HowManyBeneficiaries.{Over1, Over1001, Over101, Over201, Over501}
import models.beneficiaries.EmploymentRelatedBeneficiary
import models.{Address, NonUkAddress, UkAddress, UserAnswers}
import pages.companyoremploymentrelated.employment._

import scala.util.Try

class EmploymentRelatedBeneficiaryExtractor @Inject()() {

  def apply(answers: UserAnswers, employmentRelatedBeneficiary : EmploymentRelatedBeneficiary, index: Int): Try[UserAnswers] = {
    answers
      .deleteAtPath(pages.companyoremploymentrelated.company.basePath)
      .flatMap(_.set(NamePage, employmentRelatedBeneficiary.name))
      .flatMap(_.set(UtrPage, employmentRelatedBeneficiary.utr))
      .flatMap(answers => extractAddress(employmentRelatedBeneficiary.address, answers))
      .flatMap(_.set(DescriptionPage, employmentRelatedBeneficiary.description))
      .flatMap(answers => extracthowManyBeneficiaries(employmentRelatedBeneficiary.howManyBeneficiaries, answers))
      .flatMap(_.set(StartDatePage, employmentRelatedBeneficiary.entityStart))
      .flatMap(_.set(IndexPage, index))
  }

  private def extracthowManyBeneficiaries(howManyBeneficiaries: String, answers: UserAnswers) : Try[UserAnswers] = {
    howManyBeneficiaries match {
      case "over1"   => answers.set(NumberOfBeneficiariesPage, Over1)
      case "over101" => answers.set(NumberOfBeneficiariesPage, Over101)
      case "over201" => answers.set(NumberOfBeneficiariesPage, Over201)
      case "over501" => answers.set(NumberOfBeneficiariesPage, Over501)
      case _         => answers.set(NumberOfBeneficiariesPage, Over1001)
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
