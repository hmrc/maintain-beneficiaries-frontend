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

package utils

import models.beneficiaries.{Beneficiaries, ClassOfBeneficiary, IndividualBeneficiary, TrustBeneficiary}
import play.api.i18n.Messages
import viewmodels.addAnother.{AddRow, AddToRows}

class AddABeneficiaryViewHelper(beneficiaries: Beneficiaries)(implicit messages: Messages) {

  private def render(beneficiary: IndividualBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.name.displayName,
      typeLabel = messages(s"entities.beneficiaries.individual"),
      changeLabel = messages("site.change.details"),
      changeUrl = None,
      removeLabel = messages("site.delete"),
      removeUrl = None
    )
  private def renderClassOf(beneficiary: ClassOfBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.description,
      typeLabel = messages(s"entities.beneficiaries.classOf"),
      changeLabel = messages("site.change.details"),
      changeUrl = None,
      removeLabel = messages("site.delete"),
      removeUrl = None
    )

  private def renderTrustBeneficiary(beneficiary: TrustBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.name,
      typeLabel = messages(s"entities.beneficiaries.trust"),
      changeLabel = messages("site.change.details"),
      changeUrl = None,
      removeLabel = messages("site.delete"),
      removeUrl = None
    )

  def rows : AddToRows = {
    val completeIndividuals = beneficiaries.individualDetails.zipWithIndex.map(x => render(x._1, x._2))
    val completeClassOf = beneficiaries.classOf.zipWithIndex.map(x => renderClassOf(x._1, x._2))
    val completeTrust = beneficiaries.trust.zipWithIndex.map(x => renderTrustBeneficiary(x._1, x._2))

    val complete = completeIndividuals ++ completeClassOf ++ completeTrust

    AddToRows(Nil, complete)
  }

}
