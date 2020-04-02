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

import models.{Enumerable, WithName}
import viewmodels.RadioOption

sealed trait TypeOfBeneficiaryToAdd

object TypeOfBeneficiaryToAdd extends Enumerable.Implicits {

  case object Individual extends WithName("individual") with TypeOfBeneficiaryToAdd
  case object ClassOfBeneficiaries extends WithName("classOfBeneficiaries") with TypeOfBeneficiaryToAdd
  case object CharityOrTrust extends WithName("charityOrTrust") with TypeOfBeneficiaryToAdd
  case object CompanyOrEmploymentRelated extends WithName("companyOrEmploymentRelated") with TypeOfBeneficiaryToAdd
  case object Other extends WithName("other") with TypeOfBeneficiaryToAdd

  val values: List[TypeOfBeneficiaryToAdd] = List(
    Individual, ClassOfBeneficiaries, CharityOrTrust, CompanyOrEmploymentRelated, Other
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("whatTypeOfBeneficiary", value.toString)
  }

  implicit val enumerable: Enumerable[TypeOfBeneficiaryToAdd] =
    Enumerable(values.map(v => v.toString -> v): _*)

}