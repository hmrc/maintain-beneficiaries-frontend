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

package controllers

import config.FrontendAppConfig
import connectors.TrustStoreConnector
import controllers.actions.StandardActionSets
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import handlers.ErrorHandler
import javax.inject.Inject
import models.beneficiaries.Beneficiaries
import models.{AddABeneficiary, Enumerable}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddABeneficiaryViewHelper
import views.html.{AddABeneficiaryView, AddABeneficiaryYesNoView, MaxedOutBeneficiariesView}

import scala.concurrent.{ExecutionContext, Future}

class AddABeneficiaryController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           repository: PlaybackRepository,
                                           trustService: TrustService,
                                           standardActionSets: StandardActionSets,
                                           addAnotherFormProvider: AddABeneficiaryFormProvider,
                                           yesNoFormProvider: YesNoFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           addAnotherView: AddABeneficiaryView,
                                           yesNoView: AddABeneficiaryYesNoView,
                                           completeView: MaxedOutBeneficiariesView,
                                           val appConfig: FrontendAppConfig,
                                           trustStoreConnector: TrustStoreConnector,
                                            errorHandler: ErrorHandler
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val addAnotherForm : Form[AddABeneficiary] = addAnotherFormProvider()

  val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addABeneficiaryYesNo")

  private val logger = Logger(getClass)

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      (for {
        beneficiaries <- trustService.getBeneficiaries(request.userAnswers.utr)
        updatedAnswers <- Future.fromTry(request.userAnswers.cleanup)
        _ <- repository.set(updatedAnswers)
      } yield {
        beneficiaries match {
          case Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil) =>
            logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
              s" asking user if they want to add a beneficiary, no beneficiaries in trust")

            Ok(yesNoView(yesNoForm))
          case all: Beneficiaries =>

            val beneficiaryRows = new AddABeneficiaryViewHelper(all).rows

            if (beneficiaries.nonMaxedOutOptions.isEmpty) {
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
                s" showing user their beneficiaries, maximum number of beneficiaries reached")

              Ok(completeView(
                inProgressBeneficiaries = beneficiaryRows.inProgress,
                completeBeneficiaries = beneficiaryRows.complete,
                heading = all.addToHeading
              ))
            } else {
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
                s" showing user their beneficiaries, user is not at the maximum beneficiaries")

              Ok(addAnotherView(
                form = addAnotherForm,
                inProgressBeneficiaries = beneficiaryRows.inProgress,
                completeBeneficiaries = beneficiaryRows.complete,
                heading = all.addToHeading,
                maxedOut = beneficiaries.maxedOutOptions.map(x => x.messageKey)
              ))
            }
        }
      }) recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
            s" unable to show add to page due to an error getting beneficiaries from trusts ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def submitOne(): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors)))
        },
        addNow => {
          if (addNow) {

            for {
                updatedAnswers <- Future.fromTry(request.userAnswers.cleanup)
                _ <- repository.set(updatedAnswers)
              } yield Redirect(controllers.routes.AddNowController.onPageLoad())
          } else {
            for {
              _ <- trustStoreConnector.setTaskComplete(request.userAnswers.utr)
            } yield {
              Redirect(appConfig.maintainATrustOverview)
            }
          }
        }
      )
  }

  def submitAnother(): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      trustService.getBeneficiaries(request.userAnswers.utr).flatMap { beneficiaries =>
        addAnotherForm.bindFromRequest().fold(
          (formWithErrors: Form[_]) => {

            val rows = new AddABeneficiaryViewHelper(beneficiaries).rows

            Future.successful(BadRequest(
              addAnotherView(
                formWithErrors,
                rows.inProgress,
                rows.complete,
                beneficiaries.addToHeading,
                maxedOut = beneficiaries.maxedOutOptions.map(x => x.messageKey)
              )
            ))
          },
          {
            case AddABeneficiary.YesNow =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.cleanup)
                _ <- repository.set(updatedAnswers)
              } yield Redirect(controllers.routes.AddNowController.onPageLoad())

            case AddABeneficiary.YesLater =>
              Future.successful(Redirect(appConfig.maintainATrustOverview))

            case AddABeneficiary.NoComplete =>
              for {
                _ <- trustStoreConnector.setTaskComplete(request.userAnswers.utr)
              } yield {
                Redirect(appConfig.maintainATrustOverview)
              }
          }
        )
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.utr}]" +
            s" unable add a new beneficiary due to an error getting beneficiaries from trusts ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def submitComplete(): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      for {
        _ <- trustStoreConnector.setTaskComplete(request.userAnswers.utr)
      } yield {
        Redirect(appConfig.maintainATrustOverview)
      }
  }
}
