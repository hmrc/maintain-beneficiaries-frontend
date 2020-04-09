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
import forms.NonUkAddressFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.other.NonUkAddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.other.add.NonUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class NonUkAddressController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: PlaybackRepository,
                                        @AddOtherBeneficiary navigator: Navigator,
                                        standardActionSets: StandardActionSets,
                                        descriptionAction: DescriptionRequiredAction,
                                        formProvider: NonUkAddressFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: NonUkAddressView,
                                        val countryOptions: CountryOptionsNonUK
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(NonUkAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, request.description))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, request.description))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NonUkAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NonUkAddressPage, updatedAnswers))
      )
  }
}
