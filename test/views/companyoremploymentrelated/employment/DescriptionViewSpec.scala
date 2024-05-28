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

package views.companyoremploymentrelated.employment

import controllers.companyoremploymentrelated.employment.routes
import forms.EmploymentRelatedBeneficiaryDescriptionFormProvider
import models.{Description, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.companyoremploymentrelated.employment.DescriptionView

class DescriptionViewSpec extends QuestionViewBehaviours[Description] {

  val messageKeyPrefix = "employmentBeneficiary.description"

  val form: Form[Description] = new EmploymentRelatedBeneficiaryDescriptionFormProvider().withPrefix(messageKeyPrefix)
  val view: DescriptionView = viewFor[DescriptionView](Some(emptyUserAnswers))

  "Description view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPageWithCaption(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithHint(form, applyView, messageKeyPrefix + ".hint")

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      None,
      routes.NameController.onSubmit(NormalMode).url,
      "description",
      "description1",
      "description2",
      "description3",
      "description4"
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}
