/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.IncomePercentageFormProvider
import models.{Mode, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.other.ShareOfIncomeView

class ShareOfIncomeViewSpec extends QuestionViewBehaviours[Int] {

  val messageKeyPrefix = "otherBeneficiary.shareOfIncome"
  val description: String = "Other"
  val mode: Mode = NormalMode

  val form: Form[Int] = new IncomePercentageFormProvider().withPrefix(messageKeyPrefix)
  val view: ShareOfIncomeView = viewFor[ShareOfIncomeView](Some(emptyUserAnswers))

  "ShareOfIncome view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, mode, description)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, description)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      None,
      routes.DescriptionController.onSubmit(mode).url,
      "value"
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}
