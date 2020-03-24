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

import com.google.inject.Inject
import models.UserAnswers
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class ClassOfBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                              countryOptions: CountryOptions
                                 ) {

  def apply(userAnswers: UserAnswers, description: String)(implicit messages: Messages) = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, description, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(DescriptionPage, "classOfBeneficiary.description", controllers.classofbeneficiary.add.routes.DescriptionController.onPageLoad().url),
        bound.dateQuestion(EntityStartPage, "classOfBeneficiary.entityStart", controllers.classofbeneficiary.add.routes.EntityStartController.onPageLoad().url)
      ).flatten
    )
  }
}
