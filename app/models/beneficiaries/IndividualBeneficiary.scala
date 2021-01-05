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

package models.beneficiaries

import java.time.LocalDate

import models.{Address, IndividualIdentification, Name}
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class IndividualBeneficiary(name: Name,
                                       dateOfBirth: Option[LocalDate],
                                       identification: Option[IndividualIdentification],
                                       address : Option[Address],
                                       vulnerableYesNo: Boolean,
                                       roleInCompany: Option[RoleInCompany],
                                       income: Option[String],
                                       incomeDiscretionYesNo: Boolean,
                                       entityStart: LocalDate,
                                       provisional : Boolean) extends Beneficiary

object IndividualBeneficiary {

  implicit val reads: Reads[IndividualBeneficiary] =
    ((__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).readNullable[LocalDate] and
      __.lazyRead(readNullableAtSubPath[IndividualIdentification](__ \ 'identification)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address)) and
      (__ \ 'vulnerableBeneficiary).read[Boolean] and
      (__ \ 'beneficiaryType).readNullable[RoleInCompany] and
      (__ \ 'beneficiaryShareOfIncome).readNullable[String] and
      (__ \ 'beneficiaryDiscretion).readNullable[Boolean] and
      (__ \ "entityStart").read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)).tupled.map{

      case (name, dob, nino, identification, vulnerable, employment, None, _, entityStart, provisional) =>
        IndividualBeneficiary(name, dob, nino, identification, vulnerable, employment, None, incomeDiscretionYesNo = true, entityStart, provisional)
      case (name, dob, nino, identification, vulnerable, employment, _, Some(true), entityStart, provisional) =>
        IndividualBeneficiary(name, dob, nino, identification, vulnerable, employment, None, incomeDiscretionYesNo = true, entityStart, provisional)
      case (name, dob, nino, identification, vulnerable,  employment, income, _, entityStart, provisional) =>
        IndividualBeneficiary(name, dob, nino, identification, vulnerable, employment, income, incomeDiscretionYesNo = false, entityStart, provisional)

    }

  implicit val writes: Writes[IndividualBeneficiary] =
    ((__ \ 'name).write[Name] and
      (__ \ 'dateOfBirth).writeNullable[LocalDate] and
      (__ \ 'identification).writeNullable[IndividualIdentification] and
      (__ \ 'identification \ 'address).writeNullable[Address] and
      (__ \ 'vulnerableBeneficiary).write[Boolean] and
      (__ \ 'beneficiaryType).writeNullable[RoleInCompany] and
      (__ \ 'beneficiaryShareOfIncome).writeNullable[String] and
      (__ \ 'beneficiaryDiscretion).write[Boolean] and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    ).apply(unlift(IndividualBeneficiary.unapply))

  def readNullableAtSubPath[T:Reads](subPath : JsPath) : Reads[Option[T]] = Reads (
    _.transform(subPath.json.pick)
      .flatMap(_.validate[T])
      .map(Some(_))
      .recoverWith(_ => JsSuccess(None))
  )
}