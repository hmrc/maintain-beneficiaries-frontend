/*
 * Copyright 2023 HM Revenue & Customs
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

package navigation

import models.NormalMode
import models.beneficiaries.TypeOfBeneficiaryToAdd._
import models.beneficiaries._
import play.api.mvc.Call
import utils.Constants.MAX

class BeneficiaryNavigator {

  def addBeneficiaryRoute(beneficiaries: Beneficiaries): Call = {
    val routes: List[(List[Beneficiary], Call)] = List(
      (beneficiaries.individualDetails, addBeneficiaryNowRoute(Individual)),
      (beneficiaries.unidentified, addBeneficiaryNowRoute(ClassOfBeneficiaries)),
      (beneficiaries.company, addBeneficiaryNowRoute(Company)),
      (beneficiaries.employmentRelated, addBeneficiaryNowRoute(EmploymentRelated)),
      (beneficiaries.trust, addBeneficiaryNowRoute(Trust)),
      (beneficiaries.charity, addBeneficiaryNowRoute(Charity)),
      (beneficiaries.other, addBeneficiaryNowRoute(Other))
    )

    routes.filter(_._1.size < MAX) match {
      case (_, x) :: Nil =>
        x
      case (x, _) :: (y, _) :: Nil if x == beneficiaries.company && y == beneficiaries.employmentRelated =>
        addBeneficiaryNowRoute(CompanyOrEmploymentRelated)
      case (x, _) :: (y, _) :: Nil if x == beneficiaries.trust && y == beneficiaries.charity =>
        addBeneficiaryNowRoute(CharityOrTrust)
      case _ =>
        controllers.routes.AddNowController.onPageLoad()
    }
  }

  def addBeneficiaryNowRoute(`type`: TypeOfBeneficiaryToAdd): Call = {
    `type` match {
      case Individual => controllers.individualbeneficiary.routes.NameController.onPageLoad(NormalMode)
      case ClassOfBeneficiaries => controllers.classofbeneficiary.add.routes.DescriptionController.onPageLoad()
      case CompanyOrEmploymentRelated => controllers.companyoremploymentrelated.routes.CompanyOrEmploymentRelatedController.onPageLoad()
      case Company => controllers.companyoremploymentrelated.company.routes.NameController.onPageLoad(NormalMode)
      case EmploymentRelated => controllers.companyoremploymentrelated.employment.routes.NameController.onPageLoad(NormalMode)
      case CharityOrTrust => controllers.charityortrust.routes.CharityOrTrustController.onPageLoad()
      case Trust => controllers.charityortrust.trust.routes.NameController.onPageLoad(NormalMode)
      case Charity => controllers.charityortrust.charity.routes.NameController.onPageLoad(NormalMode)
      case Other => controllers.other.routes.DescriptionController.onPageLoad(NormalMode)
    }
  }
}
