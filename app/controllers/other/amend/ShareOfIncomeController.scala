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

import config.annotations.AmendOtherBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.other.DescriptionRequiredAction
import forms.IncomePercentageFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.other.ShareOfIncomePage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.other.amend.ShareOfIncomeView

import scala.concurrent.{ExecutionContext, Future}

class ShareOfIncomeController @Inject()(
                                         val controllerComponents: MessagesControllerComponents,
                                         standardActionSets: StandardActionSets,
                                         formProvider: IncomePercentageFormProvider,
                                         view: ShareOfIncomeView,
                                         repository: PlaybackRepository,
                                         @AmendOtherBeneficiary navigator: Navigator,
                                         descriptionAction: DescriptionRequiredAction
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Int] = formProvider.withPrefix("otherBeneficiary.shareOfIncome")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ShareOfIncomePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.description))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.description))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ShareOfIncomePage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ShareOfIncomePage, updatedAnswers))
      )
  }
}
