/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.charityortrust.charity.remove

import controllers.actions.StandardActionSets
import forms.DateRemovedFromTrustFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.{BeneficiaryType, RemoveBeneficiary}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.charityortrust.charity.remove.WhenRemovedView

import scala.concurrent.{ExecutionContext, Future}

class WhenRemovedController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       standardActionSets: StandardActionSets,
                                       formProvider: DateRemovedFromTrustFormProvider,
                                       trust: TrustService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhenRemovedView,
                                       trustService: TrustService,
                                       errorHandler: ErrorHandler
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getCharityBeneficiary(request.userAnswers.identifier, index).map {
        beneficiary =>
          val form = formProvider.withPrefixAndEntityStartDate("charityBeneficiary.whenRemoved", beneficiary.entityStart)
          Ok(view(form, index, beneficiary.name))
      } recoverWith {
        case iobe: IndexOutOfBoundsException =>
          logger.warn(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" error showing the user the charity beneficiary to remove, problem getting other beneficiary $index from trusts service ${iobe.getMessage}: IndexOutOfBoundsException")

          Future.successful(Redirect(controllers.routes.AddABeneficiaryController.onPageLoad()))
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" error showing the user the charity beneficiary to remove, problem getting other beneficiary $index from trusts service ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getCharityBeneficiary(request.userAnswers.identifier, index).flatMap {
        beneficiary =>
          val form = formProvider.withPrefixAndEntityStartDate("charityBeneficiary.whenRemoved", beneficiary.entityStart)
          form.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(BadRequest(view(formWithErrors, index, beneficiary.name)))
            },
            value =>
              trustService.removeBeneficiary(request.userAnswers.identifier, RemoveBeneficiary(BeneficiaryType.CharityBeneficiary, index, value)).map { _ =>
                logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
                  s" removed existing charity beneficiary $index")
                Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
              }
          )
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" error removing a charity beneficiary as could not get beneficiary $index from trusts service ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }
}
