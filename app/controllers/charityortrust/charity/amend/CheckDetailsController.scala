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

package controllers.charityortrust.charity.amend

import config.FrontendAppConfig
import connectors.TrustConnector
import controllers.actions._
import controllers.actions.charity.NameRequiredAction
import extractors.CharityBeneficiaryExtractor
import handlers.ErrorHandler
import models.BeneficiaryType.CharityBeneficiary
import models.{CheckMode, UserAnswers}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.mappers.CharityBeneficiaryMapper
import utils.print.CharityBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.OutOfBoundsPageNotFoundView
import views.html.charityortrust.charity.amend.CheckDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  standardActionSets: StandardActionSets,
  val controllerComponents: MessagesControllerComponents,
  view: CheckDetailsView,
  val outOfBoundsView: OutOfBoundsPageNotFoundView,
  service: TrustService,
  connector: TrustConnector,
  val appConfig: FrontendAppConfig,
  playbackRepository: PlaybackRepository,
  printHelper: CharityBeneficiaryPrintHelper,
  mapper: CharityBeneficiaryMapper,
  nameAction: NameRequiredAction,
  extractor: CharityBeneficiaryExtractor,
  val errorHandler: ErrorHandler
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging with IndexAndGenericExceptionRecovery {

  private val provisional: Boolean = false

  private def render(userAnswers: UserAnswers, index: Int, name: String)(implicit
    request: Request[AnyContent]
  ): Result = {
    val section: AnswerSection = printHelper(userAnswers, provisional, name)
    Ok(view(Seq(section), index))
  }

  def extractAndRender(index: Int): Action[AnyContent] = extractAndDoAction(index, redirect = false)

  def extractAndRedirect(index: Int): Action[AnyContent] = extractAndDoAction(index, redirect = true)

  private def extractAndDoAction(index: Int, redirect: Boolean): Action[AnyContent] =
    standardActionSets.verifiedForUtr.async { implicit request =>
      (for {
        charity         <- service.getCharityBeneficiary(request.userAnswers.identifier, index)
        extractedAnswers = extractor(request.userAnswers, charity, index)
        extractedF      <- Future.fromTry(extractedAnswers)
        _               <- playbackRepository.set(extractedF)
      } yield
        if (charity.utr.isDefined) {
          Redirect(controllers.charityortrust.charity.amend.routes.CheckDetailsUtrController.onPageLoad())
        } else {
          if (redirect) {
            Redirect(controllers.charityortrust.charity.routes.NameController.onPageLoad(CheckMode).url)
          } else {
            render(extractedF, index, charity.name)
          }
        })
        .recoverWith {
          recoverIndexAndGenericException(CharityBeneficiary, index, request.userAnswers.identifier, "onPageLoad")
        }
    }

  def renderFromUserAnswers(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>
      render(request.userAnswers, index, request.beneficiaryName)
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async { implicit request =>
    mapper(request.userAnswers)
      .map { beneficiary =>
        connector
          .amendCharityBeneficiary(request.userAnswers.identifier, index, beneficiary)
          .map(_ => Redirect(controllers.routes.AddABeneficiaryController.onPageLoad()))
      }
      .getOrElse {
        logger.error(
          s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" error mapping user answers to charity beneficiary $index, isNew: $provisional"
        )

        errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
      }
  }

}
