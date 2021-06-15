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

import models.Address
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class CompanyBeneficiary(name: String,
                              utr: Option[String],
                              address: Option[Address],
                              income: Option[String],
                              incomeDiscretionYesNo: Option[Boolean],
                              countryOfResidence: Option[String] = None,
                              entityStart: LocalDate,
                              provisional: Boolean) extends OrgBeneficiary

object CompanyBeneficiary extends BeneficiaryReads {

  implicit val reads: Reads[CompanyBeneficiary] = (
    (__ \ 'organisationName).read[String] and
      __.lazyRead(readNullableAtSubPath[String](__ \ 'identification \ 'utr)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address)) and
      (__ \ 'beneficiaryShareOfIncome).readNullable[String] and
      (__ \ 'beneficiaryDiscretion).readNullable[Boolean] and
      (__ \ 'countryOfResidence).readNullable[String] and
      (__ \ "entityStart").read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)
    ).tupled.map {
    case (name, None, None, None, None, country, entityStart, provisional) =>
      CompanyBeneficiary(name, None, None, None, None, country, entityStart, provisional)
    case (name, utr, address, None, _, country, entityStart, provisional) =>
      CompanyBeneficiary(name, utr, address, None, incomeDiscretionYesNo = Some(true), country, entityStart, provisional)
    case (name, utr, address, _, Some(true), country, entityStart, provisional) =>
      CompanyBeneficiary(name, utr, address, None, incomeDiscretionYesNo = Some(true), country, entityStart, provisional)
    case (name, utr, address, income, _, country, entityStart, provisional) =>
      CompanyBeneficiary(name, utr, address, income, incomeDiscretionYesNo = Some(false), country, entityStart, provisional)
  }

  implicit val writes: Writes[CompanyBeneficiary] = (
    (__ \ 'organisationName).write[String] and
      (__ \ 'identification \ 'utr).writeNullable[String] and
      (__ \ 'identification \ 'address).writeNullable[Address] and
      (__ \ 'beneficiaryShareOfIncome).writeNullable[String] and
      (__ \ 'beneficiaryDiscretion).writeNullable[Boolean] and
      (__ \ 'countryOfResidence).writeNullable[String] and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    ).apply(unlift(CompanyBeneficiary.unapply))

}
