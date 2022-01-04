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

import models.Address
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

final case class OtherBeneficiary(description: String,
                                  address: Option[Address],
                                  income: Option[String],
                                  incomeDiscretionYesNo: Option[Boolean],
                                  countryOfResidence: Option[String] = None,
                                  entityStart: LocalDate,
                                  provisional: Boolean) extends IncomeBeneficiary

object OtherBeneficiary {

  implicit val reads: Reads[OtherBeneficiary] = (
    (__ \ 'description).read[String] and
      (__ \ 'address).readNullable[Address] and
      (__ \ 'beneficiaryShareOfIncome).readNullable[String] and
      (__ \ 'beneficiaryDiscretion).readNullable[Boolean] and
      (__ \ 'countryOfResidence).readNullable[String] and
      (__ \ "entityStart").read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)
    ).tupled.map {
    case (description, None, None, None, country, entityStart, provisional) =>
      OtherBeneficiary(description, None, None, None, country, entityStart, provisional)
    case (description, address, None, _, country, entityStart, provisional) =>
      OtherBeneficiary(description, address, None, incomeDiscretionYesNo = Some(true), country, entityStart, provisional)
    case (description, address, _, Some(true), country, entityStart, provisional) =>
      OtherBeneficiary(description, address, None, incomeDiscretionYesNo = Some(true), country, entityStart, provisional)
    case (description, address, income, _, country, entityStart, provisional) =>
      OtherBeneficiary(description, address, income, incomeDiscretionYesNo = Some(false), country, entityStart, provisional)
  }

  implicit val writes: Writes[OtherBeneficiary] = (
    (__ \ 'description).write[String] and
      (__ \ 'address).writeNullable[Address] and
      (__ \ 'beneficiaryShareOfIncome).writeNullable[String] and
      (__ \ 'beneficiaryDiscretion).writeNullable[Boolean] and
      (__ \ 'countryOfResidence).writeNullable[String] and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    ).apply(unlift(OtherBeneficiary.unapply))
}