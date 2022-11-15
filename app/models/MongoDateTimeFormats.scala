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

package models

import java.time.{Instant, LocalDateTime, ZoneOffset, ZonedDateTime}
import scala.util.{Success, Try}
import play.api.libs.json._

trait MongoDateTimeFormats {

  implicit val localDateTimeRead: Reads[LocalDateTime] = {
    case JsObject(map) if map.contains("$date") =>
      map("$date") match {
        case JsNumber(bigDecimal) =>
          JsSuccess(LocalDateTime.ofInstant(Instant.ofEpochMilli(bigDecimal.toLong), ZoneOffset.UTC))
        case JsObject(stringObject) =>
          if (stringObject.contains("$numberLong")) {
            JsSuccess(LocalDateTime.ofInstant(Instant.ofEpochMilli(BigDecimal(stringObject("$numberLong").as[JsString].value).toLong), ZoneOffset.UTC))
          } else {
            JsError("Unexpected LocalDateTime Format")
          }
        case JsString(dateValue) =>
          val parseDateTime = if (dateValue.contains("Z")) { (dateAsString: String) =>
            ZonedDateTime.parse(dateAsString)
          } else { (dateAsString: String) => LocalDateTime.parse(dateAsString) }
          Try(parseDateTime(dateValue)) match {
            case Success(value: LocalDateTime) => JsSuccess(value)
            case Success(value: ZonedDateTime) => JsSuccess(value.toLocalDateTime)
            case _ => JsError("Unexpected LocalDateTime Format")
          }
        case _ => JsError("Unexpected LocalDateTime Format")
      }
    case _ => JsError("Unexpected LocalDateTime Format")
  }

  implicit val localDateTimeWrite: Writes[LocalDateTime] = (dateTime: LocalDateTime) => Json.obj(
    "$date" -> dateTime.atZone(ZoneOffset.UTC).toInstant.toEpochMilli
  )

}

object MongoDateTimeFormats extends MongoDateTimeFormats
