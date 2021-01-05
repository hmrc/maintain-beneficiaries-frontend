/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.individualbeneficiary.amend

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.actions.individual.NameRequiredAction
import extractors.IndividualBeneficiaryExtractor
import handlers.ErrorHandler
import javax.inject.Inject
import models.UserAnswers
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.mappers.IndividualBeneficiaryMapper
import utils.print.IndividualBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.individualbeneficiary.amend.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        service: TrustService,
                                        connector: TrustConnector,
                                        val appConfig: FrontendAppConfig,
                                        playbackRepository: PlaybackRepository,
                                        printHelper: IndividualBeneficiaryPrintHelper,
                                        mapper: IndividualBeneficiaryMapper,
                                        nameAction: NameRequiredAction,
                                        extractor: IndividualBeneficiaryExtractor,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val provisional = false

  private def render(userAnswers: UserAnswers,
                     index: Int,
                     name: String
                    )(implicit request: Request[AnyContent]): Result = {
    val section: AnswerSection = printHelper(userAnswers, provisional, name)
    Ok(view(section, index))
  }

  def extractAndRender(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      service.getIndividualBeneficiary(request.userAnswers.utr, index) flatMap {
        individual =>
          val extractedAnswers = extractor(request.userAnswers, individual, index)
          for {
            extractedF <- Future.fromTry(extractedAnswers)
            _ <- playbackRepository.set(extractedF)
          } yield render(extractedF, index, individual.name.displayName)
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
            s" error showing the user the check answers for individual beneficiary $index ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def renderFromUserAnswers(index: Int) : Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>
      render(request.userAnswers, index, request.beneficiaryName)
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      mapper(request.userAnswers, provisional).map {
        beneficiary =>
          connector.amendIndividualBeneficiary(request.userAnswers.utr, index, beneficiary).map(_ =>
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
          )
      }.getOrElse {
        logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
          s" error mapping user answers to individual beneficiary $index, isNew: $provisional")

        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }
}
