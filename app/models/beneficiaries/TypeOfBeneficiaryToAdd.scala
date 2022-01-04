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

package models.beneficiaries

import models.{Enumerable, WithName}

sealed trait TypeOfBeneficiaryToAdd

object TypeOfBeneficiaryToAdd extends Enumerable.Implicits {

  val prefix = "whatTypeOfBeneficiary"

  case object Individual extends WithName("individual") with TypeOfBeneficiaryToAdd
  case object ClassOfBeneficiaries extends WithName("classOfBeneficiaries") with TypeOfBeneficiaryToAdd
  case object CharityOrTrust extends WithName("charityOrTrust") with TypeOfBeneficiaryToAdd
  case object CompanyOrEmploymentRelated extends WithName("companyOrEmploymentRelated") with TypeOfBeneficiaryToAdd
  case object Other extends WithName("other") with TypeOfBeneficiaryToAdd

  case object Charity extends WithName("charity") with TypeOfBeneficiaryToAdd
  case object Trust extends WithName("trust") with TypeOfBeneficiaryToAdd
  case object Company extends WithName("company") with TypeOfBeneficiaryToAdd
  case object EmploymentRelated extends WithName("employmentRelated") with TypeOfBeneficiaryToAdd

  val values: List[TypeOfBeneficiaryToAdd] = List(
    Individual, ClassOfBeneficiaries, CharityOrTrust, CompanyOrEmploymentRelated, Other,
    Charity, Trust, Company, EmploymentRelated
  )

  implicit val enumerable: Enumerable[TypeOfBeneficiaryToAdd] =
    Enumerable(values.map(v => v.toString -> v): _*)

}