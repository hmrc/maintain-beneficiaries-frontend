/*
 * Copyright 2020 HM Revenue & Customs
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

package utils.print

import java.time.LocalDate

import base.SpecBase
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class ClassOfBeneficiaryPrintHelperSpec extends SpecBase {

  val description: String = "Description"
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "ClassOfBeneficiaryPrintHelper" must {

    "generate class of beneficiary section" in {

      val helper = injector.instanceOf[ClassOfBeneficiaryPrintHelper]

      val userAnswers = emptyUserAnswers
        .set(DescriptionPage, description).success.value
        .set(EntityStartPage, date).success.value

      val result = helper(userAnswers)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = Html(messages("classOfBeneficiary.description.checkYourAnswersLabel")), answer = Html("Description"), changeUrl = controllers.classofbeneficiary.add.routes.DescriptionController.onPageLoad().url),
          AnswerRow(label = Html(messages("classOfBeneficiary.entityStart.checkYourAnswersLabel")), answer = Html("3 February 2019"), changeUrl = controllers.classofbeneficiary.add.routes.EntityStartController.onPageLoad().url)
        )
      )
    }
  }
}
