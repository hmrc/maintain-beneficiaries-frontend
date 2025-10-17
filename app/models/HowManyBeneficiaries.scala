/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait HowManyBeneficiaries

object HowManyBeneficiaries extends Enumerable.Implicits {

  case object Over1 extends WithName("over1") with HowManyBeneficiaries
  case object Over101 extends WithName("over101") with HowManyBeneficiaries
  case object Over201 extends WithName("over201") with HowManyBeneficiaries
  case object Over501 extends WithName("over501") with HowManyBeneficiaries
  case object Over1001 extends WithName("over1001") with HowManyBeneficiaries

  val values: List[HowManyBeneficiaries] = List(
    Over1, Over101, Over201, Over501, Over1001
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("numberOfBeneficiaries", value.toString)
  }

  implicit val enumerable: Enumerable[HowManyBeneficiaries] =
    Enumerable(values.map(v => v.toString -> v): _*)

  implicit val reads: Reads[HowManyBeneficiaries] =
    __.read[String].map(_.toInt).map {
      case x if 0 to 100 contains x => Over1
      case x if 101 to 200 contains x => Over101
      case x if 201 to 500 contains x => Over201
      case x if 501 to 999 contains x => Over501
      case _ => Over1001
    }

  implicit val writes: Writes[HowManyBeneficiaries] = Writes {
    case Over1 => JsString("1")
    case Over101 => JsString("101")
    case Over201 => JsString("201")
    case Over501 => JsString("501")
    case Over1001 => JsString("1001")
  }

}
