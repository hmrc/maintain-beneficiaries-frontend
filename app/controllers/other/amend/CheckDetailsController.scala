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

package controllers.other.amend

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.actions.other.DescriptionRequiredAction
import extractors.OtherBeneficiaryExtractor
import handlers.ErrorHandler
import models.{CheckMode, UserAnswers}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.mappers.OtherBeneficiaryMapper
import utils.print.OtherBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.other.amend.CheckDetailsView

import javax.inject.Inject
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
                                        printHelper: OtherBeneficiaryPrintHelper,
                                        mapper: OtherBeneficiaryMapper,
                                        descriptionAction: DescriptionRequiredAction,
                                        extractor: OtherBeneficiaryExtractor,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val provisional: Boolean = false

  private def render(userAnswers: UserAnswers,
                     index: Int,
                     name: String)
                    (implicit request: Request[AnyContent]): Result = {
    val section: AnswerSection = printHelper(userAnswers, provisional, name)
    Ok(view(Seq(section), index))
  }

  def extractAndRender(index: Int): Action[AnyContent] = extractAndDoAction(index, redirect = false)

  def extractAndRedirect(index: Int): Action[AnyContent] = extractAndDoAction(index, redirect = true)

  private def extractAndDoAction(index: Int, redirect: Boolean): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      service.getOtherBeneficiary(request.userAnswers.identifier, index) flatMap {
        other =>
          val extractedAnswers = extractor(request.userAnswers, other, index)
          for {
            extractedF <- Future.fromTry(extractedAnswers)
            _ <- playbackRepository.set(extractedF)
          } yield {
            if (redirect) {
              Redirect(controllers.other.routes.DescriptionController.onPageLoad(CheckMode))
            } else {
              render(extractedF, index, other.description)
            }
          }
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" error getting other beneficiary $index ${e.getMessage}")

          errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
      }
  }

  def renderFromUserAnswers(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>
      render(request.userAnswers, index, request.description)
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      mapper(request.userAnswers).map {
        beneficiary =>
          connector.amendOtherBeneficiary(request.userAnswers.identifier, index, beneficiary).map(_ =>
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
          )
      }.getOrElse {
        logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
          s" error mapping user answers to other beneficiary $index, isNew: $provisional")

        errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
      }
  }
}
