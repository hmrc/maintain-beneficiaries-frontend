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

package controllers.charityortrust.trust

import config.annotations.TrustBeneficiary
import connectors.TrustConnector
import controllers.actions.StandardActionSets
import forms.StringFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.charityortrust.trust.NamePage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.charityortrust.trust.NameView

import scala.concurrent.{ExecutionContext, Future}

class NameController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                standardActionSets: StandardActionSets,
                                formProvider: StringFormProvider,
                                connector: TrustConnector,
                                view: NameView,
                                trustService: TrustService,
                                repository: PlaybackRepository,
                                @TrustBeneficiary navigator: Navigator
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val length: Int = 105

  val form: Form[String] = formProvider.withPrefix("trustBeneficiary.name", length)

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(NamePage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NamePage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NamePage, mode, updatedAnswers))
      )
  }
}
