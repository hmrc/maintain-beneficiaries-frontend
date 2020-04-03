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

  type BeneficiaryOption = (Int, TypeOfBeneficiaryToAdd)
  type BeneficiaryOptions = List[BeneficiaryOption]

  def addToHeading()(implicit mp: MessagesProvider): String =
    (individualDetails ++ unidentified ++ company ++ employmentRelated ++ trust ++ charity ++ other).size match {
      case 0 => Messages("addABeneficiary.heading")
      case 1 => Messages("addABeneficiary.singular.heading")
      case l => Messages("addABeneficiary.count.heading", l)
    }

  private val options: BeneficiaryOptions = {
    List((individualDetails.size, Individual)) ++
    List((unidentified.size, ClassOfBeneficiaries)) ++
    List((charity.size, Charity)) ++
    List((trust.size, Trust)) ++
    List((company.size, Company)) ++
    List((employmentRelated.size, EmploymentRelated)) ++
    List((other.size, Other))
  }

  val allAvailableOptions: List[RadioOption] = {

    def combineOptions(list: BeneficiaryOptions): BeneficiaryOptions = {
      @scala.annotation.tailrec
      def recurse(list: BeneficiaryOptions, acc: BeneficiaryOptions): BeneficiaryOptions = {
        list match {
          case Nil => acc
          case List(head, next, _*) if head._2 == Charity && next._2 == Trust =>
            val option: BeneficiaryOption = (head._1 + next._1, CharityOrTrust)
            recurse(list.tail.tail, acc ++ List(option))
          case List(head, next, _*) if head._2 == Company && next._2 == EmploymentRelated =>
            val option: BeneficiaryOption = (head._1 + next._1, CompanyOrEmploymentRelated)
            recurse(list.tail.tail, acc ++ List(option))
          case _ =>
            recurse(list.tail, acc ++ List(list.head))
        }
      }
      recurse(list, Nil)
    }

    combineOptions(options.filter(x => x._1 < 25)).map {
      x => RadioOption(TypeOfBeneficiaryToAdd.prefix, x._2.toString)
    }
  }

  val allUnavailableOptions: List[RadioOption] = {

    options.filter(x => x._1 >= 25).map {
      x => RadioOption(TypeOfBeneficiaryToAdd.prefix, x._2.toString)
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