/*
 * Copyright 2020 HM Revenue & Customs
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

import models.NonUkAddress
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat


trait NonUkAddressViewBehaviours extends ViewBehaviours {


  val errorKey = "value"
  val errorMessage = "error.number"
  val error = FormError(errorKey, errorMessage)

  val form: Form[NonUkAddress]

  def nonUkAddressPage(createView: Form[NonUkAddress] => HtmlFormat.Appendable,
                       titleMessagePrefix: Option[String],
                       expectedFormAction: String,
                       args: String*) = {

    val titlePrefix = titleMessagePrefix.getOrElse("site.address.nonUk")

    val fields = Seq(("line1", None),
      ("line2", None),
      ("line3", None),
      ("country", None)
    )

    "behave like a non-UK address page" when {

      "rendered" must {

        for (field <- fields) {

          s"contain an input for $field" in {
            val doc = asDocument(createView(form))
            assertRenderedById(doc, field._1)
          }
        }

        "not render an error summary" in {

          val doc = asDocument(createView(form))
          assertNotRenderedById(doc, "error-summary-heading")
        }
      }

      "rendered with any error" must {

        "show an error prefix in the browser title" in {

          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(
            doc,
            "title",
            s"""${messages("error.browser.title.prefix")} ${messages(s"$titlePrefix.title", args: _*)}""")
        }
      }

      for (field <- fields) {

        s"rendered with an error with field '$field'" must {

          "show an error summary" in {

            val doc = asDocument(createView(form.withError(FormError(field._1, "error"))))
            assertRenderedById(doc, "error-summary-heading")
          }

          s"show an error in the label for field '$field'" in {

            val doc = asDocument(createView(form.withError(FormError(field._1, "error"))))
            val errorSpan = doc.getElementsByClass("error-message").first
            errorSpan.parent.getElementsByClass("form-label").attr("for") mustBe field._1
          }
        }
      }

      for (field <- fields) {
        s"contains a label and optional hint text for the field '$field'" in {
          val doc = asDocument(createView(form))
          val fieldName = field._1
          assertContainsLabel(doc, fieldName, messages(s"site.address.nonUk.$fieldName"))
        }
      }
    }
  }

}
