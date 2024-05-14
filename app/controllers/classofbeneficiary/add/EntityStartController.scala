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

package controllers.classofbeneficiary.add

import config.annotations.ClassOfBeneficiary
import connectors.TrustConnector
import controllers.actions.StandardActionSets
import controllers.actions.classofbeneficiary.DescriptionRequiredAction
import forms.DateAddedToTrustFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.classofbeneficiary.EntityStartPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.classofbeneficiary.add.EntityStartView

import scala.concurrent.{ExecutionContext, Future}

class EntityStartController @Inject()(
                                       val controllerComponents: MessagesControllerComponents,
                                       standardActionSets: StandardActionSets,
                                       formProvider: DateAddedToTrustFormProvider,
                                       connector: TrustConnector,
                                       view: EntityStartView,
                                       trustService: TrustService,
                                       repository: PlaybackRepository,
                                       @ClassOfBeneficiary navigator: Navigator,
                                       descriptionAction: DescriptionRequiredAction
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val prefix: String = "classOfBeneficiary.entityStart"

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction) {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate(prefix, request.userAnswers.whenTrustSetup)

      val preparedForm = request.userAnswers.get(EntityStartPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.description))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(descriptionAction).async {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate(prefix, request.userAnswers.whenTrustSetup)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.description))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(EntityStartPage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(EntityStartPage, updatedAnswers))
      )
  }

}
