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

package controllers

import controllers.actions._
import forms.AddBeneficiaryTypeFormProvider
import handlers.ErrorHandler
import models.beneficiaries.TypeOfBeneficiaryToAdd
import models.beneficiaries.TypeOfBeneficiaryToAdd._
import navigation.BeneficiaryNavigator
import pages.AddNowPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.AddNowView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddNowController @Inject()(
                                  override val messagesApi: MessagesApi,
                                  standardActionSets: StandardActionSets,
                                  val controllerComponents: MessagesControllerComponents,
                                  view: AddNowView,
                                  formProvider: AddBeneficiaryTypeFormProvider,
                                  repository: PlaybackRepository,
                                  trustService: TrustService,
                                  errorHandler: ErrorHandler,
                                  navigator: BeneficiaryNavigator
                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[TypeOfBeneficiaryToAdd] = formProvider()

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trustService.getBeneficiaries(request.userAnswers.identifier).map {
        beneficiaries =>
          val preparedForm = request.userAnswers.get(AddNowPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, beneficiaries.nonMaxedOutOptions))

      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" unable add a new beneficiary due to an error getting beneficiaries from trusts ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate.toString))
      }
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trustService.getBeneficiaries(request.userAnswers.identifier).flatMap {
        beneficiaries =>

          form.bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, beneficiaries.nonMaxedOutOptions))),

            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddNowPage, value))
                _ <- repository.set(updatedAnswers)
              } yield Redirect(navigator.addBeneficiaryNowRoute(value))
          )
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" unable add a new beneficiary due to an error getting beneficiaries from trusts ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate.toString))
      }
  }
}
