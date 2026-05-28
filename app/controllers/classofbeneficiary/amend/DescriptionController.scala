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

package controllers.classofbeneficiary.amend

import connectors.TrustConnector
import controllers.actions.{IndexAndGenericExceptionRecovery, StandardActionSets}
import forms.DescriptionFormProvider
import handlers.ErrorHandler
import models.BeneficiaryType.{ClassOfBeneficiary => ClassOfBeneficiaryType}
import models.beneficiaries.ClassOfBeneficiary
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OutOfBoundsPageNotFoundView
import views.html.classofbeneficiary.amend.DescriptionView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DescriptionController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  standardActionSets: StandardActionSets,
  formProvider: DescriptionFormProvider,
  connector: TrustConnector,
  view: DescriptionView,
  val outOfBoundsView: OutOfBoundsPageNotFoundView,
  trustService: TrustService,
  val errorHandler: ErrorHandler
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging with IndexAndGenericExceptionRecovery {

  val form: Form[String] = formProvider.withPrefix("classOfBeneficiary.description", 56)

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async { implicit request =>
    trustService
      .getUnidentifiedBeneficiary(request.userAnswers.identifier, index)
      .map {
        case ClassOfBeneficiary(description, _, _) => Ok(view(form.fill(description), index))
        case _                                     => Ok(view(form, index))
      }
      .recoverWith {
        recoverIndexAndGenericException(ClassOfBeneficiaryType, index, request.userAnswers.identifier, "onPageLoad")
      }
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, index))),
        value =>
          connector
            .amendClassOfBeneficiary(request.userAnswers.identifier, index, value)
            .map(_ => Redirect(controllers.routes.AddABeneficiaryController.onPageLoad()))
      )
      .recoverWith {
        recoverIndexAndGenericException(ClassOfBeneficiaryType, index, request.userAnswers.identifier, "onSubmit")
      }
  }

}
