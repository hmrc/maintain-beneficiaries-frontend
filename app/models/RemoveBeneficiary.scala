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

import play.api.libs.json.{Format, Json, Reads}

case class RemoveBeneficiary(`type`: BeneficiaryType, index : Int, endDate: LocalDate)

object RemoveBeneficiary {

  implicit val formats : Format[RemoveBeneficiary] = Json.format[RemoveBeneficiary]

  def apply(`type`: BeneficiaryType, index: Int): RemoveBeneficiary =  RemoveBeneficiary(`type`, index, LocalDate.now)

}

sealed trait BeneficiaryType

object BeneficiaryType extends Enumerable.Implicits {

  case object Unidentified extends WithName("unidentified") with BeneficiaryType
  case object Individual extends WithName("individual") with BeneficiaryType
  case object Company extends WithName("company") with BeneficiaryType
  case object Trust extends WithName("trust") with BeneficiaryType
  case object Charity extends WithName("charity") with BeneficiaryType
  case object Large extends WithName("large") with BeneficiaryType
  case object Other extends WithName("other") with BeneficiaryType

  val values: Set[BeneficiaryType] = Set(
    Unidentified, Individual, Company, Trust, Charity, Large, Other
  )

  implicit val enumerable: Enumerable[BeneficiaryType] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}