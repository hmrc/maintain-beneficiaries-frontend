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

package controllers.charityortrust.charity

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.actions.charity.NameRequiredAction
import handlers.ErrorHandler
import javax.inject.Inject
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.mappers.CharityBeneficiaryMapper
import utils.print.CharityBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.charityortrust.charity.CheckDetailsView

import scala.concurrent.ExecutionContext

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        connector: TrustConnector,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: CharityBeneficiaryPrintHelper,
                                        mapper: CharityBeneficiaryMapper,
                                        nameAction: NameRequiredAction,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val provisional: Boolean = true

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      val section: AnswerSection = printHelper(request.userAnswers, provisional, request.beneficiaryName)
      Ok(view(Seq(section)))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      mapper(request.userAnswers) match {
        case None =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" error in mapping user answers to CharityBeneficiary")

          errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
        case Some(beneficiary) =>
          connector.addCharityBeneficiary(request.userAnswers.identifier, beneficiary).map(_ =>
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
          )
      }
  }

}
