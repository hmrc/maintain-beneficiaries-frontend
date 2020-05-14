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

import controllers.individualbeneficiary.{routes => rts}
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, TypeOfTrust, UserAnswers}
import pages.individualbeneficiary._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

class IndividualBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    nextPage(page, NormalMode, userAnswers)

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case RoleInCompanyPage => rts.DateOfBirthYesNoController.onPageLoad(mode)
    case DateOfBirthPage => rts.IncomeDiscretionYesNoController.onPageLoad(mode)
    case IncomePercentagePage => rts.NationalInsuranceNumberYesNoController.onPageLoad(mode)
    case NationalInsuranceNumberPage => rts.VPE1FormYesNoController.onPageLoad(mode)
    case UkAddressPage => rts.PassportDetailsYesNoController.onPageLoad(mode)
    case NonUkAddressPage => rts.PassportDetailsYesNoController.onPageLoad(mode)
    case PassportDetailsPage => rts.VPE1FormYesNoController.onPageLoad(mode)
    case IdCardDetailsPage => rts.VPE1FormYesNoController.onPageLoad(mode)
    case StartDatePage => controllers.individualbeneficiary.add.routes.CheckDetailsController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage, rts.DateOfBirthController.onPageLoad(mode), rts.IncomeDiscretionYesNoController.onPageLoad(mode))
    case IncomeDiscretionYesNoPage => ua =>
        yesNoNav(ua, IncomeDiscretionYesNoPage, rts.NationalInsuranceNumberYesNoController.onPageLoad(mode), rts.IncomePercentageController.onPageLoad(mode))
    case NationalInsuranceNumberYesNoPage => ua =>
        yesNoNav(ua, NationalInsuranceNumberYesNoPage, rts.NationalInsuranceNumberController.onPageLoad(mode), rts.AddressYesNoController.onPageLoad(mode))
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, rts.LiveInTheUkYesNoController.onPageLoad(mode), rts.VPE1FormYesNoController.onPageLoad(mode))
    case LiveInTheUkYesNoPage => ua =>
        yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
    case PassportDetailsYesNoPage => ua =>
        yesNoNav(ua, PassportDetailsYesNoPage, rts.PassportDetailsController.onPageLoad(mode), rts.IdCardDetailsYesNoController.onPageLoad(mode))
    case IdCardDetailsYesNoPage => ua =>
        yesNoNav(ua, IdCardDetailsYesNoPage, rts.IdCardDetailsController.onPageLoad(mode), rts.VPE1FormYesNoController.onPageLoad(mode))
  }

  private def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  private def trustTypeNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => ua => trustTypeNav(mode, ua)
  }

  private def trustTypeNav(mode: Mode, ua: UserAnswers): Call = ua.trustType match {
    case TypeOfTrust.EmployeeRelated => rts.RoleInCompanyController.onPageLoad(mode)
    case _ => rts.DateOfBirthYesNoController.onPageLoad(mode)
  }

  private def modeNavigation(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
    mode match {
      case NormalMode => {
        case VPE1FormYesNoPage => _ =>
          rts.StartDateController.onPageLoad()
      }
      case CheckMode => {
        case VPE1FormYesNoPage => ua =>
          modeNav(ua)
      }
    }
  }

  private def modeNav(answers: UserAnswers) : Call = {
    answers.get(IndexPage) match {
      case None =>
        controllers.routes.SessionExpiredController.onPageLoad()
      case Some(x) =>
        controllers.individualbeneficiary.amend.routes.CheckDetailsController.renderFromUserAnswers(x)
    }
  }

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_:UserAnswers) => c) orElse
      yesNoNavigation(mode) orElse
      trustTypeNavigation(mode) orElse
      modeNavigation(mode)
}
