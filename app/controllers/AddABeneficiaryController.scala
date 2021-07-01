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

package controllers

import config.FrontendAppConfig
import connectors.TrustsStoreConnector
import controllers.actions.StandardActionSets
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import handlers.ErrorHandler
import models.beneficiaries.Beneficiaries
import models.{AddABeneficiary, Enumerable}
import navigation.BeneficiaryNavigator
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddABeneficiaryViewHelper
import views.html.{AddABeneficiaryView, AddABeneficiaryYesNoView, MaxedOutBeneficiariesView}

import javax.inject.Inject
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
                                           trustStoreConnector: TrustsStoreConnector,
                                           errorHandler: ErrorHandler,
                                           viewHelper: AddABeneficiaryViewHelper,
                                           navigator: BeneficiaryNavigator
                                         )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport with Enumerable.Implicits with Logging {

  private val addAnotherForm : Form[AddABeneficiary] = addAnotherFormProvider()
  private val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addABeneficiaryYesNo")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      (for {
        beneficiaries <- trustService.getBeneficiaries(request.userAnswers.identifier)
        updatedAnswers <- Future.fromTry(request.userAnswers.cleanup)
        _ <- repository.set(updatedAnswers)
      } yield {
        beneficiaries match {
          case Beneficiaries(Nil, Nil, Nil, Nil, Nil, Nil, Nil) =>
            logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
              s" asking user if they want to add a beneficiary, no beneficiaries in trust")

            Ok(yesNoView(yesNoForm))
          case _ =>

            val beneficiaryRows = viewHelper.rows(
              beneficiaries = beneficiaries,
              migratingFromNonTaxableToTaxable = updatedAnswers.migratingFromNonTaxableToTaxable,
              trustType = updatedAnswers.trustType
            )

            if (beneficiaries.nonMaxedOutOptions.isEmpty) {
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
                s" showing user their beneficiaries, maximum number of beneficiaries reached")

              Ok(completeView(
                inProgressBeneficiaries = beneficiaryRows.inProgress,
                completeBeneficiaries = beneficiaryRows.complete,
                heading = beneficiaries.addToHeading
              ))
            } else {
              logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
                s" showing user their beneficiaries, user is not at the maximum beneficiaries")

              Ok(addAnotherView(
                form = addAnotherForm,
                inProgressBeneficiaries = beneficiaryRows.inProgress,
                completeBeneficiaries = beneficiaryRows.complete,
                heading = beneficiaries.addToHeading,
                maxedOut = beneficiaries.maxedOutOptions.map(x => x.messageKey)
              ))
            }
        }
      }) recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
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
            submitComplete()(request)
          }
        }
      )
  }

  def submitAnother(): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      trustService.getBeneficiaries(request.userAnswers.identifier).flatMap { beneficiaries =>
        addAnotherForm.bindFromRequest().fold(
          (formWithErrors: Form[_]) => {

            val rows = viewHelper.rows(
              beneficiaries = beneficiaries,
              migratingFromNonTaxableToTaxable = request.userAnswers.migratingFromNonTaxableToTaxable,
              trustType = request.userAnswers.trustType
            )

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
              } yield Redirect(navigator.addBeneficiaryRoute(beneficiaries))

            case AddABeneficiary.YesLater =>
              Future.successful(Redirect(appConfig.maintainATrustOverview))

            case AddABeneficiary.NoComplete =>
              submitComplete()(request)
          }
        )
      } recoverWith {
        case e =>
          logger.error(s"[Session ID: ${utils.Session.id(hc)}][UTR: ${request.userAnswers.identifier}]" +
            s" unable add a new beneficiary due to an error getting beneficiaries from trusts ${e.getMessage}")

          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  def submitComplete(): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      for {
        beneficiaries <- trustService.getBeneficiaries(request.userAnswers.identifier)
        _ <- if (beneficiaries.isEmpty) {
          Future.successful(())
        } else {
          trustStoreConnector.setTaskComplete(request.userAnswers.identifier)
        }
      } yield {
        Redirect(appConfig.maintainATrustOverview)
      }
  }
}
