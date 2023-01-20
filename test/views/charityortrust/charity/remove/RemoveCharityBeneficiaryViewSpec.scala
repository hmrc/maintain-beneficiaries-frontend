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

package views.charityortrust.charity.remove

import controllers.charityortrust.charity.remove.routes
import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.charityortrust.charity.remove.RemoveIndexView

class RemoveCharityBeneficiaryViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "removeCharityBeneficiaryYesNo"
  val form = (new YesNoFormProvider).withPrefix(messageKeyPrefix)
  val name = "Trustee Name"
  val index = 0

  "removeCharityBeneficiary view" must {

    val view = viewFor[RemoveIndexView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, index, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, Some(name), routes.RemoveCharityBeneficiaryController.onSubmit(index).url)
  }
}
