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

package utils.mappers

import models.UserAnswers
import models.beneficiaries.Beneficiary
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Reads}

import scala.reflect.{ClassTag, classTag}

class Mapper[T <: Beneficiary : ClassTag] extends Logging {

  def mapAnswersWithExplicitReads(answers: UserAnswers, reads: Reads[T]): Option[T] = {
    answers.data.validate[T](reads) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error(s"[UTR: ${answers.utr}] Failed to rehydrate ${classTag[T].runtimeClass.getSimpleName} from UserAnswers due to $errors")
        None
    }
  }

}
