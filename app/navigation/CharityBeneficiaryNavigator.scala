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

package navigation

import controllers.charityortrust.charity.{routes => rts}
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.charityortrust.charity._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

class CharityBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    routes(mode)(page)(userAnswers)

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    nextPage(page, NormalMode, userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case NamePage => rts.DiscretionYesNoController.onPageLoad(mode)
    case ShareOfIncomePage => rts.AddressYesNoController.onPageLoad(mode)
    case StartDatePage => rts.CheckDetailsController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
    case DiscretionYesNoPage => ua =>
      yesNoNav(ua, DiscretionYesNoPage, rts.AddressYesNoController.onPageLoad(mode), rts.ShareOfIncomeController.onPageLoad(mode))
    case AddressUkYesNoPage => ua =>
      yesNoNav(ua, AddressUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
  }

  private def navigationWithCheck(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
    mode match {
      case NormalMode => {
        case UkAddressPage | NonUkAddressPage => _ =>
          rts.StartDateController.onPageLoad()
        case AddressYesNoPage => ua =>
          yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(mode), yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(mode), rts.StartDateController.onPageLoad()))
      }
      case CheckMode => {
        case UkAddressPage | NonUkAddressPage => ua =>
          checkDetailsRoute(ua)
        case AddressYesNoPage => ua =>
          yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(mode), checkDetailsRoute(ua))
      }
    }
  }

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  def checkDetailsRoute(answers: UserAnswers) : Call = {
    answers.get(IndexPage) match {
      case None => controllers.routes.SessionExpiredController.onPageLoad()
      case Some(x) =>
        controllers.charityortrust.charity.amend.routes.CheckDetailsController.renderFromUserAnswers(x)
    }
  }

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_:UserAnswers) => c) orElse
      yesNoNavigation(mode) orElse
      navigationWithCheck(mode)

}
