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

package navigation

import models.NormalMode
import models.beneficiaries._
import play.api.mvc.Call
import utils.Constants.MAX

class BeneficiaryNavigator {

  def addBeneficiaryRoute(beneficiaries: Beneficiaries): Call = {
    val routes: List[(List[Beneficiary], Call)] = List(
      (beneficiaries.individualDetails, controllers.individualbeneficiary.routes.NameController.onPageLoad(NormalMode)),
      (beneficiaries.unidentified, controllers.classofbeneficiary.add.routes.DescriptionController.onPageLoad()),
      (beneficiaries.company, controllers.companyoremploymentrelated.company.routes.NameController.onPageLoad(NormalMode)),
      (beneficiaries.employmentRelated, controllers.companyoremploymentrelated.employment.routes.NameController.onPageLoad(NormalMode)),
      (beneficiaries.trust, controllers.charityortrust.trust.routes.NameController.onPageLoad(NormalMode)),
      (beneficiaries.charity, controllers.charityortrust.charity.routes.NameController.onPageLoad(NormalMode)),
      (beneficiaries.other, controllers.other.routes.DescriptionController.onPageLoad(NormalMode))
    )

    routes.filter(_._1.size < MAX) match {
      case ::(head, Nil) =>
        head._2
      case ::((x, _), ::((y, _), Nil)) if x == beneficiaries.company && y == beneficiaries.employmentRelated =>
        controllers.companyoremploymentrelated.routes.CompanyOrEmploymentRelatedController.onPageLoad()
      case ::((x, _), ::((y, _), Nil)) if x == beneficiaries.trust && y == beneficiaries.charity =>
        controllers.charityortrust.routes.CharityOrTrustController.onPageLoad()
      case _ =>
        controllers.routes.AddNowController.onPageLoad()
    }
  }
}
