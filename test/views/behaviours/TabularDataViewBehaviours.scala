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

package views.behaviours

import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import play.twirl.api.HtmlFormat
import viewmodels.addAnother.AddRow
import views.ViewSpecBase

trait TabularDataViewBehaviours extends ViewSpecBase {

  private def assertDataList(doc: Document, parentElementId: String, data: Seq[AddRow]): Unit = {

    val container = doc.getElementById(parentElementId)

    val elements: Elements = container.select(".hmrc-add-to-a-list__contents")
    elements.size mustBe data.size

    val dataWithIndex = data.zipWithIndex

    for ((item, index) <- dataWithIndex) {
      val element = elements.get(index)

      element.text must include(item.name)
      element.text must include(item.typeLabel)

      val changeLink = element.getElementsByClass("hmrc-add-to-a-list__change").first()

      item.changeUrl.map(url => changeLink.getElementsByTag("a").attr("href") must include(url))
      changeLink.text must include(s"Change ${item.name}")

      val removeLink = element.getElementsByClass("hmrc-add-to-a-list__remove").first()

      item.removeUrl.map(url => removeLink.getElementsByTag("a").attr("href") must include(url))
      removeLink.text must include(s"Remove ${item.name}")
    }
  }

  def pageWithNoTabularData(view: HtmlFormat.Appendable): Unit = {

    "behave like a page with no tabular data" when {

      "render with no data list headings" in {
        val doc = asDocument(view)
        assertNotRenderedById(doc, "data-list-heading--complete")
        assertNotRenderedById(doc, "data-list-heading--inprogress")
      }

      "rendered with no data" in {
        val doc = asDocument(view)
        assertElementNotPresent(doc, "dl")
      }
    }
  }

  def pageWithInProgressTabularData(view: HtmlFormat.Appendable, data: Seq[AddRow]): Unit = {

    "behave like a page with incomplete tabular data" should {

      "render a h2" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "data-list-heading--inprogress")
      }

      "render an add to list" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "data-list--inprogress")
        assertNotRenderedById(doc, "data-list--complete")
        assertNotRenderedById(doc, "data-list-heading--complete")
      }

      "render a row for each data item" in {
        val doc = asDocument(view)
        assertDataList(doc, "data-list--inprogress", data)
      }
    }
  }

  def pageWithCompleteTabularData(view: HtmlFormat.Appendable, data: Seq[AddRow]): Unit = {

    "behave like a page with complete tabular data" should {

      "render a h2" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "data-list-heading--complete")
      }

      "render an add to list" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "data-list--complete")
        assertNotRenderedById(doc, "data-list--inprogress")
        assertNotRenderedById(doc, "data-list-heading--inprogress")
      }

      "render a row for each data item" in {
        val doc = asDocument(view)
        assertDataList(doc, "data-list--complete", data)
      }
    }
  }

  def pageWithTabularData(view: HtmlFormat.Appendable,
                          inProgressData: Seq[AddRow],
                          completeData: Seq[AddRow]): Unit = {

    "behave like a page with complete and incomplete tabular data" should {

      "render a h2" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "data-list-heading--inprogress")
        assertRenderedById(doc, "data-list-heading--complete")
      }

      "render an add to list" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "data-list--inprogress")
        assertRenderedById(doc, "data-list--complete")
      }

      "render a row for each data item" in {
        val doc = asDocument(view)
        assertDataList(doc, "data-list--inprogress", inProgressData)
        assertDataList(doc, "data-list--complete", completeData)
      }
    }
  }

}
