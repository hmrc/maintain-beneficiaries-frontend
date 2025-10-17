/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.individualbeneficiary

import config.annotations.IndividualBeneficiary
import connectors.TrustConnector
import controllers.actions.StandardActionSets
import controllers.actions.individual.NameRequiredAction
import forms.IncomePercentageFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.individualbeneficiary.IncomePercentagePage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.individualbeneficiary.IncomePercentageView

import scala.concurrent.{ExecutionContext, Future}

class IncomePercentageController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                standardActionSets: StandardActionSets,
                                nameAction: NameRequiredAction,
                                formProvider: IncomePercentageFormProvider,
                                connector: TrustConnector,
                                view: IncomePercentageView,
                                trustService: TrustService,
                                repository: PlaybackRepository,
                                @IndividualBeneficiary navigator: Navigator
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Int] = formProvider.withPrefix("individualBeneficiary.shareOfIncome")

  def onPageLoad(mode: Mode): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(IncomePercentagePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, request.beneficiaryName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, request.beneficiaryName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IncomePercentagePage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IncomePercentagePage, mode, updatedAnswers))
      )
  }
}
