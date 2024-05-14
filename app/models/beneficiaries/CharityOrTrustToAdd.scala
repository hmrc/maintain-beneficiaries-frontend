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

import models.{Enumerable, WithName}
import viewmodels.RadioOption

sealed trait CharityOrTrustToAdd

object CharityOrTrustToAdd extends Enumerable.Implicits {

  val prefix = "isTheBeneficiaryACharityOrTrust"

  case object Charity extends WithName("charity") with CharityOrTrustToAdd
  case object Trust extends WithName("trust") with CharityOrTrustToAdd

  val values: List[CharityOrTrustToAdd] = List(
    Charity, Trust
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption(prefix, value.toString)
  }

  implicit val enumerable: Enumerable[CharityOrTrustToAdd] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
