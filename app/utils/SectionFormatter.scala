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

package utils

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{AnswerRow, AnswerSection, RepeaterAnswerSection, Section}

object SectionFormatter {

  def formatSections(answerSections: Seq[Section])(implicit messages: Messages): Seq[SummaryListRow] = {
    answerSections.flatMap {
      case a: AnswerSection => formatAnswerSection(a)
      case _: RepeaterAnswerSection => throw new NotImplementedError("Not used anywhere in code.")
    }
  }

  private def formatAnswerSection(section: AnswerSection)(implicit messages: Messages): Seq[SummaryListRow] = {
    section.rows.zipWithIndex.map {
      case (row:AnswerRow, i: Int) => {
        SummaryListRow(
          key = Key(classes = "govuk-!-width-two-thirds", content = Text(messages(row.label, row.labelArg))),
          value = Value(HtmlContent(row.answer)),
          actions = Option(Actions(items = Seq(ActionItem(href=row.changeUrl.getOrElse(""),
            classes = s"change-link-${i}",
            visuallyHiddenText = Some(messages(row.label, row.labelArg)),
            content = Text(messages("site.edit")))))
          )
        )
      }
    }

  }

}