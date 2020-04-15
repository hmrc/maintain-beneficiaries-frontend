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

package controllers.charityortrust.trust.amend

import config.{ErrorHandler, FrontendAppConfig}
import connectors.TrustConnector
import controllers.actions._
import controllers.actions.trust.NameRequiredAction
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.mappers.TrustBeneficiaryMapper
import utils.print.AmendTrustBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.charityortrust.trust.amend.CheckDetailsUtrView

import scala.concurrent.ExecutionContext

class CheckDetailsUtrController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsUtrView,
                                        connector: TrustConnector,
                                        val appConfig: FrontendAppConfig,
                                        playbackRepository: PlaybackRepository,
                                        printHelper: AmendTrustBeneficiaryPrintHelper,
                                        mapper: TrustBeneficiaryMapper,
                                        nameAction: NameRequiredAction,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      val section: AnswerSection = printHelper(request.userAnswers, request.beneficiaryName)
      Ok(view(section, request.beneficiaryName))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request =>
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
  }
}
