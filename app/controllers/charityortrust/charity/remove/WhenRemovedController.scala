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

package controllers.charityortrust.charity.remove

import controllers.actions.StandardActionSets
import forms.DateRemovedFromTrustFormProvider
import javax.inject.Inject
import models.{BeneficiaryType, RemoveBeneficiary}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.charityortrust.charity.remove.WhenRemovedView

import scala.concurrent.ExecutionContext

class WhenRemovedController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       repository: PlaybackRepository,
                                       standardActionSets: StandardActionSets,
                                       formProvider: DateRemovedFromTrustFormProvider,
                                       trust: TrustService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhenRemovedView,
                                       trustService: TrustService
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("charityBeneficiary.whenRemoved", request.userAnswers.whenTrustSetup)

      trust.getCharityBeneficiary(request.userAnswers.utr, index).map {
        beneficiary =>
          Ok(view(form, index, beneficiary.name))
      }

  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      val form = formProvider.withPrefixAndTrustStartDate("charityBeneficiary.whenRemoved", request.userAnswers.whenTrustSetup)

      form.bindFromRequest().fold(
        formWithErrors => {
          trust.getCharityBeneficiary(request.userAnswers.utr, index).map {
            beneficiary =>
              BadRequest(view(formWithErrors, index, beneficiary.name))
          }
        },
        value =>
          trustService.removeBeneficiary(request.userAnswers.utr, RemoveBeneficiary(BeneficiaryType.CharityBeneficiary, index, value)).map(_ =>
            Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
          )
      )
  }
}
