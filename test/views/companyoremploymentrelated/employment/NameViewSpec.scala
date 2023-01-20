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

package views.companyoremploymentrelated.employment

import controllers.companyoremploymentrelated.employment.routes
import forms.StringFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.companyoremploymentrelated.employment.NameView

class NameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "employmentBeneficiary.name"

  val form: Form[String] = new StringFormProvider().withPrefix(messageKeyPrefix, 105)
  val view: NameView = viewFor[NameView](Some(emptyUserAnswers))

  "Name view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      None,
      routes.NameController.onSubmit(NormalMode).url,
      "value"
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}
