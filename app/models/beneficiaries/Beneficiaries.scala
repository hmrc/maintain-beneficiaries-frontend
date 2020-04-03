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

import models.beneficiaries.TypeOfBeneficiaryToAdd._
import play.api.i18n.{Messages, MessagesProvider}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}
import viewmodels.RadioOption

trait Beneficiary

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

  private def addToList[A](size: Int, option: A): List[A] = {
    if (size < 25) List(option) else Nil
  }

  val allAvailableOptions: List[RadioOption] = {

    def addEitherOrBothToList(size1: Int,
                              option1: TypeOfBeneficiaryToAdd,
                              size2: Int,
                              option2: TypeOfBeneficiaryToAdd,
                              combinedOption: TypeOfBeneficiaryToAdd): List[TypeOfBeneficiaryToAdd] = {

      if (size1 < 25 && size2 < 25) {
        List(combinedOption)
      } else if (size1 < 25) {
        List(option1)
      } else if (size2 < 25) {
        List(option2)
      } else {
        Nil
      }
    }

    val options: List[TypeOfBeneficiaryToAdd] = {
      addToList(individualDetails.size, Individual) ++
      addToList(unidentified.size, ClassOfBeneficiaries) ++
      addEitherOrBothToList(charity.size, Charity, trust.size, Trust, CharityOrTrust) ++
      addEitherOrBothToList(company.size, Company, employmentRelated.size, EmploymentRelated, CompanyOrEmploymentRelated) ++
      addToList(other.size, Other)
    }

    options.map {
      value =>
        RadioOption(TypeOfBeneficiaryToAdd.prefix, value.toString)
    }
  }

  val allUnavailableOptions: List[RadioOption] = {

    def addToList(size: Int, option: TypeOfBeneficiaryToAdd): List[TypeOfBeneficiaryToAdd] = {
      if (size >= 25) List(option) else Nil
    }

    val options: List[TypeOfBeneficiaryToAdd] = {
      addToList(individualDetails.size, Individual) ++
      addToList(unidentified.size, ClassOfBeneficiaries) ++
      addToList(charity.size, Charity) ++
      addToList(trust.size, Trust) ++
      addToList(company.size, Company) ++
      addToList(employmentRelated.size, EmploymentRelated) ++
      addToList(other.size, Other)
    }

    options.map {
      value =>
        RadioOption(TypeOfBeneficiaryToAdd.prefix, value.toString)
    }
  }

  val availableCharityOrTrustOptions: List[RadioOption] = {

    val options: List[CharityOrTrustToAdd] = {
      addToList(charity.size, CharityOrTrustToAdd.Charity) ++
      addToList(trust.size, CharityOrTrustToAdd.Trust)
    }

    options.map {
      value =>
        RadioOption(CharityOrTrustToAdd.prefix, value.toString)
    }
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