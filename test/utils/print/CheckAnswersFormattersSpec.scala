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

package utils.print

import java.time.LocalDate

import base.SpecBase
import play.api.i18n.{Lang, MessagesImpl}

class CheckAnswersFormattersSpec extends SpecBase {

  private val checkAnswersFormatters: CheckAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]

  "CheckAnswersFormatters" when {

    ".formatDate" when {

      def messages(langCode: String): MessagesImpl = {
        val lang: Lang = Lang(langCode)
        MessagesImpl(lang, messagesApi)
      }

      val date: LocalDate = LocalDate.parse("1996-02-03")

      "in English mode" must {
        "format date in English" in {

          val result: String = checkAnswersFormatters.formatDate(date)(messages("en"))
          result mustBe "3 February 1996"
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" in {

          val result: String = checkAnswersFormatters.formatDate(date)(messages("cy"))
          result mustBe "3 Chwefror 1996"
        }
      }
    }
  }

}
