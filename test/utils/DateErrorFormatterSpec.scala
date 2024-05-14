/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

import base.SpecBase
import play.api.i18n.{Lang, MessagesImpl}

class DateErrorFormatterSpec extends SpecBase {

  private val args: Seq[String] = Seq("day", "month", "year")

  "Date Error Formatter" must {

    "format error args" when {

      "language set to English" in {

        val messages: MessagesImpl = MessagesImpl(Lang("en"), messagesApi)

        val result = DateErrorFormatter.formatArgs(args)(messages)

        result mustEqual Seq("day", "month", "year")
      }

      "language set to Welsh" in {

        val messages: MessagesImpl = MessagesImpl(Lang("cy"), messagesApi)

        val result = DateErrorFormatter.formatArgs(args)(messages)

        result mustEqual Seq("diwrnod", "mis", "blwyddyn")
      }
    }
  }

}
