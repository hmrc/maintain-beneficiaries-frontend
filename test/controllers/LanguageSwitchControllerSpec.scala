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
import config.FrontendAppConfig
import play.api.Configuration
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Headers
import play.api.test.FakeRequest
import play.api.test.Helpers._

class LanguageSwitchControllerSpec extends SpecBase {

  private def switchLanguageRoute(lang: String): String = routes.LanguageSwitchController.switchToLanguage(lang).url

  private val english = "english"
  private val welsh = "cymraeg"
  private val fakeUrl: String = "fakeUrl"

  private lazy val config: Configuration = injector.instanceOf[FrontendAppConfig].configuration

  def frontendAppConfig(languageToggleEnabled: Boolean = true): FrontendAppConfig = {
    new FrontendAppConfig(config) {
      override lazy val languageTranslationEnabled: Boolean = languageToggleEnabled
    }
  }

  "LanguageSwitch Controller" when {

    "language toggle enabled" when {

      "English selected" must {
        "switch to English" in {

          val application = new GuiceApplicationBuilder()
            .overrides(bind[FrontendAppConfig].toInstance(frontendAppConfig()))
            .build()

          val requestHeaders: Headers = new Headers(Seq(("Referer", fakeUrl)))

          val request = FakeRequest(GET, switchLanguageRoute(english)).withHeaders(requestHeaders)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual fakeUrl

          cookies(result).find(_.name == "PLAY_LANG").get.value mustEqual "en"

          application.stop()
        }
      }

      "Welsh selected" must {
        "switch to Welsh" in {

          val application = new GuiceApplicationBuilder()
            .overrides(bind[FrontendAppConfig].toInstance(frontendAppConfig()))
            .build()

          val requestHeaders: Headers = new Headers(Seq(("Referer", fakeUrl)))

          val request = FakeRequest(GET, switchLanguageRoute(welsh)).withHeaders(requestHeaders)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual fakeUrl

          cookies(result).find(_.name == "PLAY_LANG").get.value mustEqual "cy"

          application.stop()
        }
      }
    }

    "language toggle disabled" must {

      "default to English" in {

        val application = new GuiceApplicationBuilder()
          .overrides(bind[FrontendAppConfig].toInstance(frontendAppConfig(false)))
          .build()

        val requestHeaders: Headers = new Headers(Seq(("Referer", fakeUrl)))

        val request = FakeRequest(GET, switchLanguageRoute(english)).withHeaders(requestHeaders)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeUrl

        cookies(result).find(_.name == "PLAY_LANG").get.value mustEqual "en"

        application.stop()
      }
    }

    "no referer in header" must {

      "redirect to internal server error template" in {

        val application = new GuiceApplicationBuilder()
          .overrides(bind[FrontendAppConfig].toInstance(frontendAppConfig()))
          .build()

        val request = FakeRequest(GET, switchLanguageRoute(welsh))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig().loginContinueUrl

        application.stop()
      }
    }
  }
}
