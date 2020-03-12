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

package services

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.EnrolmentStoreConnector
import controllers.routes
import handlers.ErrorHandler
import models.requests.DataRequest
import models.requests.EnrolmentStoreResponse.{AlreadyClaimed, NotClaimed}
import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
                                       enrolmentStoreConnector: EnrolmentStoreConnector,
                                       config: FrontendAppConfig,
                                       errorHandler: ErrorHandler,
                                       trustsIV: TrustsIV,
                                       delegatedEnrolment: AgentAuthorisedForDelegatedEnrolment,
                                       implicit val ec: ExecutionContext
                                     ) extends AuthenticationService {

  override def authenticate[A](utr: String)
                     (implicit request: DataRequest[A],
                      hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]] =
    request.user.affinityGroup match {
      case Agent =>
        checkIfAgentAuthorised(utr)
      case _ =>
        checkIfTrustIsClaimedAndTrustIV(utr)
    }

  private def checkIfTrustIsClaimedAndTrustIV[A](utr: String)
                                                (implicit request: DataRequest[A],
                                                 hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]] = {

    val userEnrolled = checkForTrustEnrolmentForUTR(utr)

    if (userEnrolled) {
      Logger.info(s"[PlaybackAuthentication] user is enrolled")

      trustsIV.authenticate(
        utr = utr,
        onIVRelationshipExisting = {
          Logger.info(s"[PlaybackAuthentication] user is enrolled, redirecting to maintain")
          Future.successful(Right(request))
        },
        onIVRelationshipNotExisting = {
          Logger.info(s"[PlaybackAuthentication] user is enrolled, redirecting to /verify-identity-for-a-trust")
          Future.successful(Left(Redirect(config.verifyIdentityForATrustUrl(utr))))
        }
      )
    } else {
      enrolmentStoreConnector.checkIfAlreadyClaimed(utr) flatMap {
        case AlreadyClaimed =>
          Logger.info(s"[PlaybackAuthentication] user is not enrolled but the trust is already claimed")
          Future.successful(Left(Redirect(controllers.routes.IndexController.onPageLoad(utr))))
        case NotClaimed =>
          Logger.info(s"[PlaybackAuthentication] user is not enrolled and the trust is not claimed")
          Future.successful(Left(Redirect(config.claimATrustUrl(utr))))
        case _ =>
          Future.successful(Left(InternalServerError(errorHandler.internalServerErrorTemplate)))
      }
    }
  }

  private def checkIfAgentAuthorised[A](utr: String)
                                       (implicit request: DataRequest[A],
                                        hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]] =

    enrolmentStoreConnector.checkIfAlreadyClaimed(utr) flatMap {
      case NotClaimed =>
        Logger.info(s"[PlaybackAuthentication] trust is not claimed")
        Future.successful(Left(Redirect(controllers.routes.TrustNotClaimedController.onPageLoad())))
      case AlreadyClaimed =>

        delegatedEnrolment.authenticate(utr)
      case _ =>
        Future.successful(Left(InternalServerError(errorHandler.internalServerErrorTemplate)))
    }

  private def checkForTrustEnrolmentForUTR[A](utr: String)(implicit request: DataRequest[A]): Boolean =
    request.user.enrolments.enrolments
      .find(_.key equals "HMRC-TERS-ORG")
      .flatMap(_.identifiers.find(_.key equals "SAUTR"))
      .exists(_.value equals utr)

}

trait AuthenticationService {
  def authenticate[A](utr: String)
                     (implicit request: DataRequest[A],
                      hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]]
}