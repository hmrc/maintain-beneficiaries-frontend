/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.actions.individual

import controllers.actions.BeneficiaryNameRequest
import javax.inject.Inject
import models.requests.DataRequest
import pages.individualbeneficiary.NamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.ActionTransformer

import scala.concurrent.{ExecutionContext, Future}

class NameRequiredAction @Inject()(val executionContext: ExecutionContext, val messagesApi: MessagesApi)
  extends ActionTransformer[DataRequest, BeneficiaryNameRequest] with I18nSupport {

  override protected def transform[A](request: DataRequest[A]): Future[BeneficiaryNameRequest[A]] = {
    Future.successful(BeneficiaryNameRequest[A](request,
      getName(request)
    ))
  }

  private def getName[A](request: DataRequest[A]): String = {
    request.userAnswers.get(NamePage) match {
      case Some(name) => name.displayName
      case _ => request.messages(messagesApi)("individualBeneficiary.name.default")
    }
  }
}
