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

import base.SpecBase
import play.api.i18n.{Lang, MessagesImpl}
import play.twirl.api.Html
import java.time.LocalDate

import models.{CombinedPassportOrIdCard, DetailsType}

class CheckAnswersFormattersSpec extends SpecBase {

  private val checkAnswersFormatters: CheckAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]

  "CheckAnswersFormatters" when {
    def messages(langCode: String): MessagesImpl = {
      val lang: Lang = Lang(langCode)
      MessagesImpl(lang, messagesApi)
    }


    ".formatDate" when {

      def messages(langCode: String): MessagesImpl = {
        val lang: Lang = Lang(langCode)
        MessagesImpl(lang, messagesApi)
      }

      val date: LocalDate = LocalDate.parse("1996-02-03")

      "in English mode" must {
        "format date in English" in {

          val result: Html = checkAnswersFormatters.formatDate(date)(messages("en"))
          result mustBe Html("3 February 1996")
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" in {

          val result: Html = checkAnswersFormatters.formatDate(date)(messages("cy"))
          result mustBe Html("3 Chwefror 1996")
        }
      }
    }

    ".formatNino" must {

      "format a nino with prefix and suffix" in {
        val nino = "JP121212A"
        val result = checkAnswersFormatters.formatNino(nino)
        result mustBe Html("JP 12 12 12 A")
      }

      "suppress IllegalArgumentException and not format nino" in {
        val nino = "JP121212"
        val result = checkAnswersFormatters.formatNino(nino)
        result mustBe Html("JP121212")
      }

    }

    ".formatPassportOrIdCardDetails" must {
      val date: LocalDate = LocalDate.parse("1996-02-03")

      "mask the passport/ID card number" when {
        "details not added in session" when {

          "English" when {

            "number 4 digits or more" in {
              val passportOrIdCard = CombinedPassportOrIdCard("FR", "1234567890", date)
              val result = checkAnswersFormatters.formatPassportOrIdCardDetails(passportOrIdCard)(messages("en"))
              result mustBe Html("France<br />Number ending 7890<br />3 February 1996")
            }

            "number less than 4 digits" in {
              val passportOrIdCard = CombinedPassportOrIdCard("FR", "1", date)
              val result = checkAnswersFormatters.formatPassportOrIdCardDetails(passportOrIdCard)(messages("en"))
              result mustBe Html("France<br />Number ending 1<br />3 February 1996")
            }
          }

          "Welsh" when {

            "number 4 digits or more" in {
              val passportOrIdCard = CombinedPassportOrIdCard("FR", "1234567890", date)
              val result = checkAnswersFormatters.formatPassportOrIdCardDetails(passportOrIdCard)(messages("cy"))
              result mustBe Html("Ffrainc<br />Rhif sy’n gorffen gyda 7890<br />3 Chwefror 1996")
            }

            "number less than 4 digits" in {
              val passportOrIdCard = CombinedPassportOrIdCard("FR", "1", date)
              val result = checkAnswersFormatters.formatPassportOrIdCardDetails(passportOrIdCard)(messages("cy"))
              result mustBe Html("Ffrainc<br />Rhif sy’n gorffen gyda 1<br />3 Chwefror 1996")
            }
          }
        }
      }

      "not mask the passport/ID card number" when {
        "details added in session" when {

          "English" in {

            val passportOrIdCard = CombinedPassportOrIdCard("FR", "1234567890", date, DetailsType.Passport)
            val result = checkAnswersFormatters.formatPassportOrIdCardDetails(passportOrIdCard)(messages("en"))
            result mustBe Html("France<br />1234567890<br />3 February 1996")
          }

          "Welsh" in {

            val passportOrIdCard = CombinedPassportOrIdCard("FR", "1234567890", date, DetailsType.IdCard)
            val result = checkAnswersFormatters.formatPassportOrIdCardDetails(passportOrIdCard)(messages("cy"))
            result mustBe Html("Ffrainc<br />1234567890<br />3 Chwefror 1996")
          }
        }
      }
    }
  }

}
