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

import controllers.individualbeneficiary.add.{routes => addRts}
import controllers.individualbeneficiary.amend.{routes => amendRts}
import controllers.individualbeneficiary.{routes => rts}
import models.{CheckMode, Mode, NormalMode, TypeOfTrust, UserAnswers}
import pages.Page
import pages.individualbeneficiary._
import pages.individualbeneficiary.add._
import pages.individualbeneficiary.amend.{IndexPage, PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import play.api.mvc.Call

import javax.inject.Inject

class IndividualBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    nextPage(page, NormalMode, userAnswers)

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case RoleInCompanyPage => _ => rts.DateOfBirthYesNoController.onPageLoad(mode)
    case DateOfBirthPage => ua => navigateAwayFromDateOfBirthPages(ua, mode)
    case IncomePercentagePage => ua => navigateAwayFromShareOfIncomeQuestions(ua, mode)
    case CountryOfNationalityPage => ua => navigateAwayFromCountryOfNationalityQuestions(ua, mode)
    case NationalInsuranceNumberPage => ua => navigateAwayFromNinoQuestion(ua, mode)
    case CountryOfResidencePage => ua => navigateAwayFromCountryOfResidencyQuestions(ua, mode)
    case PassportDetailsPage | IdCardDetailsPage | PassportOrIdCardDetailsPage => ua => navigateToMentalCapacityOrVulnerableQuestions(ua, mode)
    case MentalCapacityYesNoPage => ua => navigateAwayFromMentalCapacityPage(ua, mode)
    case StartDatePage => _ => addRts.CheckDetailsController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = DateOfBirthYesNoPage,
      yesCall = rts.DateOfBirthController.onPageLoad(mode),
      noCall = navigateAwayFromDateOfBirthPages(ua, mode)
    )
    case IncomeDiscretionYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = IncomeDiscretionYesNoPage,
      yesCall = navigateAwayFromShareOfIncomeQuestions(ua, mode),
      noCall = rts.IncomePercentageController.onPageLoad(mode)
    )
    case CountryOfNationalityYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CountryOfNationalityYesNoPage,
      yesCall = rts.CountryOfNationalityUkYesNoController.onPageLoad(mode),
      noCall = navigateAwayFromCountryOfNationalityQuestions(ua, mode)
    )
    case CountryOfNationalityUkYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CountryOfNationalityUkYesNoPage,
      yesCall = navigateAwayFromCountryOfNationalityQuestions(ua, mode),
      noCall = rts.CountryOfNationalityController.onPageLoad(mode)
    )
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = NationalInsuranceNumberYesNoPage,
        yesCall = rts.NationalInsuranceNumberController.onPageLoad(mode),
        noCall = navigateAwayFromNinoYesNoQuestion(ua, mode)
      )
    case CountryOfResidenceYesNoPage => ua => yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceYesNoPage,
        yesCall = rts.CountryOfResidenceUkYesNoController.onPageLoad(mode),
        noCall = navigateAwayFromCountryOfResidencyQuestions(ua, mode)
      )
    case CountryOfResidenceUkYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceUkYesNoPage,
        yesCall = navigateAwayFromCountryOfResidencyQuestions(ua, mode),
        noCall = rts.CountryOfResidenceController.onPageLoad(mode)
      )
    case AddressYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = AddressYesNoPage,
        yesCall = rts.LiveInTheUkYesNoController.onPageLoad(mode),
        noCall = navigateToMentalCapacityOrVulnerableQuestions(ua, mode)
      )
    case LiveInTheUkYesNoPage => ua =>
        yesNoNav(ua, LiveInTheUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
    case PassportDetailsYesNoPage => ua =>
        yesNoNav(ua, PassportDetailsYesNoPage, addRts.PassportDetailsController.onPageLoad(), addRts.IdCardDetailsYesNoController.onPageLoad())
    case IdCardDetailsYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = IdCardDetailsYesNoPage,
        yesCall = addRts.IdCardDetailsController.onPageLoad(),
        noCall = navigateToMentalCapacityOrVulnerableQuestions(ua, mode)
      )
    case PassportOrIdCardDetailsYesNoPage => ua =>
      yesNoNav(
        ua = ua,
        fromPage = PassportOrIdCardDetailsYesNoPage,
        yesCall = amendRts.PassportOrIdCardDetailsController.onPageLoad(),
        noCall = navigateToMentalCapacityOrVulnerableQuestions(ua, mode)
      )
  }

  private def navigateAwayFromDateOfBirthPages(ua: UserAnswers, mode: Mode): Call = {
    if (ua.isTaxable) {
      rts.IncomeDiscretionYesNoController.onPageLoad(mode)
    } else {
      rts.CountryOfNationalityYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromShareOfIncomeQuestions(ua: UserAnswers, mode: Mode): Call = {
    if (ua.is5mldEnabled) {
      rts.CountryOfNationalityYesNoController.onPageLoad(mode)
    } else {
      rts.NationalInsuranceNumberYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromCountryOfNationalityQuestions(ua: UserAnswers, mode: Mode): Call = {
    if (ua.isTaxable) {
      rts.NationalInsuranceNumberYesNoController.onPageLoad(mode)
    } else {
      rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromCountryOfResidencyQuestions(ua: UserAnswers, mode: Mode): Call = {
    (ua.get(NationalInsuranceNumberYesNoPage), ua.isTaxable) match {
      case (Some(true), _) => rts.MentalCapacityYesNoController.onPageLoad(mode)
      case (_, true) => rts.AddressYesNoController.onPageLoad(mode)
      case _ => rts.MentalCapacityYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromNinoYesNoQuestion(ua: UserAnswers, mode: Mode): Call = {
    if (ua.is5mldEnabled) {
      rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    } else {
      rts.AddressYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromNinoQuestion(ua: UserAnswers, mode: Mode): Call = {
    if (ua.is5mldEnabled) {
      rts.CountryOfResidenceYesNoController.onPageLoad(mode)
    } else {
      rts.VPE1FormYesNoController.onPageLoad(mode)
    }
  }

  private def navigateAwayFromMentalCapacityPage(ua: UserAnswers, mode: Mode): Call = {
    if (ua.isTaxable) {
      rts.VPE1FormYesNoController.onPageLoad(mode)
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

  private def navigateToMentalCapacityOrVulnerableQuestions(ua: UserAnswers, mode: Mode): Call = {
    if (ua.is5mldEnabled) {
      rts.MentalCapacityYesNoController.onPageLoad(mode)
    } else {
      rts.VPE1FormYesNoController.onPageLoad(mode)
    }
  }

  private def trustTypeNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => ua => trustTypeNav(mode, ua)
  }

  private def trustTypeNav(mode: Mode, ua: UserAnswers): Call = ua.trustType match {
    case Some(TypeOfTrust.EmployeeRelated) => rts.RoleInCompanyController.onPageLoad(mode)
    case _ => rts.DateOfBirthYesNoController.onPageLoad(mode)
  }

  private def modeNavigation(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
    mode match {
      case NormalMode => {
        case VPE1FormYesNoPage => _ =>
          addRts.StartDateController.onPageLoad()
        case UkAddressPage | NonUkAddressPage => _ =>
          addRts.PassportDetailsYesNoController.onPageLoad()
      }
      case CheckMode => {
        case VPE1FormYesNoPage => ua =>
          modeNav(ua)
        case UkAddressPage | NonUkAddressPage => _ =>
          amendRts.PassportOrIdCardDetailsYesNoController.onPageLoad()
      }
    }
  }

  private def modeNav(answers: UserAnswers) : Call = {
    answers.get(IndexPage) match {
      case None =>
        controllers.routes.SessionExpiredController.onPageLoad()
      case Some(x) =>
        amendRts.CheckDetailsController.renderFromUserAnswers(x)
    }
  }

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) orElse
      yesNoNavigation(mode) orElse
      trustTypeNavigation(mode) orElse
      modeNavigation(mode)
}
