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

import models.{Address, HowManyBeneficiaries}
  import play.api.libs.json._
import play.api.libs.functional.syntax._

case class EmploymentRelatedBeneficiary(name: String,
                                        utr: Option[String],
                                        address: Option[Address],
                                        description: Seq[String],
                                        howManyBeneficiaries: HowManyBeneficiaries,
                                        entityStart: LocalDate
                                       ) extends Beneficiary

object EmploymentRelatedBeneficiary {

  implicit val rds: Reads[EmploymentRelatedBeneficiary] =
    ((__ \ 'organisationName).read[String] and
      __.lazyRead(readNullableAtSubPath[String](__ \ 'identification \ 'utr)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address)) and
      readDescription and
      (__ \ 'numberOfBeneficiary).read[HowManyBeneficiaries] and
      (__ \ 'entityStart).read[LocalDate]
      ).apply(EmploymentRelatedBeneficiary.apply _)

  def readDescription =
    ((__ \ 'description).read[String] and
    (__ \ 'description1).readNullable[String] and
    (__ \ 'description2).readNullable[String] and
    (__ \ 'description3).readNullable[String] and
    (__ \ 'description4).readNullable[String]).tupled.map{
      case (desc, desc1, desc2, desc3, desc4) =>
        Seq(desc) ++ Seq(desc1, desc2, desc3, desc4).flatten
    }


  def readNullableAtSubPath[T:Reads](subPath : JsPath) : Reads[Option[T]] = Reads (
    _.transform(subPath.json.pick)
      .flatMap(_.validate[T])
      .map(Some(_))
      .recoverWith(_ => JsSuccess(None))
  )
}