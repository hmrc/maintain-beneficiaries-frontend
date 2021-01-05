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

package controllers.companyoremploymentrelated.employment

import config.annotations.EmploymentRelatedBeneficiary
import controllers.actions.StandardActionSets
import forms.EmploymentRelatedBeneficiaryDescriptionFormProvider
import javax.inject.Inject
import models.{Description, Mode}
import navigation.Navigator
import pages.companyoremploymentrelated.employment.DescriptionPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.companyoremploymentrelated.employment.DescriptionView

import scala.concurrent.{ExecutionContext, Future}

class DescriptionController @Inject()(
                                       val controllerComponents: MessagesControllerComponents,
                                       standardActionSets: StandardActionSets,
                                       formProvider: EmploymentRelatedBeneficiaryDescriptionFormProvider,
                                       view: DescriptionView,
                                       repository: PlaybackRepository,
                                       @EmploymentRelatedBeneficiary navigator: Navigator
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Description] = formProvider.withPrefix("employmentBeneficiary.description")

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(DescriptionPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))

  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DescriptionPage, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(DescriptionPage, mode, updatedAnswers))
      )
  }
}
