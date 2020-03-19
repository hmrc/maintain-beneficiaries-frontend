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

package utils.mappers

import java.time.LocalDate

import models.UserAnswers
import models.beneficiaries.ClassOfBeneficiary
import org.slf4j.LoggerFactory
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.libs.functional.syntax._

class ClassOfBeneficiaryMapper {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  def apply(answers: UserAnswers): Option[ClassOfBeneficiary] = {
    val readFromUserAnswers: Reads[ClassOfBeneficiary] =
      (
        DescriptionPage.path.read[String] and
        EntityStartPage.path.read[LocalDate]
      ).apply(ClassOfBeneficiary.apply _)

    answers.data.validate[ClassOfBeneficiary](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error("Failed to rehydrate ClassOfBeneficiary from UserAnswers", errors)
        None
    }
  }
}