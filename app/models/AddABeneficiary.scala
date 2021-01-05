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

import viewmodels.RadioOption

sealed trait AddABeneficiary

object AddABeneficiary extends Enumerable.Implicits {

  case object YesNow extends WithName("add-them-now") with AddABeneficiary
  case object YesLater extends WithName("add-them-later") with AddABeneficiary
  case object NoComplete extends WithName("no-complete") with AddABeneficiary

  val values: List[AddABeneficiary] = List(YesNow, YesLater, NoComplete) filterNot { _ == YesLater}

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("addABeneficiary", value.toString)
  }

  implicit val enumerable: Enumerable[AddABeneficiary] =
    Enumerable(values.map(v => v.toString -> v): _*)
}


