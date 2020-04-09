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

package controllers.other.add

import config.annotations.AddOtherBeneficiary
import controllers.actions._
import controllers.actions.other.DescriptionRequiredAction
import forms.UkAddressFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.other.UkAddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.other.add.UkAddressView

import scala.concurrent.{ExecutionContext, Future}

class UkAddressController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     sessionRepository: PlaybackRepository,
                                     @AddOtherBeneficiary navigator: Navigator,
                                     standardActionSets: StandardActionSets,
                                     descriptionAction: DescriptionRequiredAction,
                                     formProvider: UkAddressFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: UkAddressView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(UkAddressPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UkAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(UkAddressPage, updatedAnswers))
      )
  }
}