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

package config

import base.SpecBase
import play.api.i18n.{Lang, MessagesImpl}

class FrontendAppConfigSpec extends SpecBase {

  val config: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  "FrontendAppConfig" when {

    ".helplineUrl" when {
      "in English mode" must {
        "return trusts helpline URL" in {
          val messages = MessagesImpl(Lang("en"), messagesApi)

          config.helplineUrl(messages) mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/trusts"
        }
      }

      "in Welsh mode" must {
        "return Welsh language helpline URL" in {
          val messages = MessagesImpl(Lang("cy"), messagesApi)

          config.helplineUrl(messages) mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/welsh-language-helplines"
        }
      }
    }
  }
}
