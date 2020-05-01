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

package views.charityortrust.charity

import controllers.charityortrust.charity.routes
import forms.IncomePercentageFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.charityortrust.charity.ShareOfIncomeView

class ShareOfIncomeViewSpec extends QuestionViewBehaviours[Int] {

  val messageKeyPrefix = "charityBeneficiary.shareOfIncome"
  val name: String = "Charity"

  val form: Form[Int] = new IncomePercentageFormProvider().withPrefix(messageKeyPrefix)
  val view: ShareOfIncomeView = viewFor[ShareOfIncomeView](Some(emptyUserAnswers))

  "ShareOfIncome view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

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
