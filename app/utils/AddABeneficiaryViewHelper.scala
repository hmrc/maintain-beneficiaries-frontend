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

import models.beneficiaries._
import play.api.i18n.Messages
import viewmodels.addAnother.{AddRow, AddToRows}

class AddABeneficiaryViewHelper(beneficiaries: Beneficiaries)(implicit messages: Messages) {

  private def individualBeneficiaryRow(beneficiary: IndividualBeneficiary, index: Int) : AddRow = {
    AddRow(
      name = beneficiary.name.displayName,
      typeLabel = messages("entities.beneficiaries.individual"),
      changeLabel = messages("site.change.details"),
      changeUrl = Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRender(index).url),
      removeLabel =  messages("site.delete"),
      removeUrl = Some(controllers.individualbeneficiary.remove.routes.RemoveIndividualBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def classOfBeneficiaryRow(beneficiary: ClassOfBeneficiary, index: Int) : AddRow = {
    AddRow(
      name = beneficiary.description,
      typeLabel = messages("entities.beneficiaries.unidentified"),
      changeLabel = messages("site.change.details"),
      changeUrl = Some(controllers.classofbeneficiary.amend.routes.DescriptionController.onPageLoad(index).url),
      removeLabel =  messages("site.delete"),
      removeUrl = Some(controllers.classofbeneficiary.remove.routes.RemoveClassOfBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def renderTrustBeneficiary(beneficiary: TrustBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.trust"),
      changeLabel = messages("site.change.details"),
      changeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url),
      removeLabel = messages("site.delete"),
      removeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url)
    )

  private def renderCharityBeneficiary(beneficiary: CharityBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.charity"),
      changeLabel = messages("site.change.details"),
      changeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url),
      removeLabel = messages("site.delete"),
      removeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url)
    )

  private def renderCompanyBeneficiary(beneficiary: CompanyBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.company"),
      changeLabel = messages("site.change.details"),
      changeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url),
      removeLabel = messages("site.delete"),
      removeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url)
    )

  private def renderEmploymentRelatedBeneficiary(beneficiary: EmploymentRelatedBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.employmentRelated"),
      changeLabel = messages("site.change.details"),
      changeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url),
      removeLabel = messages("site.delete"),
      removeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url)
    )

  private def renderOtherBeneficiary(beneficiary: OtherBeneficiary, index: Int) : AddRow =
    AddRow(
      name = beneficiary.description,
      typeLabel = messages("entities.beneficiaries.other"),
      changeLabel = messages("site.change.details"),
      changeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url),
      removeLabel = messages("site.delete"),
      removeUrl = Some(controllers.routes.FeatureNotAvailableController.onPageLoad().url)
    )

  def rows : AddToRows = {
    val complete =
      beneficiaries.individualDetails.zipWithIndex.map(x => individualBeneficiaryRow(x._1, x._2)) ++
        beneficiaries.unidentified.zipWithIndex.map(x => classOfBeneficiaryRow(x._1, x._2)) ++
        beneficiaries.company.zipWithIndex.map(x => renderCompanyBeneficiary(x._1, x._2)) ++
        beneficiaries.employmentRelated.zipWithIndex.map(x => renderEmploymentRelatedBeneficiary(x._1, x._2)) ++
        beneficiaries.trust.zipWithIndex.map(x => renderTrustBeneficiary(x._1, x._2)) ++
        beneficiaries.charity.zipWithIndex.map(x => renderCharityBeneficiary(x._1, x._2)) ++
        beneficiaries.other.zipWithIndex.map(x => renderOtherBeneficiary(x._1, x._2))

    AddToRows(Nil, complete)
  }

}
