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

import controllers.companyoremploymentrelated.employment.{routes => rts}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.companyoremploymentrelated.employment._
import play.api.mvc.Call

import javax.inject.Inject

class EmploymentRelatedBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    nextPage(page, NormalMode, userAnswers)

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case NamePage => rts.AddressYesNoController.onPageLoad(mode)
    case UkAddressPage => rts.DescriptionController.onPageLoad(mode)
    case NonUkAddressPage => rts.DescriptionController.onPageLoad(mode)
    case DescriptionPage => rts.NumberOfBeneficiariesController.onPageLoad(mode)
    case StartDatePage => rts.CheckDetailsController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(mode), rts.DescriptionController.onPageLoad(mode))
    case AddressUkYesNoPage => ua =>
      yesNoNav(ua, AddressUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
  }

  private def navigationWithCheck(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
    mode match {
      case NormalMode => {
        case NumberOfBeneficiariesPage => _ => rts.StartDateController.onPageLoad()
      }
      case CheckMode => {
        case NumberOfBeneficiariesPage => ua =>
          checkDetailsRoute(ua)
      }
    }
  }

  def checkDetailsRoute(answers: UserAnswers) : Call = {
    answers.get(IndexPage) match {
      case None =>
        controllers.routes.SessionExpiredController.onPageLoad()
      case Some(x) =>
        controllers.companyoremploymentrelated.employment.amend.routes.CheckDetailsController.renderFromUserAnswers(x)
    }
  }

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_:UserAnswers) => c) orElse
      yesNoNavigation(mode) orElse
      navigationWithCheck(mode)

}
