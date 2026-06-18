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
import forms.DateRemovedFromTrustFormProvider
import handlers.ErrorHandler
import models.BeneficiaryType.TrustBeneficiary
import models.{BeneficiaryType, RemoveBeneficiary}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OutOfBoundsPageNotFoundView
import views.html.charityortrust.trust.remove.WhenRemovedView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhenRemovedController @Inject() (
  override val messagesApi: MessagesApi,
  standardActionSets: StandardActionSets,
  formProvider: DateRemovedFromTrustFormProvider,
  trust: TrustService,
  val controllerComponents: MessagesControllerComponents,
  view: WhenRemovedView,
  val outOfBoundsView: OutOfBoundsPageNotFoundView,
  trustService: TrustService,
  val errorHandler: ErrorHandler
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging with IndexAndGenericExceptionRecovery {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async { implicit request =>
    trust
      .getTrustBeneficiary(request.userAnswers.identifier, index)
      .map { beneficiary =>
        val form = formProvider.withPrefixAndEntityStartDate("trustBeneficiary.whenRemoved", beneficiary.entityStart)
        Ok(view(form, index, beneficiary.name))
      }
      .recoverWith {
        recoverIndexAndGenericException(TrustBeneficiary, index, request.userAnswers.identifier, "onPageLoad")
      }
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async { implicit request =>
    trust
      .getTrustBeneficiary(request.userAnswers.identifier, index)
      .flatMap { beneficiary =>
        val form = formProvider.withPrefixAndEntityStartDate("trustBeneficiary.whenRemoved", beneficiary.entityStart)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, index, beneficiary.name))),
            value =>
              trustService
                .removeBeneficiary(
                  request.userAnswers.identifier,
                  RemoveBeneficiary(BeneficiaryType.TrustBeneficiary, index, value)
                )
                .map { _ =>
                  logger.info(
                    s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
                      s" removed existing trust beneficiary $index"
                  )
                  Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
                }
          )
      }
      .recoverWith {
        recoverIndexAndGenericException(TrustBeneficiary, index, request.userAnswers.identifier, "onSubmit")
      }
  }

}
