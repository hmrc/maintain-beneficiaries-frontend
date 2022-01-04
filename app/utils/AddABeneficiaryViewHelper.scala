/*
 * Copyright 2022 HM Revenue & Customs
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

import models.TypeOfTrust
import models.beneficiaries._
import play.api.i18n.Messages
import viewmodels.addAnother.{AddRow, AddToRows}

class AddABeneficiaryViewHelper {

  def rows(beneficiaries: Beneficiaries, migratingFromNonTaxableToTaxable: Boolean, trustType: Option[TypeOfTrust] = None)
          (implicit messages: Messages): AddToRows = {

    implicit class BeneficiaryRows[T <: Beneficiary](beneficiaries: List[T]) {
      def zipThenGroupThenMap(row: (T, Int) => AddRow, isComplete: Boolean): List[AddRow] = beneficiaries
        .zipWithIndex
        .groupBy(_._1.hasRequiredData(migratingFromNonTaxableToTaxable, trustType))
        .getOrElse(isComplete, Nil)
        .map(x => row(x._1, x._2))
    }

    def individualBeneficiaryRow(beneficiary: IndividualBeneficiary, index: Int): AddRow = {
      AddRow(
        name = beneficiary.name.displayName,
        typeLabel = messages("entities.beneficiaries.individual"),
        changeUrl = if (beneficiary.hasRequiredData(migratingFromNonTaxableToTaxable, trustType)) {
          Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRender(index).url)
        } else {
          Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRedirect(index).url)
        },
        removeUrl = Some(controllers.individualbeneficiary.remove.routes.RemoveIndividualBeneficiaryController.onPageLoad(index).url)
      )
    }

    def classOfBeneficiaryRow(beneficiary: ClassOfBeneficiary, index: Int): AddRow = {
      AddRow(
        name = beneficiary.description,
        typeLabel = messages("entities.beneficiaries.unidentified"),
        changeUrl = Some(controllers.classofbeneficiary.amend.routes.DescriptionController.onPageLoad(index).url),
        removeUrl = Some(controllers.classofbeneficiary.remove.routes.RemoveClassOfBeneficiaryController.onPageLoad(index).url)
      )
    }

    def trustBeneficiaryRow(beneficiary: TrustBeneficiary, index: Int): AddRow = {
      AddRow(
        name = beneficiary.name,
        typeLabel = messages("entities.beneficiaries.trust"),
        changeUrl = if (beneficiary.hasRequiredData(migratingFromNonTaxableToTaxable, trustType)) {
          Some(controllers.charityortrust.trust.amend.routes.CheckDetailsController.extractAndRender(index).url)
        } else {
          Some(controllers.charityortrust.trust.amend.routes.CheckDetailsController.extractAndRedirect(index).url)
        },
        removeUrl = Some(controllers.charityortrust.trust.remove.routes.RemoveTrustBeneficiaryController.onPageLoad(index).url)
      )
    }

    def charityBeneficiaryRow(beneficiary: CharityBeneficiary, index: Int): AddRow = {
      AddRow(
        name = beneficiary.name,
        typeLabel = messages("entities.beneficiaries.charity"),
        changeUrl = if (beneficiary.hasRequiredData(migratingFromNonTaxableToTaxable, trustType)) {
          Some(controllers.charityortrust.charity.amend.routes.CheckDetailsController.extractAndRender(index).url)
        } else {
          Some(controllers.charityortrust.charity.amend.routes.CheckDetailsController.extractAndRedirect(index).url)
        },
        removeUrl = Some(controllers.charityortrust.charity.remove.routes.RemoveCharityBeneficiaryController.onPageLoad(index).url)
      )
    }

    def companyBeneficiaryRow(beneficiary: CompanyBeneficiary, index: Int): AddRow = {
      AddRow(
        name = beneficiary.name,
        typeLabel = messages("entities.beneficiaries.company"),
        changeUrl = if (beneficiary.hasRequiredData(migratingFromNonTaxableToTaxable, trustType)) {
          Some(controllers.companyoremploymentrelated.company.amend.routes.CheckDetailsController.extractAndRender(index).url)
        } else {
          Some(controllers.companyoremploymentrelated.company.amend.routes.CheckDetailsController.extractAndRedirect(index).url)
        },
        removeUrl = Some(controllers.companyoremploymentrelated.company.remove.routes.RemoveCompanyBeneficiaryController.onPageLoad(index).url)
      )
    }

    def employmentRelatedBeneficiaryRow(beneficiary: EmploymentRelatedBeneficiary, index: Int): AddRow = {
      AddRow(
        name = beneficiary.name,
        typeLabel = messages("entities.beneficiaries.employmentRelated"),
        changeUrl = Some(controllers.companyoremploymentrelated.employment.amend.routes.CheckDetailsController.extractAndRender(index).url),
        removeUrl = Some(controllers.companyoremploymentrelated.employment.remove.routes.RemoveEmploymentBeneficiaryController.onPageLoad(index).url)
      )
    }

    def otherBeneficiaryRow(beneficiary: OtherBeneficiary, index: Int): AddRow = {
      AddRow(
        name = beneficiary.description,
        typeLabel = messages("entities.beneficiaries.other"),
        changeUrl = if (beneficiary.hasRequiredData(migratingFromNonTaxableToTaxable, trustType)) {
          Some(controllers.other.amend.routes.CheckDetailsController.extractAndRender(index).url)
        } else {
          Some(controllers.other.amend.routes.CheckDetailsController.extractAndRedirect(index).url)
        },
        removeUrl = Some(controllers.other.remove.routes.RemoveOtherBeneficiaryController.onPageLoad(index).url)
      )
    }

    def rows(isComplete: Boolean): List[AddRow] = {
      beneficiaries.individualDetails.zipThenGroupThenMap(individualBeneficiaryRow, isComplete) ++
        beneficiaries.unidentified.zipThenGroupThenMap(classOfBeneficiaryRow, isComplete) ++
        beneficiaries.company.zipThenGroupThenMap(companyBeneficiaryRow, isComplete) ++
        beneficiaries.employmentRelated.zipThenGroupThenMap(employmentRelatedBeneficiaryRow, isComplete) ++
        beneficiaries.trust.zipThenGroupThenMap(trustBeneficiaryRow, isComplete) ++
        beneficiaries.charity.zipThenGroupThenMap(charityBeneficiaryRow, isComplete) ++
        beneficiaries.other.zipThenGroupThenMap(otherBeneficiaryRow, isComplete)
    }

    val inProgressRows: List[AddRow] = if (migratingFromNonTaxableToTaxable) {
      rows(isComplete = false)
    } else {
      Nil
    }

    val completedRows: List[AddRow] = rows(isComplete = true)

    AddToRows(inProgressRows, completedRows)
  }

}
