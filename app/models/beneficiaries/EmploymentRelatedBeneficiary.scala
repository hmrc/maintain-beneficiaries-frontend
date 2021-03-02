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

import models.{Address, Description}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class EmploymentRelatedBeneficiary(name: String,
                                        utr: Option[String],
                                        address: Option[Address],
                                        description: Description,
                                        howManyBeneficiaries: String,
                                        entityStart: LocalDate,
                                        provisional: Boolean) extends Beneficiary

object EmploymentRelatedBeneficiary extends BeneficiaryReads {

  implicit val reads: Reads[EmploymentRelatedBeneficiary] = (
    (__ \ 'organisationName).read[String] and
      __.lazyRead(readNullableAtSubPath[String](__ \ 'identification \ 'utr)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address)) and
      __.read[Description] and
      (__ \ 'numberOfBeneficiary).read[String] and
      (__ \ 'entityStart).read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)
    ).apply(EmploymentRelatedBeneficiary.apply _)

  implicit val writes: Writes[EmploymentRelatedBeneficiary] = (
    (__ \ 'organisationName).write[String] and
      (__ \ 'identification \ 'utr).writeNullable[String] and
      (__ \ 'identification \ 'address).writeNullable[Address] and
      __.write[Description] and
      (__ \ 'numberOfBeneficiary).write[String] and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    ).apply(unlift(EmploymentRelatedBeneficiary.unapply))

}