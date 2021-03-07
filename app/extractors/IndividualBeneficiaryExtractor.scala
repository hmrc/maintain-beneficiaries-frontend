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

package extractors

import java.time.LocalDate

import models.beneficiaries.{IndividualBeneficiary, RoleInCompany}
import models.{CombinedPassportOrIdCard, Name, NonUkAddress, UkAddress, UserAnswers}
import pages.QuestionPage
import pages.individualbeneficiary._
import pages.individualbeneficiary.add.StartDatePage
import pages.individualbeneficiary.amend.{IndexPage, PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}
import play.api.libs.json.JsPath

import scala.util.Try

class IndividualBeneficiaryExtractor extends BeneficiaryExtractor[IndividualBeneficiary] {

  override def apply(answers: UserAnswers,
                     individualBeneficiary: IndividualBeneficiary,
                     index: Int): Try[UserAnswers] = {

    super.apply(answers, individualBeneficiary, index)
      .flatMap(answers => extractUserAnswersForIndBeneficiary(answers, individualBeneficiary))
  }

  override def fullNamePage: QuestionPage[Name] = NamePage
  override def dateOfBirthYesNoPage: QuestionPage[Boolean] = DateOfBirthYesNoPage
  override def dateOfBirthPage: QuestionPage[LocalDate] = DateOfBirthPage

  override def roleInCompanyPage: QuestionPage[RoleInCompany] = RoleInCompanyPage

  override def shareOfIncomeYesNoPage: QuestionPage[Boolean] = IncomeDiscretionYesNoPage
  override def shareOfIncomePage: QuestionPage[Int] = IncomePercentagePage

  override def countryOfNationalityYesNoPage: QuestionPage[Boolean] = CountryOfNationalityYesNoPage
  override def ukCountryOfNationalityYesNoPage: QuestionPage[Boolean] = CountryOfNationalityUkYesNoPage
  override def countryOfNationalityPage: QuestionPage[String] = CountryOfNationalityPage

  override def nationalInsuranceNumberYesNoPage: QuestionPage[Boolean] = NationalInsuranceNumberYesNoPage
  override def nationalInsuranceNumberPage: QuestionPage[String] = NationalInsuranceNumberPage

  override def passportOrIdCardDetailsYesNoPage: QuestionPage[Boolean] = PassportOrIdCardDetailsYesNoPage
  override def passportOrIdCardDetailsPage: QuestionPage[CombinedPassportOrIdCard] = PassportOrIdCardDetailsPage

  override def countryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceYesNoPage
  override def ukCountryOfResidenceYesNoPage: QuestionPage[Boolean] = CountryOfResidenceUkYesNoPage
  override def countryOfResidencePage: QuestionPage[String] = CountryOfResidencePage

  override def addressYesNoPage: QuestionPage[Boolean] = AddressYesNoPage
  override def ukAddressYesNoPage: QuestionPage[Boolean] = LiveInTheUkYesNoPage
  override def ukAddressPage: QuestionPage[UkAddress] = UkAddressPage
  override def nonUkAddressPage: QuestionPage[NonUkAddress] = NonUkAddressPage

  override def startDatePage: QuestionPage[LocalDate] = StartDatePage

  override def mentalCapacityYesNoPage: QuestionPage[Boolean] = MentalCapacityYesNoPage
  override def vulnerableYesNoPage: QuestionPage[Boolean] = VPE1FormYesNoPage

  override def indexPage: QuestionPage[Int] = IndexPage

  override def basePath: JsPath = pages.individualbeneficiary.basePath

}

