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

package views.individualbeneficiary

import forms.RoleInCompanyFormProvider
import models.beneficiaries.RoleInCompany
import models.{Name, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.individualbeneficiary.RoleInCompanyView

class RoleInCompanyViewSpec extends OptionsViewBehaviours {

  val messageKeyPrefix = "individualBeneficiary.roleInCompany"
  val name: Name = Name("First", None, "Last")
  val form: Form[RoleInCompany] = new RoleInCompanyFormProvider()()
  val view: RoleInCompanyView = viewFor[RoleInCompanyView](Some(emptyUserAnswers))

  "RoleInCompany view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithOptions(form, applyView, RoleInCompany.options)

    behave like pageWithASubmitButton(applyView(form))
  }

}
