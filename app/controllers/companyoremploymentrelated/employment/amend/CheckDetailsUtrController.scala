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

package controllers.companyoremploymentrelated.employment.amend

import config.FrontendAppConfig
import controllers.actions._
import controllers.actions.employment.NameRequiredAction
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.EmploymentRelatedBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.companyoremploymentrelated.employment.amend.CheckDetailsUtrView

class CheckDetailsUtrController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsUtrView,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: EmploymentRelatedBeneficiaryPrintHelper,
                                        nameAction: NameRequiredAction
                                      ) extends FrontendBaseController with I18nSupport {

  private val provisional: Boolean = false

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction) {
    implicit request =>
      val section: AnswerSection = printHelper(request.userAnswers, provisional, request.beneficiaryName)
      Ok(view(section, request.beneficiaryName))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForUtr {
      Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
  }
}
