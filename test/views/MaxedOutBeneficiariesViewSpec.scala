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

package views

import play.twirl.api.HtmlFormat
import viewmodels.addAnother.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.MaxedOutBeneficiariesView

class MaxedOutBeneficiariesViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val messageKeyPrefix = "addABeneficiary"

  val view: MaxedOutBeneficiariesView = viewFor[MaxedOutBeneficiariesView](Some(emptyUserAnswers))

  val featureUnavailable: Option[String] = Some("/feature-not-available")

  val rows = List(AddRow("Charity", "", featureUnavailable, featureUnavailable), AddRow("Trust", "", None, None))

  def applyView(migrating: Boolean): HtmlFormat.Appendable =
    view.apply(rows, rows, "Add a beneficiary", migrating)(fakeRequest, messages)

  "MaxedOutBeneficiaryView" when {

    "migrating from non-taxable to taxable" when {

      val migrating: Boolean = true

      "there are many maxed out beneficiaries" must {
        val view = applyView(migrating)

        behave like normalPage(view, messageKeyPrefix)

        behave like pageWithBackLink(view)

        behave like pageWithTabularData(view, rows, rows, migrating)

        behave like pageWithASubmitButton(view)

        "content shows maxed beneficiaries" in {
          val doc = asDocument(view)

          assertContainsText(doc, "You cannot enter another beneficiary as you have entered a maximum of 175.")
          assertContainsText(doc, "Check the beneficiaries you have added. If you have further beneficiaries to add, write to HMRC with their details.")
        }

        behave like pageWithWarning(applyView(migrating))

        "render additional content" in {

          val doc = asDocument(applyView(migrating))
          assertContainsText(doc, messages("addABeneficiary.transition.subheading"))
          assertContainsText(doc, messages("addABeneficiary.transition.warning"))
          assertContainsText(doc, messages("addABeneficiary.transition.p1"))
        }
      }
    }

    "not migrating from non-taxable to taxable" when {

      val migrating: Boolean = false

      "there are many maxed out beneficiaries" must {
        val view = applyView(migrating)

        behave like normalPage(view, messageKeyPrefix)

        behave like pageWithBackLink(view)

        behave like pageWithTabularData(view, rows, rows, migrating)

        behave like pageWithASubmitButton(view)

        "content shows maxed beneficiaries" in {
          val doc = asDocument(view)

          assertContainsText(doc, "You cannot enter another beneficiary as you have entered a maximum of 175.")
          assertContainsText(doc, "Check the beneficiaries you have added. If you have further beneficiaries to add, write to HMRC with their details.")
        }
      }
    }
  }

}
