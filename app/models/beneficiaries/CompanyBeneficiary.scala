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

import models.{Address, Name}
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class CompanyBeneficiary(name: String,
                              utr: Option[String],
                              address: Option[Address],
                              income: Option[String],
                              incomeYesNo: Boolean,
                              entityStart: LocalDate
                             ) extends Beneficiary

object CompanyBeneficiary {

  implicit val reads: Reads[CompanyBeneficiary] =
    ((__ \ 'organisationName).read[String] and
      __.lazyRead(readNullableAtSubPath[String](__ \ 'identification \ 'utr)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address)) and
      (__ \ 'beneficiaryShareOfIncome).readNullable[String] and
      (__ \ 'beneficiaryDiscretion).readWithDefault[Boolean](true) and
      (__ \ "entityStart").read[LocalDate])(CompanyBeneficiary.apply _)

  def readNullableAtSubPath[T:Reads](subPath : JsPath) : Reads[Option[T]] = Reads (
    _.transform(subPath.json.pick)
      .flatMap(_.validate[T])
      .map(Some(_))
      .recoverWith(_ => JsSuccess(None))
  )
}
