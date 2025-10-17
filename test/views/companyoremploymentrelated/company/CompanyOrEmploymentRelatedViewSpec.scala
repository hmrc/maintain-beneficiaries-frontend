/*
 * Copyright 2025 HM Revenue & Customs
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

package views.companyoremploymentrelated.company

import forms.CompanyOrEmploymentRelatedBeneficiaryTypeFormProvider
import models.beneficiaries.CompanyOrEmploymentRelatedToAdd
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.companyoremploymentrelated.CompanyOrEmploymentRelatedView

class CompanyOrEmploymentRelatedViewSpec extends OptionsViewBehaviours {

  val messageKeyPrefix = "companyOrEmploymentRelated"

  val form: Form[CompanyOrEmploymentRelatedToAdd] = new CompanyOrEmploymentRelatedBeneficiaryTypeFormProvider()()
  val view: CompanyOrEmploymentRelatedView = viewFor[CompanyOrEmploymentRelatedView](Some(emptyUserAnswers))

  "CompanyOrEmploymentRelated view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithHint(form, applyView, messageKeyPrefix + ".hint")

    behave like pageWithOptions(form, applyView, CompanyOrEmploymentRelatedToAdd.options)

    behave like pageWithASubmitButton(applyView(form))
  }

}
