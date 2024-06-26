/*
 * Copyright 2024 HM Revenue & Customs
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

package extractors

import models.HowManyBeneficiaries._
import models.beneficiaries.EmploymentRelatedBeneficiary
import models.{NonUkAddress, UkAddress, UserAnswers}
import pages.QuestionPage
import pages.companyoremploymentrelated.employment._
import play.api.libs.json.JsPath

import java.time.LocalDate
import scala.util.Try

class EmploymentRelatedBeneficiaryExtractor extends BeneficiaryExtractor[EmploymentRelatedBeneficiary] {

  override def apply(answers: UserAnswers,
                     employmentRelatedBeneficiary: EmploymentRelatedBeneficiary,
                     index: Int): Try[UserAnswers] = {

    super.apply(answers, employmentRelatedBeneficiary, index)
      .flatMap(_.set(NamePage, employmentRelatedBeneficiary.name))
      .flatMap(_.set(UtrPage, employmentRelatedBeneficiary.utr))
      .flatMap(answers => extractCountryOfResidence(employmentRelatedBeneficiary.countryOfResidence, answers))
      .flatMap(answers => extractAddress(employmentRelatedBeneficiary.address, answers))
      .flatMap(_.set(DescriptionPage, employmentRelatedBeneficiary.description))
      .flatMap(_.set(NumberOfBeneficiariesPage, employmentRelatedBeneficiary.howManyBeneficiaries))
  }

  override def countryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceYesNoPage
  override def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceUkYesNoPage
  override def countryOfResidencePage: QuestionPage[String] = CountryOfResidencePage

  override def addressYesNoPage: QuestionPage[Boolean] = AddressYesNoPage
  override def ukAddressYesNoPage: QuestionPage[Boolean] = AddressUkYesNoPage
  override def ukAddressPage: QuestionPage[UkAddress] = UkAddressPage
  override def nonUkAddressPage: QuestionPage[NonUkAddress] = NonUkAddressPage

  override def startDatePage: QuestionPage[LocalDate] = StartDatePage

  override def indexPage: QuestionPage[Int] = IndexPage

  override def basePath: JsPath = pages.companyoremploymentrelated.company.basePath
}
