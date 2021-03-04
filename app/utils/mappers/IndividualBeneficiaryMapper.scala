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

package utils.mappers

import models._
import models.Constant.GB
import models.beneficiaries.{IndividualBeneficiary, RoleInCompany}
import pages.individualbeneficiary._
import pages.individualbeneficiary.add._
import pages.individualbeneficiary.amend.{PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsSuccess, Reads}

import java.time.LocalDate

class IndividualBeneficiaryMapper extends Mapper[IndividualBeneficiary]  {

  def apply(answers: UserAnswers, provisional: Boolean): Option[IndividualBeneficiary] = {

    val readFromUserAnswers: Reads[IndividualBeneficiary] =
      (
        NamePage.path.read[Name] and
        DateOfBirthPage.path.readNullable[LocalDate] and
        readIdentification(provisional) and
        readAddress and
        VPE1FormYesNoPage.path.readNullable[Boolean] and
        RoleInCompanyPage.path.readNullable[RoleInCompany] and
        readIncome and
        IncomeDiscretionYesNoPage.path.readNullable[Boolean] and
        CountryOfResidenceYesNoPage.path.readNullable[Boolean].flatMap[Option[String]] {
          case Some(true) => CountryOfResidenceUkYesNoPage.path.read[Boolean].flatMap {
            case true => Reads(_ => JsSuccess(Some(GB)))
            case false => CountryOfResidencePage.path.read[String].map(Some(_))
          }
          case _ => Reads(_ => JsSuccess(None))
        } and
        CountryOfNationalityYesNoPage.path.readNullable[Boolean].flatMap[Option[String]] {
          case Some(true) => CountryOfNationalityUkYesNoPage.path.read[Boolean].flatMap {
            case true => Reads(_ => JsSuccess(Some(GB)))
            case false => CountryOfNationalityPage.path.read[String].map(Some(_))
          }
          case _ => Reads(_ => JsSuccess(None))
        } and
        LegallyIncapableYesNoPage.path.readNullable[Boolean].flatMap[Option[Boolean]] {
          case Some(true) => Reads(_ => JsSuccess(Some(false)))
          case Some(false) => Reads(_ => JsSuccess(Some(true)))
          case _ => Reads(_ => JsSuccess(None))
        } and
        StartDatePage.path.read[LocalDate] and
        Reads(_ => JsSuccess(true))
      ) (IndividualBeneficiary.apply _)

    mapAnswersWithExplicitReads(answers, readFromUserAnswers)
  }

  private def readIdentification(provisional: Boolean): Reads[Option[IndividualIdentification]] = {
    NationalInsuranceNumberYesNoPage.path.readNullable[Boolean].flatMap[Option[IndividualIdentification]] {
      case Some(true) => NationalInsuranceNumberPage.path.read[String].map(nino => Some(NationalInsuranceNumber(nino)))
      case Some(false) => if (provisional) readSeparatePassportOrIdCard else readCombinedPassportOrIdCard
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readSeparatePassportOrIdCard: Reads[Option[IndividualIdentification]] = {
    (for {
      hasNino <- NationalInsuranceNumberYesNoPage.path.readWithDefault(false)
      hasAddress <- AddressYesNoPage.path.readWithDefault(false)
      hasPassport <- PassportDetailsYesNoPage.path.readWithDefault(false)
      hasIdCard <- IdCardDetailsYesNoPage.path.readWithDefault(false)
    } yield (hasNino, hasAddress, hasPassport, hasIdCard)).flatMap[Option[IndividualIdentification]] {
      case (false, true, true, false) => PassportDetailsPage.path.read[Passport].map(Some(_))
      case (false, true, false, true) => IdCardDetailsPage.path.read[IdCard].map(Some(_))
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readCombinedPassportOrIdCard: Reads[Option[IndividualIdentification]] = {
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
    NationalInsuranceNumberYesNoPage.path.readNullable[Boolean].flatMap {
      case Some(true) => Reads(_ => JsSuccess(None))
      case Some(false) => AddressYesNoPage.path.read[Boolean].flatMap[Option[Address]] {
        case true => readUkOrNonUkAddress
        case false => Reads(_ => JsSuccess(None))
      }
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  private def readUkOrNonUkAddress: Reads[Option[Address]] = {
    LiveInTheUkYesNoPage.path.read[Boolean].flatMap[Option[Address]] {
      case true => UkAddressPage.path.read[UkAddress].map(Some(_))
      case false => NonUkAddressPage.path.read[NonUkAddress].map(Some(_))
    }
  }

  private def readIncome: Reads[Option[String]] = {
    IncomeDiscretionYesNoPage.path.readNullable[Boolean].flatMap[Option[String]] {
      case Some(true) => Reads(_ => JsSuccess(None))
      case Some(false) => IncomePercentagePage.path.read[Int].map(value => Some(value.toString))
      case _ => Reads(_ => JsSuccess(None))
    }
  }
}
