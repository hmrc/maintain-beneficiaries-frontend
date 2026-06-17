/*
 * Copyright 2026 HM Revenue & Customs
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
import views.behaviours.ViewBehaviours
import views.html.OutOfBoundsPageNotFoundView

class OutOfBoundsPageNotFoundViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "outOfBoundsPageNotFound"

  "OutOfBoundsPageNotFound view" must {

    val view = viewFor[OutOfBoundsPageNotFoundView](Some(emptyUserAnswers))

    val applyView: HtmlFormat.Appendable = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, messageKeyPrefix, "p1", "p2")

    behave like pageWithoutBackLink(applyView)

    "display the bullet point links with the correct text and hrefs" in {
      val doc = asDocument(applyView)

      val links = doc.select("ul.govuk-list--bullet li a.govuk-link")
      links.size() mustBe 2

      val overviewLink = links.get(0)

      overviewLink.text()       mustBe messages(s"$messageKeyPrefix.bullet1")
      overviewLink.attr("href") mustBe frontendAppConfig.maintainATrustOverview

      val addBeneficiaryLink = links.get(1)

      addBeneficiaryLink.text()       mustBe messages(s"$messageKeyPrefix.bullet2")
      addBeneficiaryLink.attr("href") mustBe
        controllers.routes.AddABeneficiaryController.onPageLoad().url
    }
  }

}
