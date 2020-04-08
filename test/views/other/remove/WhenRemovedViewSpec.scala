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

package views.other.remove

import java.time.LocalDate

import forms.DateRemovedFromTrustFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.other.remove.WhenRemovedView

class WhenRemovedViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "otherBeneficiary.whenRemoved"
  val index = 0
  val description = "Description"

  override val form: Form[LocalDate] = new DateRemovedFromTrustFormProvider().withPrefixAndEntityStartDate(messageKeyPrefix, LocalDate.now())

  "whenRemoved view" must {

    val view = viewFor[WhenRemovedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, index, description)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, description)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithHint(form, applyView, messageKeyPrefix + ".hint")

    "fields" must {

      behave like pageWithDateFields(
        form,
        applyView,
        messageKeyPrefix,
        "value",
        description
      )
    }

    behave like pageWithASubmitButton(applyView(form))
  }
}