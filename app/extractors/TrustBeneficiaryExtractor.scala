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

package extractors

import models.beneficiaries.TrustBeneficiary
import models.{NonUkAddress, UkAddress, UserAnswers}
import pages.QuestionPage
import pages.charityortrust.trust._
import play.api.libs.json.JsPath

import java.time.LocalDate
import scala.util.Try

class TrustBeneficiaryExtractor extends BeneficiaryExtractor[TrustBeneficiary] {

  override def apply(answers: UserAnswers,
                     trustBeneficiary: TrustBeneficiary,
                     index: Int): Try[UserAnswers] = {

    super.apply(answers, trustBeneficiary, index)
      .flatMap(answers => extractUserAnswersForOrgBeneficiary(answers, trustBeneficiary))
  }

  override def namePage: QuestionPage[String] = NamePage

  override def utrPage: QuestionPage[String] = UtrPage

  override def shareOfIncomeYesNoPage: QuestionPage[Boolean] = DiscretionYesNoPage
  override def shareOfIncomePage: QuestionPage[Int] = ShareOfIncomePage

  override def countryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceYesNoPage
  override def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceUkYesNoPage
  override def countryOfResidencePage: QuestionPage[String] = CountryOfResidencePage

  override def addressYesNoPage: QuestionPage[Boolean] = AddressYesNoPage
  override def ukAddressYesNoPage: QuestionPage[Boolean] = AddressUkYesNoPage
  override def ukAddressPage: QuestionPage[UkAddress] = UkAddressPage
  override def nonUkAddressPage: QuestionPage[NonUkAddress] = NonUkAddressPage

  override def startDatePage: QuestionPage[LocalDate] = StartDatePage

  override def indexPage: QuestionPage[Int] = IndexPage

  override def basePath: JsPath = pages.charityortrust.trust.basePath
}
