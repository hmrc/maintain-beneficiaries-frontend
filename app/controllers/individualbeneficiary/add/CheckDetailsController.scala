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

package controllers.individualbeneficiary.add

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.classofbeneficiary.actions.DescriptionRequiredAction
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.mappers.IndividualBeneficiaryMapper
import utils.print.IndividualBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.individualbeneficiary.add.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        connector: TrustConnector,
                                        val appConfig: FrontendAppConfig,
                                        playbackRepository: PlaybackRepository,
                                        printHelper: IndividualBeneficiaryPrintHelper,
                                        mapper: IndividualBeneficiaryMapper,
                                        descriptionAction: DescriptionRequiredAction
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>

      val section: AnswerSection = printHelper(request.userAnswers, request.description)
      Ok(view(section))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      mapper(request.userAnswers) match {
        case None =>
          Future.successful(InternalServerError)
        case Some(beneficiary) =>
          connector.addIndividualBeneficiary(request.userAnswers.utr, beneficiary).map(_ =>
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
          )
      }
  }
}
