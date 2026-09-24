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

import play.twirl.api.Html
import views.html.MainTemplate

import scala.jdk.CollectionConverters._

class MainTemplateSpec extends ViewSpecBase {

  val view: MainTemplate = app.injector.instanceOf[MainTemplate]

  val title       = "Test page"
  val mainContent = Html("<p>hello</p>")

  "MainTemplate" must {

    "render the service navigation component" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))

      assertRenderedByCssSelector(doc, ".govuk-service-navigation")
    }

    "request the service navigation component on every generated link to a shared PlatUI page" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))

      val sharedPagePaths = Seq(
        "/accessibility-statement/",
        "/contact/report-technical-problem",
        "/help/cookies",
        "/help/privacy",
        "/help/terms-and-conditions"
      )

      sharedPagePaths.foreach { path =>
        withClue(s"links to $path: ") {
          val hrefs =
            doc.select(s"""a[href*="$path"]""").eachAttr("href").asScala.toSeq

          hrefs must not be empty

          hrefs.foreach { href =>
            href must include("useServiceNavigation")
          }
        }
      }
    }
  }

}
