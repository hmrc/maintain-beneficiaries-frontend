/*
 * Copyright 2022 HM Revenue & Customs
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

import models._
import models.beneficiaries.EmploymentRelatedBeneficiary
import pages.QuestionPage
import pages.companyoremploymentrelated.employment._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsSuccess, Reads}

import java.time.LocalDate

class EmploymentRelatedBeneficiaryMapper extends Mapper[EmploymentRelatedBeneficiary] {

  override val reads: Reads[EmploymentRelatedBeneficiary] = (
    NamePage.path.read[String] and
      Reads(_ => JsSuccess(None)) and
      readAddress and
      DescriptionPage.path.read[Description] and
      NumberOfBeneficiariesPage.path.read[HowManyBeneficiaries] and
      readCountryOfResidence and
      StartDatePage.path.read[LocalDate] and
      Reads(_ => JsSuccess(true))
    )(EmploymentRelatedBeneficiary.apply _)

  override def ukAddressYesNoPage: QuestionPage[Boolean] = AddressUkYesNoPage
  override def ukAddressPage: QuestionPage[UkAddress] = UkAddressPage
  override def nonUkAddressPage: QuestionPage[NonUkAddress] = NonUkAddressPage

  override def countryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceYesNoPage
  override def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceUkYesNoPage
  override def countryOfResidencePage: QuestionPage[String] = CountryOfResidencePage

}
