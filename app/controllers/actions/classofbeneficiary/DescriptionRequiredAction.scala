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

package controllers.actions.classofbeneficiary

import controllers.actions
import controllers.actions.DescriptionRequest
import javax.inject.Inject
import models.requests.DataRequest
import pages.classofbeneficiary.DescriptionPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.ActionTransformer

import scala.concurrent.{ExecutionContext, Future}

class DescriptionRequiredAction @Inject()(val executionContext: ExecutionContext, val messagesApi: MessagesApi)
  extends ActionTransformer[DataRequest, DescriptionRequest] with I18nSupport {

  override protected def transform[A](request: DataRequest[A]): Future[DescriptionRequest[A]] = {
    Future.successful(actions.DescriptionRequest[A](request,
      getDescription(request)
    ))
  }

  private def getDescription[A](request: DataRequest[A]): String = {
    request.userAnswers.get(DescriptionPage) match {
      case Some(description) => description
      case None => request.messages(messagesApi)("classOfBeneficiary.description.default")
    }
  }
}
