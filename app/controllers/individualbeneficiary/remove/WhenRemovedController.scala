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

package controllers.individualbeneficiary.remove

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
import views.html.individualbeneficiary.remove.WhenRemovedView

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

      trust.getIndividualBeneficiary(request.userAnswers.utr, index).map {
        beneficiary =>
          val form = formProvider.withPrefixAndEntityStartDate("individualBeneficiary.whenRemoved", beneficiary.entityStart)
          Ok(view(form, index, beneficiary.name.displayName))
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
            s" error showing the user the individual beneficiary to remove, problem getting individual beneficiary $index from trusts service ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getIndividualBeneficiary(request.userAnswers.utr, index).flatMap {
        beneficiary =>
          val form = formProvider.withPrefixAndEntityStartDate("individualBeneficiary.whenRemoved", beneficiary.entityStart)
          form.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(BadRequest(view(formWithErrors, index, beneficiary.name.displayName)))
            },
            value => {
              trustService.removeBeneficiary(request.userAnswers.utr, RemoveBeneficiary(BeneficiaryType.IndividualBeneficiary, index, value)).map { _ =>
                logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
                  s" removed existing individual beneficiary $index")
                Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
              }
            }
          )
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
            s" error removing an individual beneficiary as could not get beneficiary $index from trusts service ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }
}
