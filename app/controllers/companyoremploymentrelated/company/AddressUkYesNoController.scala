/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.actions.StandardActionSets
import controllers.actions.company.NameRequiredAction
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.companyoremploymentrelated.company.AddressUkYesNoPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.companyoremploymentrelated.company.AddressUkYesNoView

import scala.concurrent.{ExecutionContext, Future}

class AddressUkYesNoController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          standardActionSets: StandardActionSets,
                                          formProvider: YesNoFormProvider,
                                          view: AddressUkYesNoView,
                                          repository: PlaybackRepository,
                                          @CompanyBeneficiary navigator: Navigator,
                                          nameAction: NameRequiredAction
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("companyBeneficiary.addressUkYesNo")

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddressUkYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, request.beneficiaryName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, request.beneficiaryName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddressUkYesNoPage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddressUkYesNoPage, mode, updatedAnswers))
      )
  }
}
