/*
 * Copyright 2022 HM Revenue & Customs
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

package views.companyoremploymentrelated.employment.amend

import play.twirl.api.HtmlFormat
import viewmodels.AnswerSection
import views.behaviours.ViewBehaviours
import views.html.companyoremploymentrelated.employment.amend.CheckDetailsView

class CheckDetailsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "employmentBeneficiary.checkDetails"

  "CheckDetails view" must {
    val index = 0
    val view = viewFor[CheckDetailsView](Some(emptyUserAnswers))

    def applyView(): HtmlFormat.Appendable =
      view.apply(Seq(AnswerSection(None, Seq())), index)(fakeRequest, messages)

    behave like normalPage(applyView(), messageKeyPrefix)

    behave like pageWithBackLink(applyView())

    behave like pageWithASubmitButton(applyView())
  }
}

