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

import controllers.companyoremploymentrelated.employment.amend.{routes => amendRts}
import controllers.companyoremploymentrelated.employment.{routes => rts}
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.companyoremploymentrelated.employment._
import play.api.mvc.Call

class EmploymentRelatedBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    nextPage(page, NormalMode, userAnswers)

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    case CountryOfResidencePage => ua => navigateAwayFromCountryOfResidenceQuestions(ua, mode)
    case UkAddressPage | NonUkAddressPage => _ => rts.DescriptionController.onPageLoad(mode)
    case DescriptionPage => _ => rts.NumberOfBeneficiariesController.onPageLoad(mode)
    case NumberOfBeneficiariesPage => ua => if (mode == NormalMode) {
      rts.StartDateController.onPageLoad()
    } else {
      checkDetailsRoute(ua)
    }
    case StartDatePage => _ => rts.CheckDetailsController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case CountryOfResidenceYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CountryOfResidenceYesNoPage,
      yesCall = rts.CountryOfResidenceUkYesNoController.onPageLoad(mode),
      noCall = navigateAwayFromCountryOfResidenceQuestions(ua, mode)
    )
    case CountryOfResidenceUkYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CountryOfResidenceUkYesNoPage,
      yesCall = navigateAwayFromCountryOfResidenceQuestions(ua, mode),
      noCall = rts.CountryOfResidenceController.onPageLoad(mode)
    )
    case AddressYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = AddressYesNoPage,
      yesCall = rts.AddressUkYesNoController.onPageLoad(mode),
      noCall = rts.DescriptionController.onPageLoad(mode)
    )
    case AddressUkYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = AddressUkYesNoPage,
      yesCall = rts.UkAddressController.onPageLoad(mode),
      noCall = rts.NonUkAddressController.onPageLoad(mode)
    )
  }

  private def checkDetailsRoute(answers: UserAnswers): Call = {
    answers.get(IndexPage) match {
      case Some(x) => amendRts.CheckDetailsController.renderFromUserAnswers(x)
      case None => controllers.routes.SessionExpiredController.onPageLoad
    }
  }

  private def navigateAwayFromCountryOfResidenceQuestions(ua: UserAnswers, mode: Mode): Call = {
    if (ua.isTaxable) {
      rts.AddressYesNoController.onPageLoad(mode)
    } else {
      rts.DescriptionController.onPageLoad(mode)
    }
  }

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) orElse
      yesNoNavigation(mode)

}
