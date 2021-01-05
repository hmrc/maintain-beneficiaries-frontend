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

package controllers.actions

import javax.inject.Inject
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import play.api.mvc.{ActionBuilder, AnyContent}

class StandardActionSets @Inject()(identify: IdentifierAction,
                                   val saveSession: SaveActiveSessionProvider,
                                   val getData: DataRetrievalAction,
                                   requireData: DataRequiredAction,
                                   playbackIdentifier: PlaybackIdentifierAction
                                  ){

  def auth: ActionBuilder[IdentifierRequest, AnyContent] = identify

  def authWithSession : ActionBuilder[OptionalDataRequest, AnyContent] = auth andThen getData

  def identifiedUserWithData: ActionBuilder[DataRequest, AnyContent] = authWithSession andThen requireData

  def verifiedForUtr: ActionBuilder[DataRequest, AnyContent] = identifiedUserWithData andThen playbackIdentifier

}
