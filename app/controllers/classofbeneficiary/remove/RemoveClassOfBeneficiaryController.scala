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

package controllers.classofbeneficiary.remove

import controllers.actions.StandardActionSets
import forms.RemoveIndexFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.{BeneficiaryType, RemoveBeneficiary}
import pages.classofbeneficiary.RemoveYesNoPage
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.classofbeneficiary.remove.RemoveIndexView

import scala.concurrent.{ExecutionContext, Future}

class RemoveClassOfBeneficiaryController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    repository: PlaybackRepository,
                                                    standardActionSets: StandardActionSets,
                                                    trustService: TrustService,
                                                    formProvider: RemoveIndexFormProvider,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: RemoveIndexView,
                                                    errorHandler: ErrorHandler
                                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val logger = Logger(getClass)

  private def formRoute(index: Int): Call =
    controllers.classofbeneficiary.remove.routes.RemoveClassOfBeneficiaryController.onSubmit(index)

  private val messagesPrefix: String = "removeClassOfBeneficiary"

  private val form = formProvider.apply(messagesPrefix)

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      val preparedForm = request.userAnswers.get(RemoveYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      trustService.getUnidentifiedBeneficiary(request.userAnswers.utr, index).map {
        beneficiary =>
          Ok(view(messagesPrefix, preparedForm, index, beneficiary.description, formRoute(index)))
      } recoverWith {
        case _ =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
            s" error getting class of beneficiary $index from trusts service")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }

  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          trustService.getUnidentifiedBeneficiary(request.userAnswers.utr, index).map {
            beneficiary =>
              BadRequest(view(messagesPrefix, formWithErrors, index, beneficiary.description, formRoute(index)))
          }
        },
        value => {

          if (value) {

            trustService.getUnidentifiedBeneficiary(request.userAnswers.utr, index).flatMap {
              beneficiary =>
                if (beneficiary.provisional) {
                  trustService.removeBeneficiary(request.userAnswers.utr, RemoveBeneficiary(BeneficiaryType.ClassOfBeneficiary, index)).map { _ =>
                    logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
                      s" removed new class of beneficiary $index")
                    Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
                  }
                } else {
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(RemoveYesNoPage, value))
                    _ <- repository.set(updatedAnswers)
                  } yield {
                    Redirect(controllers.classofbeneficiary.remove.routes.WhenRemovedController.onPageLoad(index).url)
                  }
                }
            } recoverWith {
              case _ =>
                logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
                  s" error removing a class of beneficiary as could not get beneficiary $index from trusts service")

                Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
            }
          } else {
            Future.successful(Redirect(controllers.routes.AddABeneficiaryController.onPageLoad().url))
          }
        }
      )
  }
}
