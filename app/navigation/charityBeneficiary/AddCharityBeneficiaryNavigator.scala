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

package navigation.charityBeneficiary

import controllers.charityortrust.charity.add.{routes => rts}
import javax.inject.Inject
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import pages.charityortrust.charity._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

class AddCharityBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes(page)(userAnswers)

  private val simpleNavigation: PartialFunction[Page, Call] = {
    case NamePage => rts.DiscretionYesNoController.onPageLoad(NormalMode)
    case ShareOfIncomePage => rts.AddressYesNoController.onPageLoad(NormalMode)
    case UkAddressPage => rts.StartDateController.onPageLoad()
    case NonUkAddressPage => rts.StartDateController.onPageLoad()
    case StartDatePage => rts.CheckDetailsController.onPageLoad()
  }

  private val yesNoNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case DiscretionYesNoPage => ua =>
      yesNoNav(ua, DiscretionYesNoPage, rts.AddressYesNoController.onPageLoad(NormalMode), rts.ShareOfIncomeController.onPageLoad(NormalMode))
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(NormalMode), rts.StartDateController.onPageLoad())
    case AddressUkYesNoPage => ua =>
      yesNoNav(ua, AddressUkYesNoPage, rts.UkAddressController.onPageLoad(NormalMode), rts.NonUkAddressController.onPageLoad(NormalMode))
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
    yesNoNavigation

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
