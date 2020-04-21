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

import models.beneficiaries.EmploymentRelatedBeneficiary
import models.{Address, Description, HowManyBeneficiaries, NonUkAddress, UkAddress, UserAnswers}
import org.slf4j.LoggerFactory
import pages.companyoremploymentrelated.employment._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

class EmploymentRelatedBeneficiaryMapper {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  def apply(answers: UserAnswers): Option[EmploymentRelatedBeneficiary] = {
    val readFromUserAnswers: Reads[EmploymentRelatedBeneficiary] =
      (
        NamePage.path.read[String] and
        Reads(_ => JsSuccess(None)) and
        readAddress and
        DescriptionPage.path.read[Description] and
        readNumberOfBeneficiaries and
        StartDatePage.path.read[LocalDate] and
        Reads(_ => JsSuccess(true))
      ) (EmploymentRelatedBeneficiary.apply _ )

    answers.data.validate[EmploymentRelatedBeneficiary](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error("Failed to rehydrate EmploymentRelatedBeneficiary from UserAnswers", errors)
        None
    }
  }

  private def readAddress: Reads[Option[Address]] = {
    AddressUkYesNoPage.path.readNullable[Boolean].flatMap {
      case Some(true) => UkAddressPage.path.readNullable[UkAddress].widen[Option[Address]]
      case Some(false) => NonUkAddressPage.path.readNullable[NonUkAddress].widen[Option[Address]]
      case _ => Reads(_ => JsSuccess(None)).widen[Option[Address]]
    }
  }

  private def readNumberOfBeneficiaries: Reads[String] = {
    NumberOfBeneficiariesPage.path.read[HowManyBeneficiaries].map {
      case HowManyBeneficiaries.Over1 => "1"
      case HowManyBeneficiaries.Over101 => "101"
      case HowManyBeneficiaries.Over201 => "201"
      case HowManyBeneficiaries.Over501 => "501"
      case HowManyBeneficiaries.Over1001 => "1001"
    }
  }
}