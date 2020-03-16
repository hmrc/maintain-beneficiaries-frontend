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

import play.api.i18n.{Messages, MessagesProvider}
import play.api.libs.json.{Format, Json}

sealed trait Beneficiary {
  val provisional : Boolean

  def isNewlyAdded : Boolean = provisional
}

object Beneficiary {
  implicit val formats: Format[Beneficiary] = Json.format[Beneficiary]
}

final case class IndividualBeneficiary(name: Name,
                                       dateOfBirth: Option[LocalDate],
                                       nationalInsuranceNumber: Option[String],
                                       address : Option[Address],
                                       vulnerableYesNo: Boolean,
                                       income: Option[String],
                                       incomeYesNo: Boolean,
                                       provisional: Boolean
                                      ) extends Beneficiary

object IndividualBeneficiary {
  implicit val classFormat: Format[IndividualBeneficiary] = Json.format[IndividualBeneficiary]
}

final case class ClassOfBeneficiary(description: String, provisional: Boolean) extends Beneficiary

object ClassOfBeneficiary {
  implicit val classFormat : Format[ClassOfBeneficiary] = Json.format[ClassOfBeneficiary]
}

case class Beneficiaries(beneficiaries: List[Beneficiary]) {

  def addToHeading()(implicit mp: MessagesProvider) = beneficiaries.size match {
    case 0 => Messages("addABeneficiary.heading")
    case 1 => Messages("addABeneficiary.singular.heading")
    case l => Messages("addABeneficiary.count.heading", l)
  }

}

object Beneficiaries {
  implicit val formats: Format[Beneficiaries] = Json.format[Beneficiaries]
}
