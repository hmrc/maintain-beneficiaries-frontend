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

package views

import forms.AddBeneficiaryTypeFormProvider
import models.beneficiaries.TypeOfBeneficiaryToAdd
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.AddNowView

class AddNowViewSpec extends OptionsViewBehaviours {

  val messageKeyPrefix = "addNow"

  val form: Form[TypeOfBeneficiaryToAdd] = new AddBeneficiaryTypeFormProvider()()
  val view: AddNowView = viewFor[AddNowView](Some(emptyUserAnswers))

  "Description view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithHint(form, applyView, messageKeyPrefix + ".hint")

    behave like pageWithOptions(form, applyView, TypeOfBeneficiaryToAdd.options)

    behave like pageWithASubmitButton(applyView(form))
  }

}
