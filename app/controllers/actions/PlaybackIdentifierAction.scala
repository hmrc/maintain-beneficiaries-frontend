/*
 * Copyright 2026 HM Revenue & Customs
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

import com.google.inject.{ImplementedBy, Inject}
import models.requests.DataRequest
import play.api.mvc.{ActionRefiner, Result}
import services.AuthenticationService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class PlaybackIdentifierActionImpl @Inject() (
  playbackAuthenticationService: AuthenticationService
)(implicit override val executionContext: ExecutionContext)
    extends PlaybackIdentifierAction {

  override def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = {
    val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    playbackAuthenticationService.authenticateForUtr(request.userAnswers.identifier)(request, hc)
  }

}

@ImplementedBy(classOf[PlaybackIdentifierActionImpl])
trait PlaybackIdentifierAction extends ActionRefiner[DataRequest, DataRequest] {
  def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]]
}
