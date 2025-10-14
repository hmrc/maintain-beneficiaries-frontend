/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.companyoremploymentrelated.employment

import config.annotations.EmploymentRelatedBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.employment.NameRequiredAction
import forms.DateAddedToTrustFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.companyoremploymentrelated.employment.StartDatePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.companyoremploymentrelated.employment.StartDateView

import scala.concurrent.{ExecutionContext, Future}

class StartDateController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     playbackRepository: PlaybackRepository,
                                     @EmploymentRelatedBeneficiary navigator: Navigator,
                                     standardActionSets: StandardActionSets,
                                     nameAction: NameRequiredAction,
                                     formProvider: DateAddedToTrustFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: StartDateView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction) {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("employmentBeneficiary.startDate", request.userAnswers.whenTrustSetup)

      val preparedForm = request.userAnswers.get(StartDatePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.beneficiaryName))
  }

  def onSubmit(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction).async {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("employmentBeneficiary.startDate", request.userAnswers.whenTrustSetup)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.beneficiaryName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(StartDatePage, value))
            _              <- playbackRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(StartDatePage, updatedAnswers))
      )
  }
}
