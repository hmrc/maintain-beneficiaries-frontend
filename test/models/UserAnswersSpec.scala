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

package models

import java.time.LocalDate

import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsPath, Json}

import scala.util.Success

class UserAnswersSpec extends FreeSpec with MustMatchers {
  "delete data removes data from the Json Object" in {
    val json = Json.obj(
      "field" -> Json.obj(
        "innerfield" -> "value"
      )
    )

    val ua = new UserAnswers(
      "ID",
      "UTRUTRUTR",
      LocalDate.of(1999, 10, 20),
      json
    )

    ua.deleteAtPath(JsPath \ "field" \ "innerfield") mustBe Success(ua.copy(data = Json.obj(
      "field" -> Json.obj()
    )))
  }
}
