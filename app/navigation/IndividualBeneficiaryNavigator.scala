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

import controllers.individualbeneficiary.add.{routes => rts}
import models.UserAnswers
import pages.{Page, QuestionPage}
import pages.individualbeneficiary._
import play.api.mvc.Call

object IndividualBeneficiaryNavigator {
  private val simpleNavigation: PartialFunction[Page, Call] = {
    case IndividualNamePage => rts.DateOfBirthYesNoController.onPageLoad()
    case DateOfBirthPage => rts.IncomeDiscretionYesNoController.onPageLoad()
    case NationalInsuranceNumberPage => ???
    case UkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
    case NonUkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
    case IdCardDetailsPage => ???
  }
  private val yesNoNavigations : PartialFunction[Page, UserAnswers => Call] =
    yesNoNav(DateOfBirthYesNoPage, rts.DateOfBirthController.onPageLoad(), rts.IncomeDiscretionYesNoController.onPageLoad()) orElse
    yesNoNav(IncomeDiscretionYesNoPage, rts.DateOfBirthController.onPageLoad(), rts.IncomePercentageController.onPageLoad()) orElse
    yesNoNav(NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(), ???) orElse
    yesNoNav(LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad()) orElse
    yesNoNav(PassportDetailsYesNoPage, rts.PassportDetailsController.onPageLoad(), rts.IdCardDetailsYesNoController.onPageLoad()) orElse
    yesNoNav(IdCardDetailsYesNoPage, rts.IdCardDetailsController.onPageLoad(), ???)

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
    yesNoNavigations

  def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call) : PartialFunction[Page, UserAnswers => Call] = {
    case `fromPage` =>
      ua => ua.get(fromPage)
        .map(if (_) yesCall else noCall)
        .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
