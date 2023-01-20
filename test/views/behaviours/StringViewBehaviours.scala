/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.ViewUtils

trait StringViewBehaviours extends QuestionViewBehaviours[String] {

  val answer = "answer"

  def stringPage(form: Form[String],
                 createView: Form[String] => HtmlFormat.Appendable,
                 messageKeyPrefix: String,
                 messageKeyParam: Option[String],
                 expectedFormAction: String,
                 expectedHintKey: Option[String] = None) = {

    "behave like a page with a string value field" when {

      "rendered" must {

        "contain a label for the value" in {

          val doc = asDocument(createView(form))
          val expectedHintText = expectedHintKey map (k => messages(k))
          assertContainsLabel(doc, "value", messages(s"$messageKeyPrefix.heading", messageKeyParam.getOrElse("")), expectedHintText)
        }

        "contain an input for the value" in {

          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }
      }

      "rendered with a valid form" must {

        "include the form's value in the value input" in {

          val doc = asDocument(createView(form.fill(answer)))
          doc.getElementById("value").`val`() mustBe answer
        }
      }

      "rendered with an error" must {

        "show an error summary" in {

          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-title")
        }

        "show an error associated to the value field" in {

          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe s"""${messages(errorPrefix)} ${messages(errorMessage)}"""
        }

        "show an error prefix in the browser title" in {

          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", ViewUtils.breadcrumbTitle(s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title", messageKeyParam.getOrElse(""))}"""))
        }
      }
    }
  }
}
