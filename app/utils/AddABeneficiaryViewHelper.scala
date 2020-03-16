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

import models.{Beneficiary, IndividualBeneficiary}
import play.api.i18n.Messages
import viewmodels.addAnother.{AddRow, AddToRows}

class AddABeneficiaryViewHelper(beneficiaries: Beneficiary)(implicit messages: Messages) {

  private def render(beneficiary : (IndividualBeneficiary, Int)) : AddRow = {


        AddRow(
          name = beneficiary._1.name.displayName,
          typeLabel = messages(s"entities.beneficiaries.individual"),
          changeLabel = messages("site.change.details"),
          changeUrl = None,
          removeLabel =  messages("site.delete"),
          removeUrl = None
        )
  }

  def rows : AddToRows = {

    val complete = beneficiaries.individualDetails.zipWithIndex.map(render)

    AddToRows(Nil, complete)
  }

}
