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

package controllers.other.amend

import config.{ErrorHandler, FrontendAppConfig}
import connectors.TrustConnector
import controllers.actions._
import controllers.actions.other.DescriptionRequiredAction
import extractors.OtherBeneficiaryExtractor
import javax.inject.Inject
import models.UserAnswers
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.mappers.OtherBeneficiaryMapper
import utils.print.OtherBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.other.amend.CheckDetailsView

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
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val logger = Logger(getClass)


  private def render(userAnswers: UserAnswers,
                     index: Int,
                     name: String)
                    (implicit request: Request[AnyContent]): Result=
  {
    val section: AnswerSection = printHelper(userAnswers, provisional = false, name)
    Ok(view(section, index))
  }

  def extractAndRender(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      service.getOtherBeneficiary(request.userAnswers.utr, index) flatMap {
        other =>
          val extractedAnswers = extractor(request.userAnswers, other, index)
          for {
            extractedF <- Future.fromTry(extractedAnswers)
            _ <- playbackRepository.set(extractedF)
          } yield {
              render(extractedF, index, other.description)
          }
      } recoverWith {
        case _ =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
            s" error showing the user the check answers for other beneficiary $index")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def renderFromUserAnswers(index: Int) : Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>
      render(request.userAnswers, index, request.description)
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      mapper(request.userAnswers).map {
        beneficiary =>
          connector.amendOtherBeneficiary(request.userAnswers.utr, index, beneficiary).map(_ =>
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
          )
      }.getOrElse {
        logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
          s" error mapping user answers to other beneficiary $index")

        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }
}
