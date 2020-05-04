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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.{AgentUser, IdentifierRequest, OrganisationUser}
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._
import services.AuthenticationService
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject()(
                                               config: FrontendAppConfig,
                                               trustsAuthFunctions: TrustsAuthorisedFunctions,
                                               val parser: BodyParsers.Default,
                                               playbackAuthenticationService: AuthenticationService
                                             )
                                             (implicit val executionContext: ExecutionContext) extends IdentifierAction {

  private def authoriseAgent[A](internalId: String,
                                enrolments: Enrolments,
                                block: IdentifierRequest[A] => Future[Result])
                               (implicit request: Request[A], hc: HeaderCarrier) = {

    playbackAuthenticationService.authenticateAgent() flatMap {
      case Right(arn) => block(IdentifierRequest(request, AgentUser(internalId, enrolments, arn)))
      case Left(result: Result) => Future.successful(result)
    }
  }

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    val retrievals = Retrievals.internalId and
      Retrievals.affinityGroup and
      Retrievals.allEnrolments

    trustsAuthFunctions.authorised().retrieve(retrievals) {
      case Some(internalId) ~ Some(Agent) ~ enrolments =>
        Logger.info(s"[AuthenticatedIdentifierAction] successfully identified as an Agent")
        authoriseAgent(internalId, enrolments, block)(request, hc)
      case Some(internalId) ~ Some(Organisation) ~ enrolments =>
        Logger.info(s"[AuthenticatedIdentifierAction] successfully identified as Organisation")
        block(IdentifierRequest(request, OrganisationUser(internalId, enrolments)))
      case Some(_) ~ _ ~ _ =>
        Logger.info(s"[AuthenticatedIdentifierAction] Unauthorised due to affinityGroup being Individual")
        Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
      case _ =>
        Logger.warn(s"[AuthenticatedIdentifierAction] Unable to retrieve internal id")
        throw new UnauthorizedException("Unable to retrieve internal Id")
    } recover trustsAuthFunctions.recoverFromAuthorisation
  }
}
