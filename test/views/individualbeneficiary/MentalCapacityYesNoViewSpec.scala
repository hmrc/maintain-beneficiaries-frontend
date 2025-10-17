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

package views.individualbeneficiary

import forms.YesNoDontKnowFormProvider
import models.{Name, NormalMode, YesNoDontKnow}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioOption
import views.behaviours.{OptionsViewBehaviours, QuestionViewBehaviours}
import views.html.individualbeneficiary.MentalCapacityYesNoView

class MentalCapacityYesNoViewSpec extends QuestionViewBehaviours[YesNoDontKnow] with OptionsViewBehaviours {

  val messageKeyPrefix = "individualBeneficiary.mentalCapacityYesNo"
  val name: Name = Name("First", None, "Last")

  val form: Form[YesNoDontKnow] = new YesNoDontKnowFormProvider().withPrefix(messageKeyPrefix)

  "MentalCapacityYesNoView" must {

    val view = viewFor[MentalCapacityYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName,"p1", "p2", "bulletpoint1", "bulletpoint2", "bulletpoint3", "bulletpoint4", "p3", "p4", "under16.p1", "under16.b1", "under16.b2", "heading2", "p5", "p6")

    behave like pageWithBackLink(applyView(form))

    val options = List(
      RadioOption(id = "value-yes", value = YesNoDontKnow.Yes.toString, messageKey = "site.yes"),
      RadioOption(id = "value-no", value = YesNoDontKnow.No.toString, messageKey = "site.no"),
      RadioOption(id = "value-dontKnow", value = YesNoDontKnow.DontKnow.toString, messageKey = "site.iDontKnow")
    )

    behave like pageWithOptions[YesNoDontKnow](form, applyView, options)

    behave like pageWithASubmitButton(applyView(form))
  }
}
