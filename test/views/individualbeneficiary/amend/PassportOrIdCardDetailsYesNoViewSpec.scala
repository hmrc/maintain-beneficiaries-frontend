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

package views.individualbeneficiary.amend

import forms.YesNoFormProvider
import models.Name
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.individualbeneficiary.amend.PassportOrIdCardDetailsYesNoView
import controllers.individualbeneficiary.amend.routes

class PassportOrIdCardDetailsYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "individualBeneficiary.passportOrIdCardDetailsYesNo"
  val name: Name = Name("First", Some("Middle"), "Last")

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "PassportOrIdCardDetailsYesNo view" must {

    val view = viewFor[PassportOrIdCardDetailsYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, Some(name.displayName), routes.PassportOrIdCardDetailsYesNoController.onSubmit().url)

    behave like pageWithASubmitButton(applyView(form))
  }
}