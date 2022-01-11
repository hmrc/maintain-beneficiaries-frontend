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

package models.beneficiaries

import models.TypeOfTrust.EmployeeRelated
import models.YesNoDontKnow.{No, Yes}

import java.time.LocalDate
import models.{Address, IndividualIdentification, Name, TypeOfTrust, YesNoDontKnow}
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class IndividualBeneficiary(name: Name,
                                       dateOfBirth: Option[LocalDate],
                                       identification: Option[IndividualIdentification],
                                       address: Option[Address],
                                       vulnerableYesNo: Option[Boolean] = None,
                                       roleInCompany: Option[RoleInCompany],
                                       income: Option[String],
                                       incomeDiscretionYesNo: Option[Boolean] = None,
                                       countryOfResidence: Option[String] = None,
                                       nationality: Option[String] = None,
                                       mentalCapacityYesNo: Option[YesNoDontKnow] = None,
                                       entityStart: LocalDate,
                                       provisional: Boolean) extends IncomeBeneficiary {

  override def hasRequiredData(migratingFromNonTaxableToTaxable: Boolean, trustType: Option[TypeOfTrust]): Boolean = {
    if (migratingFromNonTaxableToTaxable) {
      super.hasRequiredData(migratingFromNonTaxableToTaxable, trustType) &&
        vulnerableYesNo.isDefined &&
        (trustType match {
          case Some(EmployeeRelated) => roleInCompany.isDefined
          case _ => true
        })
    } else {
      true
    }
  }
}

object IndividualBeneficiary extends BeneficiaryReads {

  def readMentalCapacity: Reads[Option[YesNoDontKnow]] =
    (__ \ 'legallyIncapable).readNullable[Boolean].flatMap[Option[YesNoDontKnow]] { x: Option[Boolean] =>
      Reads(_ => JsSuccess(YesNoDontKnow.fromBoolean(x)))
    }

  def legallyIncapableWrites: Writes[YesNoDontKnow] = new Writes[YesNoDontKnow] {
    override def writes(o: YesNoDontKnow): JsValue = o match {
      case Yes => JsBoolean(false)
      case No => JsBoolean(true)
      case _ => JsNull
    }
  }

  implicit val reads: Reads[IndividualBeneficiary] = (
    (__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).readNullable[LocalDate] and
      __.lazyRead(readNullableAtSubPath[IndividualIdentification](__ \ 'identification)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address)) and
      (__ \ 'vulnerableBeneficiary).readNullable[Boolean] and
      (__ \ 'beneficiaryType).readNullable[RoleInCompany] and
      (__ \ 'beneficiaryShareOfIncome).readNullable[String] and
      (__ \ 'beneficiaryDiscretion).readNullable[Boolean] and
      (__ \ 'countryOfResidence).readNullable[String] and
      (__ \ 'nationality).readNullable[String] and
      readMentalCapacity and
      (__ \ "entityStart").read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)
    ).tupled.map{
    case (name, dob, None, None, None, None, None, None, country, nationality, mentalCapacity, entityStart, provisional) =>
      IndividualBeneficiary(name, dob, None, None, None, None, None, None, country, nationality, mentalCapacity, entityStart, provisional)
    case (name, dob, nino, identification, vulnerable, employment, None, _, country, nationality, mentalCapacity, entityStart, provisional) =>
      IndividualBeneficiary(name, dob, nino, identification, vulnerable, employment, None, incomeDiscretionYesNo = Some(true), country, nationality, mentalCapacity, entityStart, provisional)
    case (name, dob, nino, identification, vulnerable, employment, _, Some(true), country, nationality, mentalCapacity, entityStart, provisional) =>
      IndividualBeneficiary(name, dob, nino, identification, vulnerable, employment, None, incomeDiscretionYesNo = Some(true), country, nationality, mentalCapacity, entityStart, provisional)
    case (name, dob, nino, identification, vulnerable,  employment, income, _, country, nationality, mentalCapacity, entityStart, provisional) =>
      IndividualBeneficiary(name, dob, nino, identification, vulnerable, employment, income, incomeDiscretionYesNo = Some(false), country, nationality, mentalCapacity, entityStart, provisional)
  }

  implicit val writes: Writes[IndividualBeneficiary] = (
    (__ \ 'name).write[Name] and
      (__ \ 'dateOfBirth).writeNullable[LocalDate] and
      (__ \ 'identification).writeNullable[IndividualIdentification] and
      (__ \ 'identification \ 'address).writeNullable[Address] and
      (__ \ 'vulnerableBeneficiary).writeNullable[Boolean] and
      (__ \ 'beneficiaryType).writeNullable[RoleInCompany] and
      (__ \ 'beneficiaryShareOfIncome).writeNullable[String] and
      (__ \ 'beneficiaryDiscretion).writeNullable[Boolean] and
      (__ \ 'countryOfResidence).writeNullable[String] and
      (__ \ 'nationality).writeNullable[String] and
      (__ \ 'legallyIncapable).writeNullable[YesNoDontKnow](legallyIncapableWrites) and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    ).apply(unlift(IndividualBeneficiary.unapply))

}
