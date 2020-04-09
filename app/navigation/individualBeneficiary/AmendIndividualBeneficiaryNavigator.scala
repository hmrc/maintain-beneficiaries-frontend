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

package navigation.individualBeneficiary

import controllers.individualbeneficiary.amend.{routes => rts}
import javax.inject.Inject
import models.{TypeOfTrust, UserAnswers}
import navigation.Navigator
import pages.individualbeneficiary._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

class AmendIndividualBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes(page)(userAnswers)

  private val simpleNavigation: PartialFunction[Page, Call] = {
    case RoleInCompanyPage => rts.DateOfBirthYesNoController.onPageLoad()
    case DateOfBirthPage => rts.IncomeDiscretionYesNoController.onPageLoad()
    case IncomePercentagePage => rts.NationalInsuranceNumberYesNoController.onPageLoad()
    case NationalInsuranceNumberPage => rts.VPE1FormYesNoController.onPageLoad()
    case UkAddressPage => rts.PassportOrIdCardDetailsYesNoController.onPageLoad()
    case NonUkAddressPage => rts.PassportOrIdCardDetailsYesNoController.onPageLoad()
    case PassportOrIdCardDetailsPage => rts.VPE1FormYesNoController.onPageLoad()
  }
  private val yesNoNavigation : PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => ua => namePageNav(ua)
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
    case PassportOrIdCardDetailsYesNoPage => ua =>
        yesNoNav(ua, PassportOrIdCardDetailsYesNoPage, rts.PassportOrIdCardDetailsController.onPageLoad(), rts.VPE1FormYesNoController.onPageLoad())
    case VPE1FormYesNoPage => ua => checkDetailsRoute(ua)
  }

  val routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_ : UserAnswers) => c) orElse
    yesNoNavigation

  def namePageNav(ua: UserAnswers): Call = ua.trustType match {
    case TypeOfTrust.EmployeeRelated => rts.RoleInCompanyController.onPageLoad()
    case _ => rts.DateOfBirthYesNoController.onPageLoad()
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
        rts.CheckDetailsController.renderFromUserAnswers(x)
    }
  }
}
