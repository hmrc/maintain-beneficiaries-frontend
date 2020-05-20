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

package views.companyoremploymentrelated.employment

import controllers.companyoremploymentrelated.employment.routes
import forms.UkAddressFormProvider
import models.{NormalMode, UkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.UkAddressViewBehaviours
import views.html.companyoremploymentrelated.employment.UkAddressView

class UkAddressViewSpec extends UkAddressViewBehaviours {

  val messageKeyPrefix = "site.address.uk"
  val name: String = "Large"

  override val form: Form[UkAddress] = new UkAddressFormProvider().apply()

  "UkAddressView" must {

    val view = viewFor[UkAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like ukAddressPage(
      applyView,
      Some(messageKeyPrefix),
      routes.UkAddressController.onSubmit(NormalMode).url,
      name
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
