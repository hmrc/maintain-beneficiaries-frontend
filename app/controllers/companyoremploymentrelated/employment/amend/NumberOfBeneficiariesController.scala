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

package controllers.companyoremploymentrelated.employment.amend

import config.annotations.AmendEmploymentRelatedBeneficiary
import controllers.actions.StandardActionSets
import forms.NumberOfBeneficiariesFormProvider
import javax.inject.Inject
import models.HowManyBeneficiaries
import navigation.Navigator
import pages.companyoremploymentrelated.employment.NumberOfBeneficiariesPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.companyoremploymentrelated.employment.amend.NumberOfBeneficiariesView

import scala.concurrent.{ExecutionContext, Future}

class NumberOfBeneficiariesController @Inject()(
                                                 val controllerComponents: MessagesControllerComponents,
                                                 standardActionSets: StandardActionSets,
                                                 formProvider: NumberOfBeneficiariesFormProvider,
                                                 view: NumberOfBeneficiariesView,
                                                 repository: PlaybackRepository,
                                                 @AmendEmploymentRelatedBeneficiary navigator: Navigator
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[HowManyBeneficiaries] = formProvider.apply()

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(NumberOfBeneficiariesPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))

  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NumberOfBeneficiariesPage, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NumberOfBeneficiariesPage, updatedAnswers))
      )
  }
}
