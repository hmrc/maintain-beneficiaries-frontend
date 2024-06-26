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

package controllers.charityortrust

import controllers.actions._
import forms.CharityOrTrustBeneficiaryTypeFormProvider
import javax.inject.Inject
import models.NormalMode
import models.beneficiaries.CharityOrTrustToAdd
import models.beneficiaries.CharityOrTrustToAdd._
import pages.charityortrust.CharityOrTrustPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.charityortrust.CharityOrTrustView

import scala.concurrent.{ExecutionContext, Future}

class CharityOrTrustController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          standardActionSets: StandardActionSets,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: CharityOrTrustView,
                                          formProvider: CharityOrTrustBeneficiaryTypeFormProvider,
                                          repository: PlaybackRepository
                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[CharityOrTrustToAdd] = formProvider()

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(CharityOrTrustPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CharityOrTrustPage, value))
            _ <- repository.set(updatedAnswers)
          } yield {
            value match {
              case Charity => Redirect(controllers.charityortrust.charity.routes.NameController.onPageLoad(NormalMode))
              case Trust => Redirect(controllers.charityortrust.trust.routes.NameController.onPageLoad(NormalMode))
            }
          }
      )
  }
}
