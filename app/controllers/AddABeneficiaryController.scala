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
import javax.inject.Inject
import models.beneficiaries.Beneficiaries
import models.{AddABeneficiary, Enumerable}
import navigation.Navigator
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddABeneficiaryViewHelper
import views.html.{AddABeneficiaryView, AddABeneficiaryYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddABeneficiaryController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: PlaybackRepository,
                                       navigator: Navigator,
                                       trust: TrustService,
                                       standardActionSets: StandardActionSets,
                                       addAnotherFormProvider: AddABeneficiaryFormProvider,
                                       yesNoFormProvider: YesNoFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddABeneficiaryView,
                                       yesNoView: AddABeneficiaryYesNoView,
                                       val appConfig: FrontendAppConfig,
                                       trustStoreConnector: TrustStoreConnector
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val addAnotherForm : Form[AddABeneficiary] = addAnotherFormProvider()

  val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addABeneficiaryYesNo")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getBeneficiaries(request.userAnswers.utr) map {
        case Beneficiaries(Nil, Nil, Nil, Nil, Nil) =>
          Ok(yesNoView(yesNoForm))
        case all: Beneficiaries =>

          val beneficiaries = new AddABeneficiaryViewHelper(all).rows

          Ok(addAnotherView(
            form = addAnotherForm,
            inProgressBeneficiaries = beneficiaries.inProgress,
            completeBeneficiaries = beneficiaries.complete,
            heading = all.addToHeading
          ))
      }
  }

  def submitOne(): Action[AnyContent] = standardActionSets.identifiedUserWithData {
    implicit request =>
      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          BadRequest(yesNoView(formWithErrors))
        },
        addNow => {
          if (addNow) {
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
          } else {
            Redirect(appConfig.maintainATrustOverview)
          }
        }
      )
  }

  def submitAnother(): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      trust.getBeneficiaries(request.userAnswers.utr).flatMap { beneficiaries =>
        addAnotherForm.bindFromRequest().fold(
          (formWithErrors: Form[_]) => {

            val rows = new AddABeneficiaryViewHelper(beneficiaries).rows

            Future.successful(BadRequest(
              addAnotherView(
                formWithErrors,
                rows.inProgress,
                rows.complete,
                beneficiaries.addToHeading
              )
            ))
          },
          {
            case AddABeneficiary.YesNow =>
              Future.successful(Redirect(controllers.routes.AddABeneficiaryController.onPageLoad()))
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
      }
  }
}
