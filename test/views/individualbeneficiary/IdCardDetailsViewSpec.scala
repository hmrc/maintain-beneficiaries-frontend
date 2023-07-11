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

import forms.IdCardDetailsFormProvider
import models.beneficiaries.Beneficiaries
import models.{IdCard, Name, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.behaviours.QuestionViewBehaviours
import views.html.individualbeneficiary.IdCardDetailsView

class IdCardDetailsViewSpec extends QuestionViewBehaviours[IdCard] {

  val messageKeyPrefix = "individualBeneficiary.idCardDetails"
  val name: Name = Name("First", Some("Middle"), "Last")
  private val beneficiaries: Beneficiaries = Beneficiaries()

  override val form: Form[IdCard] = new IdCardDetailsFormProvider(frontendAppConfig).withPrefix(messageKeyPrefix, beneficiaries)

  "IdCardDetails view" must {

    val view = viewFor[IdCardDetailsView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options()

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, countryOptions, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    "fields" must {

      behave like pageWithPassportOrIDCardDetailsFields(
        form,
        applyView,
        messageKeyPrefix,
        Seq(("country", None), ("number", None)),
        "expiryDate",
        name.displayName
      )
    }

    behave like pageWithASubmitButton(applyView(form))
  }
}
