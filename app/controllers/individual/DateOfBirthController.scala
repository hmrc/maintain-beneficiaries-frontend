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

package controllers.individual

import controllers.actions.StandardActionSets
import forms.DateOfBirthFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.individual.{DateOfBirthPage, NamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.individual.DateOfBirthView

import scala.concurrent.{ExecutionContext, Future}

class DateOfBirthController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: PlaybackRepository,
                                       navigator: Navigator,
                                       standardActionSets: StandardActionSets,
                                       formProvider: DateOfBirthFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DateOfBirthView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("trustee.individual.dateOfBirth")


  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request =>

      val name = request.userAnswers.get(NamePage(index))
        .map { _.displayName }
        .getOrElse { request.messages(messagesApi)("individualBeneficiary.name.default") }

      val preparedForm = request.userAnswers.get(DateOfBirthPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, index, name))
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val name = request.userAnswers.get(NamePage(index))
            .map { _.displayName }
            .getOrElse { request.messages(messagesApi)("individualBeneficiary.name.default") }

          Future.successful(BadRequest(view(formWithErrors, index, name)))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DateOfBirthPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(DateOfBirthPage(index), updatedAnswers))
      )
  }
}
