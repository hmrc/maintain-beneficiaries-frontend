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

package models

import base.SpecBase
import models.HowManyBeneficiaries._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.JsString

class HowManyBeneficiariesSpec extends SpecBase with ScalaCheckPropertyChecks {

  "HowManyBeneficiaries" must {

    "handle unexpected number of beneficiaries" when {

      "0 to 100" in {
        val min = 0
        val max = 100
        forAll(Gen.choose(min, max)) { numberOfBeneficiaries =>
          JsString(numberOfBeneficiaries.toString).as[HowManyBeneficiaries] mustBe Over1
        }
      }

      "101 to 200" in {
        val min = 101
        val max = 200
        forAll(Gen.choose(min, max)) { numberOfBeneficiaries =>
          JsString(numberOfBeneficiaries.toString).as[HowManyBeneficiaries] mustBe Over101
        }
      }

      "201 to 500" in {
        val min = 201
        val max = 500
        forAll(Gen.choose(min, max)) { numberOfBeneficiaries =>
          JsString(numberOfBeneficiaries.toString).as[HowManyBeneficiaries] mustBe Over201
        }
      }

      "501 to 1000" in {
        val min = 501
        val max = 999
        forAll(Gen.choose(min, max)) { numberOfBeneficiaries =>
          JsString(numberOfBeneficiaries.toString).as[HowManyBeneficiaries] mustBe Over501
        }
      }

      "1000+" in {
        val min = 1000
        val max = Int.MaxValue
        forAll(Gen.choose(min, max)) { numberOfBeneficiaries =>
          JsString(numberOfBeneficiaries.toString).as[HowManyBeneficiaries] mustBe Over1001
        }
      }
    }
  }
}
