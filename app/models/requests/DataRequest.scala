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

package models.requests

import models.UserAnswers
import play.api.mvc.{Request, WrappedRequest}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}

sealed trait User {
  val internalId: String
  val affinityGroup: AffinityGroup
  val enrolments: Enrolments
}

case class AgentUser(internalId: String, enrolments: Enrolments, agentReferenceNumber: String) extends User {
  override val affinityGroup: AffinityGroup = AffinityGroup.Agent
}

case class OrganisationUser(internalId: String, enrolments: Enrolments) extends User {
  override val affinityGroup: AffinityGroup = AffinityGroup.Organisation
}

case class OptionalDataRequest[A](request: Request[A],
                                  userAnswers: Option[UserAnswers],
                                  user: User) extends WrappedRequest[A](request)

case class DataRequest[A](request: Request[A],
                          userAnswers: UserAnswers,
                          user: User) extends WrappedRequest[A](request)
