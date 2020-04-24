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

package navigation.employmentBeneficiary

import controllers.companyoremploymentrelated.employment.amend.{routes => rts}
import javax.inject.Inject
import models.UserAnswers
import navigation.Navigator
import pages.companyoremploymentrelated.employment._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

class AmendEmploymentRelatedBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes(page)(userAnswers)

  private val simpleNavigation: PartialFunction[Page, Call] = {
    case NamePage => rts.AddressYesNoController.onPageLoad()
    case UkAddressPage => rts.DescriptionController.onPageLoad()
    case NonUkAddressPage => rts.DescriptionController.onPageLoad()
    case DescriptionPage => rts.NumberOfBeneficiariesController.onPageLoad()
  }

  private val yesNoNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(), rts.DescriptionController.onPageLoad())
    case AddressUkYesNoPage => ua =>
      yesNoNav(ua, AddressUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
  }

  private val checkDetailsNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case NumberOfBeneficiariesPage => ua =>
      checkDetailsRoute(ua)
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
      yesNoNavigation orElse
      checkDetailsNavigation

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  def checkDetailsRoute(answers: UserAnswers) : Call = {
    answers.get(IndexPage) match {
      case None => controllers.routes.SessionExpiredController.onPageLoad()
      case Some(x) =>
        rts.CheckDetailsController.renderFromUserAnswers(x)
    }
  }
}
