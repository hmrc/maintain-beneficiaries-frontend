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

package controllers.companyoremploymentrelated.company

import config.annotations.CompanyBeneficiary
import controllers.actions._
import controllers.actions.company.NameRequiredAction
import forms.NonUkAddressFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.companyoremploymentrelated.company.NonUkAddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.companyoremploymentrelated.company.NonUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class NonUkAddressController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: PlaybackRepository,
                                        @CompanyBeneficiary navigator: Navigator,
                                        standardActionSets: StandardActionSets,
                                        nameAction: NameRequiredAction,
                                        formProvider: NonUkAddressFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: NonUkAddressView,
                                        val countryOptions: CountryOptionsNonUK
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(NonUkAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, countryOptions.options(), request.beneficiaryName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, countryOptions.options(), request.beneficiaryName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NonUkAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NonUkAddressPage, mode, updatedAnswers))
      )
  }
}
