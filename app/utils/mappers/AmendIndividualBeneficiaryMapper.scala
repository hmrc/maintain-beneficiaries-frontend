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

package utils.mappers

import java.time.LocalDate

import models.beneficiaries.IndividualBeneficiary
import models.{Address, CombinedPassportOrIdCard, IndividualIdentification, Name, NationalInsuranceNumber, NonUkAddress, UkAddress, UserAnswers}
import org.slf4j.LoggerFactory
import pages.individualbeneficiary._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

class AmendIndividualBeneficiaryMapper {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  def apply(answers: UserAnswers): Option[IndividualBeneficiary] = {

    val readFromUserAnswers: Reads[IndividualBeneficiary] =
      (
        NamePage.path.read[Name] and
          DateOfBirthPage.path.readNullable[LocalDate] and
          readIdentification and
          readAddress and
          VPE1FormYesNoPage.path.read[Boolean] and
          readIncome and
          IncomeDiscretionYesNoPage.path.read[Boolean] and
          StartDatePage.path.read[LocalDate]
        ) (
        (name, dateOfBirth, identification, address, vulnerableYesNo, income, incomeDiscretion, entityStart) =>
          IndividualBeneficiary(
            name,
            dateOfBirth,
            identification,
            address,
            vulnerableYesNo,
            income,
            incomeDiscretion,
            entityStart,
            provisional = true)
      )

    answers.data.validate[IndividualBeneficiary](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error(s"Failed to rehydrate IndividualBeneficiary from UserAnswers due to $errors")
        None
    }
  }

  private def readIdentification: Reads[Option[IndividualIdentification]] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap[Option[IndividualIdentification]] {
      case true => NationalInsuranceNumberPage.path.read[String].map(nino => Some(NationalInsuranceNumber(nino)))
      case false => readPassportOrIdCard
    }
  }

  private def readPassportOrIdCard: Reads[Option[IndividualIdentification]] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap {
      case true => Reads(_ => JsSuccess(None))
      case false => readPassportOrIdIfAddressExists
    }
  }

  private def readPassportOrIdIfAddressExists: Reads[Option[IndividualIdentification]] = {
    AddressYesNoPage.path.read[Boolean].flatMap {
      case true => PassportOrIdCardDetailsYesNoPage.path.read[Boolean].flatMap[Option[IndividualIdentification]] {
        case true => PassportOrIdCardDetailsPage.path.read[CombinedPassportOrIdCard].map(Some(_))
        case false => Reads(_ => JsSuccess(None))
      }
       case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readAddress: Reads[Option[Address]] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap {
      case true => Reads(_ => JsSuccess(None))
      case false => AddressYesNoPage.path.read[Boolean].flatMap[Option[Address]] {
        case true => readUkOrNonUkAddress
        case false => Reads(_ => JsSuccess(None))
      }
    }
  }

  private def readUkOrNonUkAddress: Reads[Option[Address]] = {
    LiveInTheUkYesNoPage.path.read[Boolean].flatMap[Option[Address]] {
      case true => UkAddressPage.path.read[UkAddress].map(Some(_))
      case false => NonUkAddressPage.path.read[NonUkAddress].map(Some(_))
    }
  }

  private def readIncome: Reads[Option[String]] = {
    IncomeDiscretionYesNoPage.path.read[Boolean].flatMap[Option[String]] {
      case true => Reads(_ => JsSuccess(None))
      case false => IncomePercentagePage.path.read[Int].map(value => Some(value.toString))
    }
  }
}
