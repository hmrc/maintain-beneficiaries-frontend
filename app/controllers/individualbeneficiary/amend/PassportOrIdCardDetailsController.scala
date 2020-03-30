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

package controllers.individualbeneficiary.amend

import config.annotations.IndividualBeneficiary
import controllers.actions._
import controllers.actions.individual.NameRequiredAction
import forms.CombinedPassportOrIdCardDetailsFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.individualbeneficiary.PassportOrIdCardDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import views.html.individualbeneficiary.amend.PassportOrIdCardDetailsView

import scala.concurrent.{ExecutionContext, Future}

class PassportOrIdCardDetailsController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            playbackRepository: PlaybackRepository,
                                            @IndividualBeneficiary navigator: Navigator,
                                            standardActionSets: StandardActionSets,
                                            nameAction: NameRequiredAction,
                                            formProvider: CombinedPassportOrIdCardDetailsFormProvider,
                                            countryOptions: CountryOptions,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: PassportOrIdCardDetailsView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("individualBeneficiary.passportOrIdCardDetails")

  def onPageLoad(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(PassportOrIdCardDetailsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.beneficiaryName, countryOptions.options))
  }

  def onSubmit(): Action[AnyContent] = (standardActionSets.verifiedForUtr andThen nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.beneficiaryName, countryOptions.options))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PassportOrIdCardDetailsPage, value))
            _              <- playbackRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PassportOrIdCardDetailsPage, updatedAnswers))
      )
  }
}
