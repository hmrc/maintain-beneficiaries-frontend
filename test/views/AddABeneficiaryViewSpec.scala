/*
 * Copyright 2021 HM Revenue & Customs
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

import forms.AddABeneficiaryFormProvider
import models.AddABeneficiary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.addAnother.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.AddABeneficiaryView

class AddABeneficiaryViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val featureUnavailable: Option[String] = Some("/feature-not-available")

  val completeBeneficiaries = Seq(
    AddRow("beneficiary one", "Individual Beneficiary", featureUnavailable, featureUnavailable),
    AddRow("beneficiary two", "Individual Beneficiary", featureUnavailable, featureUnavailable),
    AddRow("beneficiary three", "Individual Beneficiary", featureUnavailable, featureUnavailable),
    AddRow("class of beneficiary", "Class of beneficiaries", featureUnavailable, featureUnavailable)
  )

  val inProgressBeneficiaries = Seq(
    AddRow("beneficiary four", "Individual Beneficiary", featureUnavailable, featureUnavailable),
    AddRow("beneficiary five", "Individual Beneficiary", featureUnavailable, featureUnavailable),
    AddRow("beneficiary six", "Individual Beneficiary", featureUnavailable, featureUnavailable),
    AddRow("class of beneficiary 2", "Class of beneficiaries", featureUnavailable, featureUnavailable)
  )

  val messageKeyPrefix = "addABeneficiary"

  val form = new AddABeneficiaryFormProvider()()

  val view: AddABeneficiaryView = viewFor[AddABeneficiaryView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, Nil, Nil, "Add a beneficiary", Nil, migrating = false)(fakeRequest, messages)

  def applyView(form: Form[_], inProgressBeneficiaries: Seq[AddRow], completeBeneficiaries: Seq[AddRow], count: Int, maxedOut: List[String]): HtmlFormat.Appendable = {
    val title = if (count > 1) s"The trust has $count beneficiaries" else "Add a beneficiary"
    view.apply(form, inProgressBeneficiaries, completeBeneficiaries, title, maxedOut, migrating = false)(fakeRequest, messages)
  }

  "AddABeneficiaryView" when {

    "there is no beneficiary data" must {

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithOptions(form, applyView, AddABeneficiary.options)
    }

    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressBeneficiaries, Nil, 4, Nil)

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithInProgressTabularData(viewWithData, inProgressBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options)
    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeBeneficiaries, 4, Nil)

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithCompleteTabularData(viewWithData, completeBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options)
    }

    "there is both in progress and complete data" must {

      val viewWithData = applyView(form, inProgressBeneficiaries, completeBeneficiaries, 8, Nil)

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "8")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressBeneficiaries, completeBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options)
    }

    "there is one maxed out beneficiary" must {
      val viewWithData = applyView(form, inProgressBeneficiaries, completeBeneficiaries, 8, List("Charity"))

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "8")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressBeneficiaries, completeBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options)

      "content shows maxed beneficiary" in {
        val doc = asDocument(viewWithData)

        assertContainsText(doc, "You cannot add another charity as you have entered a maximum of 25.")
        assertContainsText(doc, "Check the beneficiaries you have added. If you have further beneficiaries to add within this type, write to HMRC with their details.")
      }
    }

    "there are many maxed out beneficiaries" must {
      val viewWithData = applyView(form, inProgressBeneficiaries, completeBeneficiaries, 8, List("Charity", "Individual"))

      behave like dynamicTitlePage(viewWithData, "addABeneficiary.count", "8")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressBeneficiaries, completeBeneficiaries)

      behave like pageWithOptions(form, applyView, AddABeneficiary.options)

      "content shows maxed beneficiaries" in {
        val doc = asDocument(viewWithData)

        assertContainsText(doc, "You have entered the maximum number of beneficiaries for:")
        assertContainsText(doc, "Charity")
        assertContainsText(doc, "Individual")
        assertContainsText(doc, "Check the beneficiaries you have added. If you have further beneficiaries to add within these types, write to HMRC with their details.")
      }
    }
  }

}
