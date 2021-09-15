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
import play.api.libs.json.Json

import java.time.LocalDate

class IdentificationSpec extends SpecBase {

  "Identification" must {

    "read from json" when {

      val country = "GB"
      val number = "1234567890"
      val date = "2000-01-01"

      "passport" in {
        val json = Json.parse(
          s"""
             |{
             |  "passport": {
             |    "countryOfIssue": "$country",
             |    "number": "$number",
             |    "expirationDate": "$date",
             |    "detailsType": "passport"
             |  }
             |}
             |""".stripMargin)

        val result = json.as[IndividualIdentification]
        result mustBe Passport(country, number, LocalDate.parse(date))
      }

      "id card" in {
        val json = Json.parse(
          s"""
             |{
             |  "passport": {
             |    "countryOfIssue": "$country",
             |    "number": "$number",
             |    "expirationDate": "$date",
             |    "detailsType": "id-card"
             |  }
             |}
             |""".stripMargin)

        val result = json.as[IndividualIdentification]
        result mustBe IdCard(country, number, LocalDate.parse(date))
      }

      "passport or id card" in {
        val json = Json.parse(
          s"""
             |{
             |  "passport": {
             |    "countryOfIssue": "$country",
             |    "number": "$number",
             |    "expirationDate": "$date"
             |  }
             |}
             |""".stripMargin)

        val result = json.as[IndividualIdentification]
        result mustBe CombinedPassportOrIdCard(country, number, LocalDate.parse(date))
      }

      "nino" in {
        val json = Json.parse(
          s"""
             |{
             |  "nino": "$number"
             |}
             |""".stripMargin)

        val result = json.as[IndividualIdentification]
        result mustBe NationalInsuranceNumber(number)
      }
    }
  }

}
