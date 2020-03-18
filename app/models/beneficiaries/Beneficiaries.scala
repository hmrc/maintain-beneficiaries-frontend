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

import play.api.i18n.{Messages, MessagesProvider}
import play.api.libs.json.{Reads, __}
import play.api.libs.functional.syntax._

case class Beneficiaries(individualDetails: List[IndividualBeneficiary],
                         unidentified: List[ClassOfBeneficiary]) {

  def addToHeading()(implicit mp: MessagesProvider) =
    individualDetails.size + unidentified.size match {
      case 0 => Messages("addABeneficiary.heading")
      case 1 => Messages("addABeneficiary.singular.heading")
      case l => Messages("addABeneficiary.count.heading", l)
    }

}

object Beneficiaries {
  implicit val reads: Reads[Beneficiaries] =
    ((__ \ "beneficiary" \ "individualDetails").read[List[IndividualBeneficiary]] and
      (__ \ "beneficiary" \ "unidentified").read[List[ClassOfBeneficiary]]
      ) (Beneficiaries.apply _)
}
