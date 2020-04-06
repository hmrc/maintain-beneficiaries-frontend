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

package views.charityortrust.charity.add

import java.time.LocalDate

import forms.DateFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.charityortrust.charity.add.StartDateView

class StartDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "charityBeneficiary.startDate"
  val name: String = "Charity"

  val form: Form[LocalDate] = new DateFormProvider().withPrefix(messageKeyPrefix)
  val view: StartDateView = viewFor[StartDateView](Some(emptyUserAnswers))

  "StartDate view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(
      form,
      applyView,
      messageKeyPrefix,
      "value",
      name
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}
