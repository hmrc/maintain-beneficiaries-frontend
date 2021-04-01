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

package controllers

import connectors.TrustConnector
import controllers.actions.StandardActionSets
import models.UserAnswers
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.FeatureFlagService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 actions: StandardActionSets,
                                 cacheRepository: PlaybackRepository,
                                 connector: TrustConnector,
                                 featureFlagService: FeatureFlagService
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(identifier: String): Action[AnyContent] = (actions.auth andThen actions.saveSession(identifier) andThen actions.getData).async {
    implicit request =>

      for {
        details <- connector.getTrustDetails(identifier)
        is5mldEnabled <- featureFlagService.is5mldEnabled()
        isUnderlyingData5mld <- connector.isTrust5mld(identifier)
        ua <- Future.successful {
          request.userAnswers match {
            case Some(userAnswers) => userAnswers.copy(
              trustType = details.typeOfTrust,
              is5mldEnabled = is5mldEnabled,
              isTaxable = details.isTaxable,
              isUnderlyingData5mld = isUnderlyingData5mld
            )
            case None => UserAnswers(
              internalId = request.user.internalId,
              identifier = identifier,
              whenTrustSetup = details.startDate,
              trustType = details.typeOfTrust,
              is5mldEnabled = is5mldEnabled,
              isTaxable = details.isTaxable,
              isUnderlyingData5mld = isUnderlyingData5mld
            )
          }
        }
        _ <- cacheRepository.set(ua)
      } yield {
        logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR: $identifier] user has started maintaining beneficiaries")
        Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
      }
  }
}
