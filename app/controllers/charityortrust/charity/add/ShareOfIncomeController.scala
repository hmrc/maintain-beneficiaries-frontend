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

package controllers.charityortrust.charity.add

import config.annotations.AddCharityBeneficiary
import connectors.TrustConnector
import controllers.actions.StandardActionSets
import controllers.actions.charity.NameRequiredAction
import forms.IncomePercentageFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.charityortrust.charity.ShareOfIncomePage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.charityortrust.charity.add.ShareOfIncomeView

import scala.concurrent.{ExecutionContext, Future}

class ShareOfIncomeController @Inject()(
                                         val controllerComponents: MessagesControllerComponents,
                                         standardActionSets: StandardActionSets,
                                         formProvider: IncomePercentageFormProvider,
                                         connector: TrustConnector,
                                         view: ShareOfIncomeView,
                                         trustService: TrustService,
                                         repository: PlaybackRepository,
                                         @AddCharityBeneficiary navigator: Navigator,
                                         nameAction: NameRequiredAction
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Int] = formProvider.withPrefix("charityBeneficiary.shareOfIncome")

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ShareOfIncomePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.beneficiaryName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.beneficiaryName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ShareOfIncomePage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ShareOfIncomePage, updatedAnswers))
      )
  }
}
