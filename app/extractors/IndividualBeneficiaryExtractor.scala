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
import models.beneficiaries.IndividualBeneficiary
import models.{Address, CombinedPassportOrIdCard, IdCard, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress, UserAnswers}

import scala.util.Try

class IndividualBeneficiaryExtractor @Inject()() {

  import pages.individualbeneficiary._

  def apply(answers: UserAnswers,
            individual : IndividualBeneficiary,
            index: Int): Try[UserAnswers] =
  {

    answers.deleteAtPath(pages.individualbeneficiary.basePath)
              .flatMap(_.set(NamePage, individual.name))
              .flatMap(answers => extractDateOfBirth(individual, answers))
              .flatMap(answers => extractShareOfIncome(individual, answers))
              .flatMap(answers => extractAddress(individual.address, answers))
              .flatMap(answers => extractIdentification(individual, answers))
              .flatMap(_.set(VPE1FormYesNoPage, individual.vulnerableYesNo))
              .flatMap(_.set(StartDatePage, individual.entityStart))
              .flatMap(_.set(IndexPage, index))
    }

  private def extractIdentification(individualBeneficiary: IndividualBeneficiary,
                                    answers: UserAnswers) : Try[UserAnswers] =
  {
    individualBeneficiary.identification match {
      case Some(NationalInsuranceNumber(nino)) =>
        answers.set(NationalInsuranceNumberYesNoPage, true)
          .flatMap(_.set(NationalInsuranceNumberPage, nino))
      case Some(p : Passport) =>
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
    }
  }


  private def extractDateOfBirth(individualBeneficiary: IndividualBeneficiary,
                                 answers: UserAnswers) : Try[UserAnswers] =
  {
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
    individualBeneficiary.income match {
      case Some(income) =>
        answers.set(IncomeDiscretionYesNoPage, false)
          // TODO come back and fix this toInt to handle at API reads
          .flatMap(_.set(IncomePercentagePage, income.toInt))
      case None =>
        // Assumption that user answered yes as the share of income is not provided
        answers.set(IncomeDiscretionYesNoPage, true)
    }
  }

  private def extractAddress(address: Option[Address],
                             answers: UserAnswers) : Try[UserAnswers] =
  {
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
  }

}
