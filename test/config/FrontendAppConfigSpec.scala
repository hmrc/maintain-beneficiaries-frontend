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

package config

import base.SpecBase
import play.api.i18n.{Lang, MessagesImpl}

import java.time.LocalDate

class FrontendAppConfigSpec extends SpecBase {

  val config: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  "FrontendAppConfig" when {

    ".appName" must {
      "return the expected application name" in {
        config.appName mustBe "maintain-beneficiaries-frontend"
      }
    }

    ".loginUrl" must {
      "return the expected login URL" in {
        config.loginUrl mustBe "http://localhost:9949/auth-login-stub/gg-sign-in"
      }
    }

    ".loginContinueUrl" must {
      "return the expected login continue URL" in {
        config.loginContinueUrl mustBe "http://localhost:9781/trusts-registration"
      }
    }

    ".logoutUrl" must {
      "append the useServiceNavigation parameter to the expected logout URL" in {
        config.logoutUrl mustBe "http://localhost:9514/feedback/trusts?useServiceNavigation"
      }
    }

    ".logoutAudit" must {
      "return the expected logout auditing feature flag" in {
        config.logoutAudit mustBe false
      }
    }

    ".maintainATrustOverview" must {
      "return the expected maintain-a-trust overview URL" in {
        config.maintainATrustOverview mustBe "http://localhost:9788/maintain-a-trust/overview"
      }
    }

    ".countdownLength" must {
      "return the expected timeout countdown length" in {
        config.countdownLength mustBe 120
      }
    }

    ".timeoutLength" must {
      "return the expected timeout length" in {
        config.timeoutLength mustBe 900
      }
    }

    ".trustsUrl" must {
      "return the base URL built from the trusts service config" in {
        config.trustsUrl mustBe "http://localhost:9782"
      }
    }

    ".trustAuthUrl" must {
      "return the base URL built from the trusts-auth service config" in {
        config.trustAuthUrl mustBe "http://localhost:9794"
      }
    }

    ".trustsStoreUrl" must {
      "return the base URL built from the trusts-store service config" in {
        config.trustsStoreUrl mustBe "http://localhost:9783"
      }
    }

    ".locationCanonicalList" must {
      "return the expected canonical location list filename" in {
        config.locationCanonicalList mustBe "location-autocomplete-canonical-list.json"
      }
    }

    ".locationCanonicalListCY" must {
      "return the expected Welsh canonical location list filename" in {
        config.locationCanonicalListCY mustBe "location-autocomplete-canonical-list-cy.json"
      }
    }

    ".languageMap" must {
      "return the supported languages keyed by their display name" in {
        config.languageMap mustBe Map(
          "english" -> Lang("en"),
          "cymraeg" -> Lang("cy")
        )
      }
    }

    ".minDate" must {
      "return the expected minimum date" in {
        config.minDate mustBe LocalDate.of(1500, 1, 1)
      }
    }

    ".maxDate" must {
      "return the expected maximum date" in {
        config.maxDate mustBe LocalDate.of(2099, 12, 31)
      }
    }

    ".helplineUrl" when {

      "in English mode" must {
        "return trusts helpline URL" in {
          val messages = MessagesImpl(Lang("en"), messagesApi)
          config.helplineUrl(
            messages
          ) mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/trusts"
        }
      }

      "in Welsh mode" must {
        "return Welsh language helpline URL" in {
          val messages = MessagesImpl(Lang("cy"), messagesApi)
          config.helplineUrl(
            messages
          ) mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/welsh-language-helplines"
        }
      }
    }

    ".cachettlplaybackInSeconds" must {
      "return the expected playback cache TTL, in seconds" in {
        config.cachettlplaybackInSeconds mustBe 3600L
      }
    }

    ".cachettlSessionInSeconds" must {
      "return the expected session cache TTL, in seconds" in {
        config.cachettlSessionInSeconds mustBe 3600L
      }
    }

    ".dropIndexes" must {
      "return the expected Mongo drop-indexes feature flag" in {
        config.dropIndexes mustBe true
      }
    }
  }

}
