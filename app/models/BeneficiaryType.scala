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

import play.api.libs.json.{JsString, Writes}

sealed trait BeneficiaryType

object BeneficiaryType {
  case object IndividualBeneficiary extends BeneficiaryType
  case object ClassOfBeneficiary extends BeneficiaryType
  case object CompanyBeneficiary extends BeneficiaryType
  case object EmploymentRelatedBeneficiary extends BeneficiaryType
  case object TrustBeneficiary extends BeneficiaryType
  case object CharityBeneficiary extends BeneficiaryType
  case object OtherBeneficiary extends BeneficiaryType

  implicit val writes: Writes[BeneficiaryType] = Writes {
    case IndividualBeneficiary => JsString("individualDetails")
    case ClassOfBeneficiary => JsString("unidentified")
    case CompanyBeneficiary => JsString("company")
    case EmploymentRelatedBeneficiary => JsString("large")
    case TrustBeneficiary => JsString("trust")
    case CharityBeneficiary => JsString("charity")
    case OtherBeneficiary => JsString("other")
  }
}