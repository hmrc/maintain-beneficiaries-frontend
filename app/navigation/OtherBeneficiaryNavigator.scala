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

package navigation

import controllers.other.add.{routes => addRts}
import controllers.other.amend.{routes => amendRts}
import controllers.other.{routes => rts}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.other._
import pages.other.add.StartDatePage
import pages.other.amend.IndexPage
import play.api.mvc.Call

import javax.inject.Inject

class OtherBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    routes(mode)(page)(userAnswers)

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    nextPage(page, NormalMode, userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case DescriptionPage => ua => if (ua.isTaxable) {
      rts.DiscretionYesNoController.onPageLoad(mode)
    } else {
      rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    }
    case ShareOfIncomePage => ua => rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    case CountryOfResidencePage => ua => navigateAwayFromCountryOfResidenceQuestions(ua, mode)
    case UkAddressPage | NonUkAddressPage => ua => navigateToStartDateOrCheckAnswers(ua, mode)
    case StartDatePage => _ => addRts.CheckDetailsController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case DiscretionYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = DiscretionYesNoPage,
      yesCall = rts.CountryOfResidenceYesNoController.onPageLoad(mode),
      noCall = rts.ShareOfIncomeController.onPageLoad(mode)
    )
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
      noCall = navigateToStartDateOrCheckAnswers(ua, mode)
    )
    case AddressUkYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = AddressUkYesNoPage,
      yesCall = rts.UkAddressController.onPageLoad(mode),
      noCall = rts.NonUkAddressController.onPageLoad(mode)
    )
  }

  private def navigateAwayFromCountryOfResidenceQuestions(ua: UserAnswers, mode: Mode): Call = {
    if (ua.isTaxable) {
      rts.AddressYesNoController.onPageLoad(mode)
    } else {
      navigateToStartDateOrCheckAnswers(ua, mode)
    }
  }

  private def navigateToStartDateOrCheckAnswers(ua: UserAnswers, mode: Mode): Call = {
    mode match {
      case NormalMode => addRts.StartDateController.onPageLoad()
      case CheckMode => checkDetailsRoute(ua)
    }
  }

  private def checkDetailsRoute(answers: UserAnswers): Call = {
    answers.get(IndexPage) match {
      case Some(x) => amendRts.CheckDetailsController.renderFromUserAnswers(x)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) orElse
      yesNoNavigation(mode)
}
