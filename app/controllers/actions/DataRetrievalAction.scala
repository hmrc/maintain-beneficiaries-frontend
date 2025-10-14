/*
 * Copyright 2025 HM Revenue & Customs
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
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import repositories.{ActiveSessionRepository, PlaybackRepository}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject()(
                                         val activeSessionRepository: ActiveSessionRepository,
                                         val playbackRepository: PlaybackRepository
                                       )(implicit val executionContext: ExecutionContext) extends DataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {

    def createdOptionalDataRequest(request: IdentifierRequest[A], userAnswers: Option[UserAnswers]) = {
      OptionalDataRequest(
        request.request,
        userAnswers,
        request.user
      )
    }

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    activeSessionRepository.get(request.user.internalId) flatMap {
      case Some(session) =>
        playbackRepository.get(request.user.internalId, session.utr, Session.id(hc)) map {
          case None =>
            createdOptionalDataRequest(request, None)
          case Some(userAnswers) =>
            createdOptionalDataRequest(request, Some(userAnswers))
        }
      case None =>
        Future.successful(createdOptionalDataRequest(request, None))
    }
  }
}

trait DataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalDataRequest]
