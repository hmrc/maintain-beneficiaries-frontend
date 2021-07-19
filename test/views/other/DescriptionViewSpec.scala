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

package views.other

import controllers.other.routes
import forms.DescriptionFormProvider
import models.{Mode, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.other.DescriptionView

class DescriptionViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "otherBeneficiary.description"
  val mode: Mode = NormalMode
  val form: Form[String] = new DescriptionFormProvider().withPrefix(messageKeyPrefix, 70)
  val view: DescriptionView = viewFor[DescriptionView](Some(emptyUserAnswers))

  "Description view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, mode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithHint(form, applyView, s"$messageKeyPrefix.hint")

    behave like stringPage(
      form,
      applyView,
      messageKeyPrefix,
      None,
      routes.DescriptionController.onSubmit(mode).url
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}
