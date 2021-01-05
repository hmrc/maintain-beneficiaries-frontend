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

package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

sealed trait Address

case class UkAddress (line1: String,
                      line2: String,
                      line3: Option[String],
                      line4: Option[String],
                      postcode: String) extends Address

object UkAddress {

  implicit val reads: Reads[UkAddress] =
    ((__ \ 'line1).read[String] and
      (__ \ 'line2).read[String] and
      (__ \ 'line3).readNullable[String] and
      (__ \ 'line4).readNullable[String] and
      (__ \ 'postCode).read[String]).apply(UkAddress.apply _)

  implicit val writes: Writes[UkAddress] =
    ((__ \ 'line1).write[String] and
      (__ \ 'line2).write[String] and
      (__ \ 'line3).writeNullable[String] and
      (__ \ 'line4).writeNullable[String] and
      (__ \ 'postCode).write[String] and
      (__ \ 'country).write[String]
      ).apply(address => (
      address.line1,
      address.line2,
      address.line3,
      address.line4,
      address.postcode,
      "GB"
    ))

  implicit val format = Format[UkAddress](reads, writes)
}

case class NonUkAddress (line1: String,
                         line2: String,
                         line3: Option[String] = None,
                         country: String) extends Address

object NonUkAddress {
  implicit val format = Json.format[NonUkAddress]
}

object Address {
  implicit val reads: Reads[Address] =
    __.read[UkAddress](UkAddress.reads).widen[Address] orElse
    __.read[NonUkAddress](NonUkAddress.format).widen[Address]

  implicit val writes: Writes[Address] = Writes {
    case a:UkAddress => Json.toJson(a)(UkAddress.writes)
    case a:NonUkAddress => Json.toJson(a)(NonUkAddress.format)
  }
}

