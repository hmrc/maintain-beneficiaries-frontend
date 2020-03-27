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
    case NamePage => rts.DateOfBirthYesNoController.onPageLoad()
    case DateOfBirthPage => rts.IncomeDiscretionYesNoController.onPageLoad()
    case IncomePercentagePage => rts.NationalInsuranceNumberYesNoController.onPageLoad()
    case NationalInsuranceNumberPage => rts.VPE1FormYesNoController.onPageLoad()
    case UkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
    case NonUkAddressPage => rts.PassportDetailsYesNoController.onPageLoad()
    case PassportDetailsPage => rts.VPE1FormYesNoController.onPageLoad()
    case IdCardDetailsPage => rts.VPE1FormYesNoController.onPageLoad()
    case VPE1FormYesNoPage => rts.StartDateController.onPageLoad()
  }
  private val yesNoNavigations : PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage, rts.DateOfBirthController.onPageLoad(), rts.IncomeDiscretionYesNoController.onPageLoad())
    case IncomeDiscretionYesNoPage => ua =>
        yesNoNav(ua, IncomeDiscretionYesNoPage, rts.NationalInsuranceNumberYesNoController.onPageLoad(), rts.IncomePercentageController.onPageLoad())
    case NationalInsuranceNumberYesNoPage => ua =>
        yesNoNav(ua, NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(), rts.AddressYesNoController.onPageLoad())
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.LiveInTheUkYesNoController.onPageLoad(), rts.VPE1FormYesNoController.onPageLoad())
    case LiveInTheUkYesNoPage => ua =>
        yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
    case PassportDetailsYesNoPage => ua =>
        yesNoNav(ua, PassportDetailsYesNoPage, rts.PassportDetailsController.onPageLoad(), rts.IdCardDetailsYesNoController.onPageLoad())
    case IdCardDetailsYesNoPage => ua =>
        yesNoNav(ua, IdCardDetailsYesNoPage, rts.IdCardDetailsController.onPageLoad(), rts.VPE1FormYesNoController.onPageLoad())
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
    yesNoNavigations

  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
