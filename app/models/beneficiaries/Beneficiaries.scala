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

trait Beneficiary {

  val provisional : Boolean

}

case class Beneficiaries(individualDetails: List[IndividualBeneficiary],
                         unidentified: List[ClassOfBeneficiary],
                         company: List[CompanyBeneficiary],
                         employmentRelated: List[EmploymentRelatedBeneficiary],
                         trust: List[TrustBeneficiary],
                         charity: List[CharityBeneficiary],
                         other: List[OtherBeneficiary]) {

  def addToHeading()(implicit mp: MessagesProvider): String =
    (individualDetails ++ unidentified ++ company ++ employmentRelated ++ trust ++ charity ++ other).size match {
      case 0 => Messages("addABeneficiary.heading")
      case 1 => Messages("addABeneficiary.singular.heading")
      case l => Messages("addABeneficiary.count.heading", l)
    }
}

object Beneficiaries {
  implicit val reads: Reads[Beneficiaries] =
    ((__ \ "beneficiary" \ "individualDetails").readWithDefault[List[IndividualBeneficiary]](Nil)
      and (__ \ "beneficiary" \ "unidentified").readWithDefault[List[ClassOfBeneficiary]](Nil)
      and (__ \ "beneficiary" \ "company").readWithDefault[List[CompanyBeneficiary]](Nil)
      and (__ \ "beneficiary" \ "large").readWithDefault[List[EmploymentRelatedBeneficiary]](Nil)
      and (__ \ "beneficiary" \ "trust").readWithDefault[List[TrustBeneficiary]](Nil)
      and (__ \ "beneficiary" \ "charity").readWithDefault[List[CharityBeneficiary]](Nil)
      and (__ \ "beneficiary" \ "other").readWithDefault[List[OtherBeneficiary]](Nil)
      ).apply(Beneficiaries.apply _)
}
