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

package pages

import models.UserAnswers
import models.beneficiaries.TypeOfBeneficiaryToAdd
import models.beneficiaries.TypeOfBeneficiaryToAdd._
import play.api.libs.json.JsPath

import scala.util.Try

case object AddNowPage extends QuestionPage[TypeOfBeneficiaryToAdd] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addNow"

  override def cleanup(value: Option[TypeOfBeneficiaryToAdd], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {

      case Some(Individual) =>
        userAnswers.deleteAtPath(classofbeneficiary.basePath)
          .flatMap(_.deleteAtPath(charityortrust.basePath))

      case Some(ClassOfBeneficiaries) =>
        userAnswers.deleteAtPath(individualbeneficiary.basePath)
          .flatMap(_.deleteAtPath(charityortrust.basePath))

      case Some(CharityOrTrust) =>
        userAnswers.deleteAtPath(individualbeneficiary.basePath)
          .flatMap(_.deleteAtPath(classofbeneficiary.basePath))

      case _ =>
        super.cleanup(value, userAnswers)
    }
  }

}
