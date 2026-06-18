/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.charityortrust.trust.remove

import controllers.actions.{IndexAndGenericExceptionRecovery, StandardActionSets}
import forms.RemoveIndexFormProvider
import handlers.ErrorHandler
import models.BeneficiaryType.TrustBeneficiary
import models.{BeneficiaryType, RemoveBeneficiary}
import pages.charityortrust.trust.RemoveYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OutOfBoundsPageNotFoundView
import views.html.charityortrust.trust.remove.RemoveIndexView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveTrustBeneficiaryController @Inject() (
  override val messagesApi: MessagesApi,
  repository: PlaybackRepository,
  standardActionSets: StandardActionSets,
  trustService: TrustService,
  formProvider: RemoveIndexFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveIndexView,
  val outOfBoundsView: OutOfBoundsPageNotFoundView,
  val errorHandler: ErrorHandler
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging with IndexAndGenericExceptionRecovery {

  private val messagesPrefix: String = "removeTrustBeneficiaryYesNo"

  private val form = formProvider.apply(messagesPrefix)

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.identifiedUserWithData.async { implicit request =>
    val preparedForm = request.userAnswers.get(RemoveYesNoPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    trustService
      .getTrustBeneficiary(request.userAnswers.identifier, index)
      .map { beneficiary =>
        Ok(view(preparedForm, index, beneficiary.name))
      }
      .recoverWith {
        recoverIndexAndGenericException(TrustBeneficiary, index, request.userAnswers.identifier, "onPageLoad")
      }

  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.identifiedUserWithData.async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          trustService.getTrustBeneficiary(request.userAnswers.identifier, index).map { beneficiary =>
            BadRequest(view(formWithErrors, index, beneficiary.name))
          },
        value =>
          if (value) {

            trustService.getTrustBeneficiary(request.userAnswers.identifier, index).flatMap { beneficiary =>
              if (beneficiary.provisional) {
                trustService
                  .removeBeneficiary(
                    request.userAnswers.identifier,
                    RemoveBeneficiary(BeneficiaryType.TrustBeneficiary, index)
                  )
                  .map { _ =>
                    logger.info(
                      s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
                        s" removed new trust beneficiary $index"
                    )
                    Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
                  }
              } else {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(RemoveYesNoPage, value))
                  _              <- repository.set(updatedAnswers)
                } yield Redirect(
                  controllers.charityortrust.trust.remove.routes.WhenRemovedController.onPageLoad(index).url
                )
              }
            }
          } else {
            Future.successful(Redirect(controllers.routes.AddABeneficiaryController.onPageLoad().url))
          }
      )
      .recoverWith {
        recoverIndexAndGenericException(TrustBeneficiary, index, request.userAnswers.identifier, "onSubmit")
      }
  }

}
