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

import utils.Constants.GB
import models._
import models.beneficiaries.EmploymentRelatedBeneficiary
import pages.companyoremploymentrelated.employment._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsSuccess, Reads}

import java.time.LocalDate

class EmploymentRelatedBeneficiaryMapper extends Mapper[EmploymentRelatedBeneficiary] {

  def apply(answers: UserAnswers): Option[EmploymentRelatedBeneficiary] = {
    val readFromUserAnswers: Reads[EmploymentRelatedBeneficiary] = (
      NamePage.path.read[String] and
        Reads(_ => JsSuccess(None)) and
        readAddress and
        DescriptionPage.path.read[Description] and
        NumberOfBeneficiariesPage.path.read[HowManyBeneficiaries] and
        CountryOfResidenceYesNoPage.path.readNullable[Boolean].flatMap[Option[String]] {
          case Some(true) => CountryOfResidenceUkYesNoPage.path.read[Boolean].flatMap {
            case true => Reads(_ => JsSuccess(Some(GB)))
            case false => CountryOfResidencePage.path.read[String].map(Some(_))
          }
          case _ => Reads(_ => JsSuccess(None))
        } and
        StartDatePage.path.read[LocalDate] and
        Reads(_ => JsSuccess(true))
      )(EmploymentRelatedBeneficiary.apply _ )

    mapAnswersWithExplicitReads(answers, readFromUserAnswers)
  }

  private def readAddress: Reads[Option[Address]] = {
    AddressUkYesNoPage.path.readNullable[Boolean].flatMap {
      case Some(true) => UkAddressPage.path.readNullable[UkAddress].widen[Option[Address]]
      case Some(false) => NonUkAddressPage.path.readNullable[NonUkAddress].widen[Option[Address]]
      case _ => Reads(_ => JsSuccess(None)).widen[Option[Address]]
    }
  }
}
