/*
 * Copyright 2025 HM Revenue & Customs
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
import models.TypeOfTrust.EmployeeRelated
import play.api.libs.json.{JsPath, Json}

import java.time.{LocalDate, LocalDateTime}
import scala.util.Success

class UserAnswersSpec extends SpecBase {

  "UserAnswers" must {

    "delete data removes data from the Json Object" in {
      val json = Json.obj(
        "field" -> Json.obj(
          "innerfield" -> "value"
        )
      )

      val ua = new UserAnswers(
        internalId = "ID",
        identifier = "UTRUTRUTR",
        sessionId = "sessionId",
        newId = "ID-UTRUTRUTR-sessionId",
        whenTrustSetup = LocalDate.of(1999, 10, 20),
        trustType = Some(TypeOfTrust.WillTrustOrIntestacyTrust),
        data = json,
        isTaxable = true,
        isUnderlyingData5mld = false,
        migratingFromNonTaxableToTaxable = false
      )

      ua.deleteAtPath(JsPath \ "field" \ "innerfield") mustBe Success(ua.copy(data = Json.obj(
        "field" -> Json.obj()
      )))
    }

    "read successfully" when {

      val internalId: String = "internalId"
      val identifier: String = "1234567890"
      val sessionId: String = "sessionId"
      val newId = s"$internalId-$identifier-$sessionId"
      val date: String = "2000-01-01"
      val trustType: TypeOfTrust = EmployeeRelated
      val dateTime: String = "2020-01-01T09:30:15"

      "identifier key is 'utr'" in {

        val json = Json.parse(
          s"""
            |{
            |  "internalId": "$internalId",
            |  "utr": "$identifier",
            |  "sessionId": "$sessionId",
            |  "newId": "$newId",
            |  "whenTrustSetup": "$date",
            |  "trustType": "$trustType",
            |  "data": {},
            |  "updatedAt": {
            |    "$$date": 1577871015000
            |  }
            |}
            |""".stripMargin
        )

        json.as[UserAnswers] mustBe UserAnswers(
          internalId = internalId,
          identifier = identifier,
          sessionId = sessionId,
          newId = s"$internalId-$identifier-$sessionId",
          whenTrustSetup = LocalDate.parse(date),
          trustType = Some(trustType),
          data = Json.obj(),
          updatedAt = LocalDateTime.parse(dateTime),
          isTaxable = true,
          isUnderlyingData5mld = false,
          migratingFromNonTaxableToTaxable = false
        )
      }

      "identifier key is 'identifier'" in {

        val json = Json.parse(
          s"""
             |{
             |  "internalId": "$internalId",
             |  "identifier": "$identifier",
             |  "sessionId": "$sessionId",
             |  "newId": "$internalId-$identifier-$sessionId",
             |  "whenTrustSetup": "$date",
             |  "trustType": "$trustType",
             |  "data": {},
             |  "updatedAt": {
             |    "$$date": 1577871015000
             |  }
             |}
             |""".stripMargin
        )

        json.as[UserAnswers] mustBe UserAnswers(
          internalId = internalId,
          identifier = identifier,
          sessionId = sessionId,
          newId = s"$internalId-$identifier-$sessionId",
          whenTrustSetup = LocalDate.parse(date),
          trustType = Some(trustType),
          data = Json.obj(),
          updatedAt = LocalDateTime.parse(dateTime),
          isTaxable = true,
          isUnderlyingData5mld = false,
          migratingFromNonTaxableToTaxable = false
        )
      }
    }
  }
}
