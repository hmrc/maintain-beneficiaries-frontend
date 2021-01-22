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

package controllers

import base.SpecBase
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

class LogoutControllerSpec extends SpecBase {

  "logout should redirect to feedback and audit" in {

    val mockAuditConnector = mock[AuditConnector]

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .overrides(bind[AuditConnector].toInstance(mockAuditConnector))
      .build()

    val request = FakeRequest(GET, routes.LogoutController.logout().url)

    val result = route(application, request).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustBe frontendAppConfig.logoutUrl

    verify(mockAuditConnector).sendExplicitAudit(eqTo("trusts"), any[Map[String, String]])(any(), any())

    application.stop()

  }

}
