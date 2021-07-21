/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.individualbeneficiary

import config.annotations.IndividualBeneficiary
import controllers.actions._
import controllers.actions.individual.NameRequiredAction
import forms.NationalInsuranceNumberFormProvider
import models.Mode
import navigation.Navigator
import pages.individualbeneficiary.NationalInsuranceNumberPage
import pages.individualbeneficiary.amend.IndexPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.individualbeneficiary.NationalInsuranceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NationalInsuranceNumberController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   sessionRepository: PlaybackRepository,
                                                   @IndividualBeneficiary navigator: Navigator,
                                                   standardActionSets: StandardActionSets,
                                                   nameAction: NameRequiredAction,
                                                   formProvider: NationalInsuranceNumberFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: NationalInsuranceNumberView,
                                                   trustsService: TrustService
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form(ninos: List[String]): Form[String] = formProvider.apply("individualBeneficiary.nationalInsuranceNumber", ninos)

  private def index(implicit request: BeneficiaryNameRequest[AnyContent]): Option[Int] = request.userAnswers.get(IndexPage)

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      trustsService.getIndividualNinos(request.userAnswers.identifier, index) map { ninos =>
        val preparedForm = request.userAnswers.get(NationalInsuranceNumberPage) match {
          case None => form(ninos)
          case Some(value) => form(ninos).fill(value)
        }

        Ok(view(preparedForm, mode, request.beneficiaryName))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForUtr.andThen(nameAction).async {
    implicit request =>

      trustsService.getIndividualNinos(request.userAnswers.identifier, index) flatMap { ninos =>
        form(ninos).bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, request.beneficiaryName))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(NationalInsuranceNumberPage, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(NationalInsuranceNumberPage, mode, updatedAnswers))
        )
      }
  }
}
