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

import models.beneficiaries.CharityBeneficiary
import models.{Address, NonUkAddress, UkAddress, UserAnswers}
import org.slf4j.LoggerFactory
import pages.charityortrust.charity._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

class CharityBeneficiaryMapper {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  def apply(answers: UserAnswers): Option[CharityBeneficiary] = {
    val readFromUserAnswers: Reads[CharityBeneficiary] =
      (
        NamePage.path.read[String] and
        Reads(_ => JsSuccess(None)) and
        AddressUkYesNoPage.path.readNullable[Boolean].flatMap {
          case Some(true) => UkAddressPage.path.readNullable[UkAddress].widen[Option[Address]]
          case Some(false) => NonUkAddressPage.path.readNullable[NonUkAddress].widen[Option[Address]]
          case _ => Reads(_ => JsSuccess(None)).widen[Option[Address]]
        } and
        ShareOfIncomePage.path.readNullable[String] and
        DiscretionYesNoPage.path.read[Boolean] and
        StartDatePage.path.read[LocalDate] and
        Reads(_ => JsSuccess(true))
      ).apply(CharityBeneficiary.apply _ )

    answers.data.validate[CharityBeneficiary](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error("Failed to rehydrate CharityBeneficiary from UserAnswers", errors)
        None
    }
  }
}