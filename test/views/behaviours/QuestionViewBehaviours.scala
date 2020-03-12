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

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat

trait QuestionViewBehaviours[A] extends ViewBehaviours {

  val errorKey = "value"
  val errorMessage = "error.number"
  val error = FormError(errorKey, errorMessage)

  val form: Form[A]

  def pageWithTextFields(form: Form[A],
                         createView: Form[A] => HtmlFormat.Appendable,
                         messageKeyPrefix: String,
                         messageKeyParam: Option[String],
                         expectedFormAction: String,
                         fields: String*) = {

    "behave like a question page" when {

      "rendered" must {

        for (field <- fields) {

          s"contain an input for $field" in {
            val doc = asDocument(createView(form))
            assertRenderedById(doc, field)
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
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title", messageKeyParam.getOrElse(""))}""")
        }
      }

      for (field <- fields) {

        s"rendered with an error with field '$field'" must {

          "show an error summary" in {

            val doc = asDocument(createView(form.withError(FormError(field, "error"))))
            assertRenderedById(doc, "error-summary-heading")
          }

          s"show an error associated with the field '$field'" in {

            val doc = asDocument(createView(form.withError(FormError(field, "error"))))
            val inputField = doc.getElementById(field)
            inputField.attr("aria-describedby").split(" ").foreach { idOfDescribedByTarget =>
              doc.getElementById(idOfDescribedByTarget) mustNot be(null)
            }


            doc.select(s"label[for='$field']").size() mustBe 1
          }
        }
      }
    }
  }

  def pageWithPassportOrIDCardDetailsFields(form: Form[A],
                       createView: Form[A] => HtmlFormat.Appendable,
                       messageKeyPrefix: String,
                       expectedFormAction: String,
                       textFields: Seq[(String, Option[String])],
                       dateKey : String,
                       args: String*) = {

    val dateFields = Seq(s"${dateKey}_day", s"${dateKey}_month", s"${dateKey}_year")

    "behave like a passportOrIDCard page" when {

      "rendered" must {

        for (field <- textFields) {

          s"contain an input for $field" in {
            val doc = asDocument(createView(form))
            assertRenderedById(doc, field._1)
          }
        }

        for (field <- dateFields) {

          s"contain an input for $field" in {
            val doc = asDocument(createView(form))
            assertRenderedById(doc, field)
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
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title", args: _*)}""")
        }
      }

      for (field <- textFields) {

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

          s"contains a label and optional hint text for the field '$field'" in {
            val doc = asDocument(createView(form))
            val fieldName = field._1
            val fieldHint = field._2 map (k => messages(k))
            assertContainsLabel(doc, fieldName, messages(s"$messageKeyPrefix.$fieldName"), fieldHint)
          }
        }
      }

      "rendered with any date field error" must {

        "show an error in the legend" in {

          val doc = asDocument(createView(form.withError(FormError(dateKey, "error"))))
          assertRenderedById(doc, s"error-message-$dateKey-input")
        }

        "show an error prefix in the browser title" in {

          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title", args: _*)}""")
        }
      }

    }
  }

  def pageWithDateFields(form: Form[A],
                         createView: Form[A] => HtmlFormat.Appendable,
                         messageKeyPrefix: String,
                         key: String,
                         args: String*) = {

    val fields = Seq(s"${key}_day", s"${key}_month", s"${key}_year")

    "behave like a question page" when {

      "rendered" must {

        for (field <- fields) {

          s"contain an input for $field" in {
            val doc = asDocument(createView(form))
            assertRenderedById(doc, field)
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
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${messages(s"$messageKeyPrefix.title", args: _*)}""")
        }
      }

      s"rendered with an error" must {

        "show an error summary" in {

          val doc = asDocument(createView(form.withError(FormError(key, "error"))))
          assertRenderedById(doc, "error-summary-heading")
        }

        s"show an error in the legend" in {

          val doc = asDocument(createView(form.withError(FormError(key, "error"))))
          assertRenderedById(doc, s"error-message-$key-input")
        }
      }
    }
  }
}
