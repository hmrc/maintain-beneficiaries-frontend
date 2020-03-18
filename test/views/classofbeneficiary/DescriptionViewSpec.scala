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

package views.classofbeneficiary

import forms.StandardSingleFieldFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.classofbeneficiary.DescriptionView

class DescriptionViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "classOfBeneficiary.description"

  val form: Form[String] = new StandardSingleFieldFormProvider().withPrefix(messageKeyPrefix)
  val view: DescriptionView = viewFor[DescriptionView](Some(emptyUserAnswers))
  val index = 0

  "Description view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, index)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix, "return.link")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithHint(form, applyView, messageKeyPrefix + ".hint")

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      None,
      controllers.classofbeneficiary.routes.DescriptionController.onSubmit(index).url,
      "value"
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}