/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.actions._
import controllers.actions.individual.NameRequiredAction
import forms.PassportDetailsFormProvider
import models.Mode
import models.beneficiaries.Beneficiaries
import navigation.Navigator
import pages.individualbeneficiary.PassportDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import views.html.individualbeneficiary.PassportDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PassportDetailsController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           sessionRepository: PlaybackRepository,
                                           trustService: TrustService,
                                           @IndividualBeneficiary navigator: Navigator,
                                           standardActionSets: StandardActionSets,
                                           nameAction: NameRequiredAction,
                                           formProvider: PassportDetailsFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: PassportDetailsView,
                                           val countryOptions: CountryOptions
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form(beneficiaries: Beneficiaries) = formProvider.withPrefix("individualBeneficiary", beneficiaries)

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      trustService.getBeneficiaries(request.userAnswers.identifier).map { beneficiaries =>
        val preparedForm = request.userAnswers.get(PassportDetailsPage) match {
          case None => form(beneficiaries)
          case Some(value) => form(beneficiaries).fill(value)
        }

        Ok(view(preparedForm, mode, countryOptions.options(), request.beneficiaryName))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      trustService.getBeneficiaries(request.userAnswers.identifier).flatMap { beneficiaries =>
        form(beneficiaries).bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, countryOptions.options(), request.beneficiaryName))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PassportDetailsPage, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield {
              Redirect(navigator.nextPage(PassportDetailsPage, mode, updatedAnswers))
            }
        )
      }
  }
}
