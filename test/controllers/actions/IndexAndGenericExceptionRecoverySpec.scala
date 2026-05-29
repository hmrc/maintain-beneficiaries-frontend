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

import base.SpecBase
import handlers.ErrorHandler
import models.BeneficiaryType.CharityBeneficiary
import org.scalatest.concurrent.ScalaFutures
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{MessagesControllerComponents, RequestHeader, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OutOfBoundsPageNotFoundView

import scala.concurrent.Future

class IndexAndGenericExceptionRecoverySpec extends SpecBase with ScalaFutures {

  implicit private val rh: RequestHeader = FakeRequest()

  private val view: OutOfBoundsPageNotFoundView = injector.instanceOf[OutOfBoundsPageNotFoundView]
  private val handler: ErrorHandler             = injector.instanceOf[ErrorHandler]

  private class TestRecovery
      extends FrontendBaseController with I18nSupport with Logging with IndexAndGenericExceptionRecovery {

    override val controllerComponents: MessagesControllerComponents =
      injector.instanceOf[MessagesControllerComponents]

    override val errorHandler: ErrorHandler                   = handler
    override def outOfBoundsView: OutOfBoundsPageNotFoundView = view

    def expose: PartialFunction[Throwable, Future[Result]] =
      recoverIndexAndGenericException(CharityBeneficiary, 0, "1234567890", "testMethod")

  }

  private val pf: PartialFunction[Throwable, Future[Result]] = (new TestRecovery).expose

  "IndexAndGenericExceptionRecovery" must {

    "be defined for IndexOutOfBoundsException and any other Throwable" in {
      pf.isDefinedAt(new IndexOutOfBoundsException("")) mustBe true
      pf.isDefinedAt(new RuntimeException(""))          mustBe true
    }

    "return Not Found and the out of bounds page on an IndexOutOfBoundsException" in {
      val result = pf(new IndexOutOfBoundsException("stale index"))

      status(result) mustEqual NOT_FOUND
      contentAsString(result) mustEqual view()(rh, messages).toString
    }

    "return Internal Server Error on any other exception" in {
      val result = pf(new RuntimeException("something else"))

      status(result) mustEqual INTERNAL_SERVER_ERROR
      contentAsString(result) mustEqual handler.internalServerErrorTemplate(rh).futureValue.toString
    }
  }

}
