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

package views.individualbeneficiary

import forms.NationalInsuranceNumberFormProvider
import models.{Mode, Name, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.individualbeneficiary.NationalInsuranceNumberView

class NationalInsuranceNumberViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "individualBeneficiary.nationalInsuranceNumber"
  val name: Name = Name("First", Some("Middle"), "Last")
  val mode: Mode = NormalMode
  override val form: Form[String] = new NationalInsuranceNumberFormProvider().apply(messageKeyPrefix, Nil)

  "NationalInsuranceNumber view" must {

    val view = viewFor[NationalInsuranceNumberView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, mode, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(form, applyView,
      messageKeyPrefix,
      Some(name.displayName),
      ""
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
