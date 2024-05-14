/*
 * Copyright 2024 HM Revenue & Customs
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

import models.{Address, Description, HowManyBeneficiaries, TypeOfTrust}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class EmploymentRelatedBeneficiary(name: String,
                                        utr: Option[String],
                                        address: Option[Address],
                                        description: Description,
                                        howManyBeneficiaries: HowManyBeneficiaries,
                                        countryOfResidence: Option[String] = None,
                                        entityStart: LocalDate,
                                        provisional: Boolean) extends Beneficiary {

  override def hasRequiredData(migratingFromNonTaxableToTaxable: Boolean, trustType: Option[TypeOfTrust]): Boolean = true
}

object EmploymentRelatedBeneficiary extends BeneficiaryReads {

  implicit val reads: Reads[EmploymentRelatedBeneficiary] = (
    (__ \ Symbol("organisationName")).read[String] and
      __.lazyRead(readNullableAtSubPath[String](__ \ Symbol("identification") \ Symbol("utr"))) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ Symbol("identification") \ Symbol("address"))) and
      __.read[Description] and
      (__ \ Symbol("numberOfBeneficiary")).read[HowManyBeneficiaries] and
      (__ \ Symbol("countryOfResidence")).readNullable[String] and
      (__ \ Symbol("entityStart")).read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)
    )(EmploymentRelatedBeneficiary.apply _)

  implicit val writes: Writes[EmploymentRelatedBeneficiary] = (
    (__ \ Symbol("organisationName")).write[String] and
      (__ \ Symbol("identification") \ Symbol("utr")).writeNullable[String] and
      (__ \ Symbol("identification") \ Symbol("address")).writeNullable[Address] and
      __.write[Description] and
      (__ \ Symbol("numberOfBeneficiary")).write[HowManyBeneficiaries] and
      (__ \ Symbol("countryOfResidence")).writeNullable[String] and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    )(unlift(EmploymentRelatedBeneficiary.unapply))

}
