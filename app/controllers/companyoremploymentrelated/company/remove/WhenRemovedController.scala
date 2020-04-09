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

package controllers.companyoremploymentrelated.company.remove

import controllers.actions.StandardActionSets
import forms.DateRemovedFromTrustFormProvider
import javax.inject.Inject
import models.{BeneficiaryType, RemoveBeneficiary}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.companyoremploymentrelated.company.remove.WhenRemovedView

import scala.concurrent.{ExecutionContext, Future}

class WhenRemovedController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: PlaybackRepository,
                                       standardActionSets: StandardActionSets,
                                       formProvider: DateRemovedFromTrustFormProvider,
                                       trust: TrustService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhenRemovedView,
                                       trustService: TrustService
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getCompanyBeneficiary(request.userAnswers.utr, index).map {
        beneficiary =>
          val form = formProvider.withPrefixAndEntityStartDate("companyBeneficiary.whenRemoved", beneficiary.entityStart)
          Ok(view(form, index, beneficiary.name))
      }
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getCompanyBeneficiary(request.userAnswers.utr, index).flatMap {
        beneficiary =>
          val form = formProvider.withPrefixAndEntityStartDate("companyBeneficiary.whenRemoved", beneficiary.entityStart)
          form.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(BadRequest(view(formWithErrors, index, beneficiary.name)))
            },
            value =>
              trustService.removeBeneficiary(request.userAnswers.utr, RemoveBeneficiary(BeneficiaryType.CompanyBeneficiary, index, value)).map(_ =>
                Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
              )
          )
      }
  }
}
