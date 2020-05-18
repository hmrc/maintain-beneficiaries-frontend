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

package views.other

import controllers.other.routes
import forms.UkAddressFormProvider
import models.{Mode, NormalMode, UkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.UkAddressViewBehaviours
import views.html.other.UkAddressView

class UkAddressViewSpec extends UkAddressViewBehaviours {

  val messageKeyPrefix = "site.address.uk"
  val description: String = "Other"
  val mode: Mode = NormalMode

  override val form: Form[UkAddress] = new UkAddressFormProvider().apply()

  "UkAddressView" must {

    val view = viewFor[UkAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, mode, description)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, description)

    behave like pageWithBackLink(applyView(form))

    behave like ukAddressPage(
      applyView,
      Some(messageKeyPrefix),
      routes.UkAddressController.onSubmit(mode).url,
      description
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
