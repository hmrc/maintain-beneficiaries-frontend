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

package controllers.other

import config.annotations.OtherBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.other.DescriptionRequiredAction
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.other.AddressYesNoPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.other.AddressYesNoView

import scala.concurrent.{ExecutionContext, Future}

class AddressYesNoController @Inject()(
                                        val controllerComponents: MessagesControllerComponents,
                                        standardActionSets: StandardActionSets,
                                        formProvider: YesNoFormProvider,
                                        view: AddressYesNoView,
                                        repository: PlaybackRepository,
                                        @OtherBeneficiary navigator: Navigator,
                                        descriptionAction: DescriptionRequiredAction
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("otherBeneficiary.addressYesNo")

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddressYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, request.description))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, request.description))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddressYesNoPage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddressYesNoPage, mode, updatedAnswers))
      )
  }
}
