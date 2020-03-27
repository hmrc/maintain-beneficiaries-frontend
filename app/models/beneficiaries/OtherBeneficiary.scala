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

package models.beneficiaries

import java.time.LocalDate

import models.Address
import play.api.libs.json._
import play.api.libs.functional.syntax._

final case class OtherBeneficiary(description: String,
                                  address : Option[Address],
                                  income: Option[String],
                                  incomeDiscretionYesNo: Boolean,
                                  entityStart: LocalDate,
                                  provisional : Boolean) extends Beneficiary

object OtherBeneficiary {

  implicit val reads: Reads[OtherBeneficiary] =
    ((__ \ 'description).read[String] and
      (__ \ 'address).readNullable[Address] and
      (__ \ 'beneficiaryShareOfIncome).readNullable[String] and
      (__ \ 'beneficiaryDiscretion).readNullable[Boolean] and
      (__ \ "entityStart").read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)).tupled.map {
      case (name, address, None, _, entityStart, provisional) =>
        OtherBeneficiary(name, address, None, incomeDiscretionYesNo = true, entityStart, provisional)
      case (name, address, _, Some(true), entityStart, provisional) =>
        OtherBeneficiary(name, address, None, incomeDiscretionYesNo = true, entityStart, provisional)
      case (name, address, income, _, entityStart, provisional) =>
        OtherBeneficiary(name, address, income, incomeDiscretionYesNo = false, entityStart, provisional)
    }

}