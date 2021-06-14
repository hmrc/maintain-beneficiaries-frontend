/*
 * Copyright 2021 HM Revenue & Customs
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
import models.{CheckMode, TypeOfTrust}
import play.api.i18n.Messages
import viewmodels.addAnother.{AddRow, AddToRows}

class AddABeneficiaryViewHelper {

  def rows(beneficiaries: Beneficiaries, migratingFromNonTaxableToTaxable: Boolean, trustType: Option[TypeOfTrust] = None)
          (implicit messages: Messages): AddToRows = {

    def rows(beneficiaries: Beneficiaries, isComplete: Boolean): List[AddRow] = {
      beneficiaries.individualDetails.zipWithIndex.map(x => individualBeneficiaryRow(x._1, isComplete, x._2)) ++
        beneficiaries.unidentified.zipWithIndex.map(x => classOfBeneficiaryRow(x._1, isComplete, x._2)) ++
        beneficiaries.company.zipWithIndex.map(x => companyBeneficiaryRow(x._1, isComplete, x._2)) ++
        beneficiaries.employmentRelated.zipWithIndex.map(x => employmentRelatedBeneficiaryRow(x._1, isComplete, x._2)) ++
        beneficiaries.trust.zipWithIndex.map(x => trustBeneficiaryRow(x._1, isComplete, x._2)) ++
        beneficiaries.charity.zipWithIndex.map(x => charityBeneficiaryRow(x._1, isComplete, x._2)) ++
        beneficiaries.other.zipWithIndex.map(x => otherBeneficiaryRow(x._1, isComplete, x._2))
    }

    val inProgressRows: List[AddRow] = if (migratingFromNonTaxableToTaxable) {
      rows(beneficiaries.inProgress(migratingFromNonTaxableToTaxable, trustType), isComplete = false)
    } else {
      Nil
    }

    val completedRows: List[AddRow] = rows(beneficiaries.completed(migratingFromNonTaxableToTaxable, trustType), isComplete = true)

    AddToRows(inProgressRows, completedRows)
  }

  private def individualBeneficiaryRow(beneficiary: IndividualBeneficiary, isComplete: Boolean, index: Int)
                                      (implicit messages: Messages): AddRow = {
    AddRow(
      name = beneficiary.name.displayName,
      typeLabel = messages("entities.beneficiaries.individual"),
      changeUrl = if (isComplete) {
        Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRender(index).url)
      } else {
        Some(controllers.individualbeneficiary.routes.NameController.onPageLoad(CheckMode).url)
      },
      removeUrl = Some(controllers.individualbeneficiary.remove.routes.RemoveIndividualBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def classOfBeneficiaryRow(beneficiary: ClassOfBeneficiary, isComplete: Boolean, index: Int)
                                   (implicit messages: Messages): AddRow = {
    AddRow(
      name = beneficiary.description,
      typeLabel = messages("entities.beneficiaries.unidentified"),
      changeUrl = Some(controllers.classofbeneficiary.amend.routes.DescriptionController.onPageLoad(index).url),
      removeUrl = Some(controllers.classofbeneficiary.remove.routes.RemoveClassOfBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def trustBeneficiaryRow(beneficiary: TrustBeneficiary, isComplete: Boolean, index: Int)
                                 (implicit messages: Messages): AddRow = {
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.trust"),
      changeUrl = if (isComplete) {
        Some(controllers.charityortrust.trust.amend.routes.CheckDetailsController.extractAndRender(index).url)
      } else {
        Some(controllers.charityortrust.trust.routes.NameController.onPageLoad(CheckMode).url)
      },
      removeUrl = Some(controllers.charityortrust.trust.remove.routes.RemoveTrustBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def charityBeneficiaryRow(beneficiary: CharityBeneficiary, isComplete: Boolean, index: Int)
                                   (implicit messages: Messages): AddRow = {
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.charity"),
      changeUrl = if (isComplete) {
        Some(controllers.charityortrust.charity.amend.routes.CheckDetailsController.extractAndRender(index).url)
      } else {
        Some(controllers.charityortrust.charity.routes.NameController.onPageLoad(CheckMode).url)
      },
      removeUrl = Some(controllers.charityortrust.charity.remove.routes.RemoveCharityBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def companyBeneficiaryRow(beneficiary: CompanyBeneficiary, isComplete: Boolean, index: Int)
                                   (implicit messages: Messages): AddRow = {
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.company"),
      changeUrl = if (isComplete) {
        Some(controllers.companyoremploymentrelated.company.amend.routes.CheckDetailsController.extractAndRender(index).url)
      } else {
        Some(controllers.companyoremploymentrelated.company.routes.NameController.onPageLoad(CheckMode).url)
      },
      removeUrl = Some(controllers.companyoremploymentrelated.company.remove.routes.RemoveCompanyBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def employmentRelatedBeneficiaryRow(beneficiary: EmploymentRelatedBeneficiary, isComplete: Boolean, index: Int)
                                             (implicit messages: Messages): AddRow = {
    AddRow(
      name = beneficiary.name,
      typeLabel = messages("entities.beneficiaries.employmentRelated"),
      changeUrl = Some(controllers.companyoremploymentrelated.employment.amend.routes.CheckDetailsController.extractAndRender(index).url),
      removeUrl = Some(controllers.companyoremploymentrelated.employment.remove.routes.RemoveEmploymentBeneficiaryController.onPageLoad(index).url)
    )
  }

  private def otherBeneficiaryRow(beneficiary: OtherBeneficiary, isComplete: Boolean, index: Int)
                                 (implicit messages: Messages): AddRow = {
    AddRow(
      name = beneficiary.description,
      typeLabel = messages("entities.beneficiaries.other"),
      changeUrl = if (isComplete) {
        Some(controllers.other.amend.routes.CheckDetailsController.extractAndRender(index).url)
      } else {
        Some(controllers.other.routes.DescriptionController.onPageLoad(CheckMode).url)
      },
      removeUrl = Some(controllers.other.remove.routes.RemoveOtherBeneficiaryController.onPageLoad(index).url)
    )
  }

}
