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

package controllers.individualbeneficiary

import config.annotations.IndividualBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.individual.NameRequiredAction
import forms.CountryFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.individualbeneficiary.CountryOfNationalityPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.individualbeneficiary.CountryOfNationalityView

import scala.concurrent.{ExecutionContext, Future}

class CountryOfNationalityController @Inject()(
                                              val controllerComponents: MessagesControllerComponents,
                                              standardActionSets: StandardActionSets,
                                              formProvider: CountryFormProvider,
                                              view: CountryOfNationalityView,
                                              repository: PlaybackRepository,
                                              @IndividualBeneficiary navigator: Navigator,
                                              nameAction: NameRequiredAction,
                                              val countryOptions: CountryOptionsNonUK
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider.withPrefix("individualBeneficiary.countryOfNationality")

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CountryOfNationalityPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, request.beneficiaryName, countryOptions.options()))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, request.beneficiaryName, countryOptions.options()))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfNationalityPage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountryOfNationalityPage, mode, updatedAnswers))
      )
  }
}
