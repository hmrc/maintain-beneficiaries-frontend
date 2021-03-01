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

package views.behaviours

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import views.ViewUtils

trait SelectCountryViewBehaviours extends QuestionViewBehaviours[String] {

  val errorPrefix = "site.error"

  def selectCountryPage(form: Form[String],
                        createView: Form[String] => HtmlFormat.Appendable,
                        messageKeyPrefix: String,
                        messageKeyParam: String,
                        expectedHintKey: Option[String] = None): Unit = {

    "behave like a page with a string value field" when {

      "rendered" must {

        "contain a label for the value" in {

          val doc = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))
          assertContainsLabel(doc, "value", messages(s"$messageKeyPrefix.heading", messageKeyParam), expectedHintText)
        }

        "contain an input for the value" in {

          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }
      }

      "rendered with a valid form" must {

        "have the correct selection option value 'selected' for the form country input value" in {

          val doc = asDocument(createView(form.fill("ES")))
          doc.getElementsByAttribute("selected").attr("value") mustBe "ES"
        }
      }

      "rendered with an error" must {

        "show an error summary" in {

          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-heading")
        }

        "show an error in the value field's label" in {

          val errorKey = "value"
          val errorMessage = "error.number"
          val error = FormError(errorKey, errorMessage)

          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("error-message").first
          errorSpan.text mustBe s"""${messages(errorPrefix)} ${messages(errorMessage)}"""
        }

        "show an error prefix in the browser title" in {

          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", ViewUtils.breadcrumbTitle(s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title")}"""))
        }
      }
    }
  }
}
