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

package models.beneficiaries

import java.time.LocalDate

import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class ClassOfBeneficiarySpec extends WordSpec with MustMatchers {

  "ClassOfBeneficiary" must {

    "deserialise from backend JSON" in {

      val json = Json.parse(
        """{
          |                "lineNo": "286",
          |                "description": "Beneficiary Other 25",
          |                "entityStart": "2019-09-23"
          |              }""".stripMargin
      )

      val beneficiary = json.as[ClassOfBeneficiary]
      beneficiary mustBe ClassOfBeneficiary(
        description = "Beneficiary Other 25",
        entityStart = LocalDate.of(2019, 9, 23)
      )

    }

  }

}
