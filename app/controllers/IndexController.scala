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

package controllers

import connectors.TrustConnector
import controllers.actions.StandardActionSets
import models.TaskStatus.InProgress
import models.UserAnswers
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import javax.inject.Inject
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 actions: StandardActionSets,
                                 cacheRepository: PlaybackRepository,
                                 trustsConnector: TrustConnector,
                                 trustsStoreService: TrustsStoreService
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(identifier: String): Action[AnyContent] = (actions.auth andThen actions.saveSession(identifier) andThen actions.getData).async {
    implicit request =>

      for {
        details <- trustsConnector.getTrustDetails(identifier)
        isUnderlyingData5mld <- trustsConnector.isTrust5mld(identifier)
        taxableMigrationFlag <- trustsConnector.getTrustMigrationFlag(identifier)
        ua <- Future.successful {
          request.userAnswers match {
            case Some(userAnswers) => userAnswers.copy(
              trustType = details.typeOfTrust,
              isTaxable = details.isTaxable,
              isUnderlyingData5mld = isUnderlyingData5mld,
              migratingFromNonTaxableToTaxable = taxableMigrationFlag.migratingFromNonTaxableToTaxable
            )
            case None => {
              val internalId = request.user.internalId
              val id = identifier
              val sessionId = Session.id(hc)
              UserAnswers(
                internalId = internalId,
                identifier = id,
                sessionId = sessionId,
                newId = s"$internalId-$id-$sessionId",
                whenTrustSetup = details.startDate,
                trustType = details.typeOfTrust,
                isTaxable = details.isTaxable,
                isUnderlyingData5mld = isUnderlyingData5mld,
                migratingFromNonTaxableToTaxable = taxableMigrationFlag.migratingFromNonTaxableToTaxable
              )
            }
          }
        }
        _ <- cacheRepository.set(ua)
        _ <- trustsStoreService.updateTaskStatus(identifier, InProgress)
      } yield {
        logger.info(s"[Session ID: ${utils.Session.id(hc)}][UTR/URN: $identifier] user has started maintaining beneficiaries")
        if (taxableMigrationFlag.migratingFromNonTaxableToTaxable) {
          Redirect(controllers.transition.routes.BeneficiariesInformationController.onPageLoad())
        } else {
          Redirect(controllers.routes.AddABeneficiaryController.onPageLoad())
        }
      }
  }
}
