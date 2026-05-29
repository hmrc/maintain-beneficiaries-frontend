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

import handlers.ErrorHandler
import models.BeneficiaryType
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{RequestHeader, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OutOfBoundsPageNotFoundView

import scala.concurrent.{ExecutionContext, Future}

trait IndexAndGenericExceptionRecovery {
  self: FrontendBaseController with I18nSupport with Logging =>

  protected val errorHandler: ErrorHandler
  protected def outOfBoundsView: OutOfBoundsPageNotFoundView
  private val className = this.getClass.getName

  protected def recoverIndexAndGenericException(
    entity: BeneficiaryType,
    index: Int,
    identifier: String,
    callingMethod: String
  )(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    request: RequestHeader
  ): PartialFunction[Throwable, Future[Result]] = {
    case _: IndexOutOfBoundsException =>
      logger.warn(
        s"[Session ID: ${utils.Session.id(hc)}][UTR/URN: $identifier] " +
          s"${classNameAndMethod(callingMethod)}: no $entity at index $index, IndexOutOfBoundsException - showing page not found"
      )
      Future.successful(NotFound(outOfBoundsView()))
    case e                            =>
      logger.error(
        s"[Session ID: ${utils.Session.id(hc)}][UTR/URN: $identifier] " +
          s"${classNameAndMethod(callingMethod)}: error at index $index: ${e.getMessage}"
      )

      errorHandler.internalServerErrorTemplate.map(InternalServerError(_))
  }

  private def classNameAndMethod(method: String) = s"[$className][$method]"

}
