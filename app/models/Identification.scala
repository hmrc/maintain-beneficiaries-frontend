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

package models

import java.time.LocalDate

import play.api.libs.json.{Format, Json, Reads, Writes, __}

sealed trait IndividualIdentification

object IndividualIdentification {
  implicit val reads: Reads[IndividualIdentification] =
    (__ \ 'passport).read[CombinedPassportOrIdCard].widen[IndividualIdentification] orElse
    __.read[NationalInsuranceNumber].widen[IndividualIdentification]

  implicit val writes: Writes[IndividualIdentification] = Writes {
    case ni:NationalInsuranceNumber =>Json.toJson(ni)(NationalInsuranceNumber.format)
    case p:Passport => Json.obj("passport" -> Json.toJson(p)(Passport.format))
    case i:IdCard=> Json.obj("passport" -> Json.toJson(i)(IdCard.format))
    case c:CombinedPassportOrIdCard=> Json.obj("passport" -> Json.toJson(c)(CombinedPassportOrIdCard.format))
  }
}

case class NationalInsuranceNumber(nino: String) extends IndividualIdentification
object NationalInsuranceNumber{
  implicit val format: Format[NationalInsuranceNumber] = Json.format[NationalInsuranceNumber]
}

case class Passport(countryOfIssue: String, number: String, expirationDate: LocalDate) extends IndividualIdentification {
  def asCombined = CombinedPassportOrIdCard(countryOfIssue, number, expirationDate)
}

object Passport {
  implicit val format: Format[Passport] = Json.format[Passport]
}

case class IdCard(countryOfIssue: String, number: String, expirationDate: LocalDate) extends IndividualIdentification {
   def asCombined = CombinedPassportOrIdCard(countryOfIssue, number, expirationDate)
}

object IdCard {
  implicit val format: Format[IdCard] = Json.format[IdCard]
}

case class CombinedPassportOrIdCard(countryOfIssue: String, number: String, expirationDate: LocalDate) extends IndividualIdentification
object CombinedPassportOrIdCard {
  implicit val format: Format[CombinedPassportOrIdCard] = Json.format[CombinedPassportOrIdCard]
}


