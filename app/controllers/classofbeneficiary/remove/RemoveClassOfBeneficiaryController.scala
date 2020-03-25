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

package controllers.classofbeneficiary.remove

import controllers.actions.StandardActionSets
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.RemoveBeneficiary
import models.beneficiaries.Beneficiary.ClassOfBeneficiaries
import navigation.Navigator
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.classofbeneficiary.remove.RemoveIndexView

import scala.concurrent.{ExecutionContext, Future}

class RemoveClassOfBeneficiaryController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    sessionRepository: PlaybackRepository,
                                                    navigator: Navigator,
                                                    standardActionSets: StandardActionSets,
                                                    trust: TrustService,
                                                    formProvider: RemoveIndexFormProvider,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: RemoveIndexView
                                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def formRoute(index: Int): Call =
    controllers.classofbeneficiary.remove.routes.RemoveClassOfBeneficiaryController.onSubmit(index)

  private val messagesPrefix: String = "removeClassOfBeneficiary"

  private val form = formProvider.apply(messagesPrefix)

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      trust.getUnidentifiedBeneficiary(request.userAnswers.utr, index).map {
        beneficiary =>
          Ok(view(messagesPrefix, form, index, beneficiary.description, formRoute(index)))
      }

  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.identifiedUserWithData.async {
    implicit request =>

      import scala.concurrent.ExecutionContext.Implicits._

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          trust.getUnidentifiedBeneficiary(request.userAnswers.utr, index).map {
            beneficiary =>
              BadRequest(view(messagesPrefix, formWithErrors, index, beneficiary.description, formRoute(index)))
          }
        },
        value => {
          if (value) {

            trust.getUnidentifiedBeneficiary(request.userAnswers.utr, index).flatMap {
              beneficiary =>
                if (beneficiary.provisional) {
                  for {
                    _ <- trust.removeBeneficiary(request.userAnswers.utr, RemoveBeneficiary(ClassOfBeneficiaries, index))
                  } yield Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
                } else {
                  Future.successful(Redirect(controllers.classofbeneficiary.remove.routes.WhenRemovedController.onPageLoad(index).url))
                }
            }
          } else {
            Future.successful(Redirect(controllers.routes.AddABeneficiaryController.onPageLoad().url))
          }
        }
      )
  }
}
