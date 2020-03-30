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

import base.SpecBase
import models.{IdCard, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress}
import pages.individualbeneficiary._

class IndividualBeneficiaryMapperSpec extends SpecBase {

  private val name = Name("First", None, "Last")
  private val dateOfBirth = LocalDate.parse("2010-02-03")
  private val startDate = LocalDate.parse("2019-03-09")
  private val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")
  private val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), "country")

  "IndividualBeneficiaryMapper" must {

    "generate class of individual model with nino and income discretion" in {

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val nino = "AA123456A"

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, true).success.value
        .set(NationalInsuranceNumberPage, nino).success.value
        .set(VPE1FormYesNoPage, false).success.value
        .set(IncomeDiscretionYesNoPage, true).success.value
        .set(StartDatePage, startDate).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe Some(dateOfBirth)
      result.identification mustBe Some(NationalInsuranceNumber(nino))
      result.address mustBe None
      result.vulnerableYesNo mustBe false
      result.income mustBe None
      result.incomeDiscretionYesNo mustBe true
      result.entityStart mustBe startDate
    }
    "generate class of individual model with UK address and no income discretion" in {

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(PassportDetailsYesNoPage, false).success.value
        .set(IdCardDetailsYesNoPage, false).success.value
        .set(AddressYesNoPage, true).success.value
        .set(LiveInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(VPE1FormYesNoPage, false).success.value
        .set(IncomeDiscretionYesNoPage, false).success.value
        .set(IncomePercentagePage, 45).success.value
        .set(StartDatePage, startDate).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe Some(dateOfBirth)
      result.identification mustBe None
      result.address mustBe Some(ukAddress)
      result.vulnerableYesNo mustBe false
      result.incomeDiscretionYesNo mustBe false
      result.income mustBe Some("45")
      result.entityStart mustBe startDate
    }
    "generate class of individual model with non-UK address" in {

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(PassportDetailsYesNoPage, false).success.value
        .set(IdCardDetailsYesNoPage, false).success.value
        .set(AddressYesNoPage, true).success.value
        .set(LiveInTheUkYesNoPage, false).success.value
        .set(NonUkAddressPage, nonUkAddress).success.value
        .set(VPE1FormYesNoPage, false).success.value
        .set(IncomeDiscretionYesNoPage, true).success.value
        .set(StartDatePage, startDate).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe Some(dateOfBirth)
      result.identification mustBe None
      result.address mustBe Some(nonUkAddress)
      result.vulnerableYesNo mustBe false
      result.income mustBe None
      result.incomeDiscretionYesNo mustBe true
      result.entityStart mustBe startDate
    }

    "generate class of individual model with passport" in {

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val passport = Passport("SP", "123456789", LocalDate.of(2024, 8, 16))

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(AddressYesNoPage, true).success.value
        .set(LiveInTheUkYesNoPage, false).success.value
        .set(NonUkAddressPage, nonUkAddress).success.value
        .set(PassportDetailsYesNoPage, true).success.value
        .set(PassportDetailsPage, passport).success.value
        .set(VPE1FormYesNoPage, false).success.value
        .set(IncomeDiscretionYesNoPage, true).success.value
        .set(StartDatePage, startDate).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe Some(dateOfBirth)
      result.identification mustBe Some(passport)
      result.address mustBe Some(nonUkAddress)
      result.vulnerableYesNo mustBe false
      result.income mustBe None
      result.incomeDiscretionYesNo mustBe true
      result.entityStart mustBe startDate
    }

    "generate class of individual model with id card" in {

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val idcard = IdCard("SP", "123456789", LocalDate.of(2024, 8, 16))

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(AddressYesNoPage, true).success.value
        .set(LiveInTheUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(PassportDetailsYesNoPage, false).success.value
        .set(IdCardDetailsYesNoPage, true).success.value
        .set(IdCardDetailsPage, idcard).success.value
        .set(VPE1FormYesNoPage, false).success.value
        .set(IncomeDiscretionYesNoPage, true).success.value
        .set(StartDatePage, startDate).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe Some(dateOfBirth)
      result.identification mustBe Some(idcard)
      result.address mustBe Some(ukAddress)
      result.vulnerableYesNo mustBe false
      result.income mustBe None
      result.incomeDiscretionYesNo mustBe true
      result.entityStart mustBe startDate
    }
  }
  "generate class of individual model with neither nino nor address" in {

    val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

    val userAnswers = emptyUserAnswers
      .set(NamePage, name).success.value
      .set(DateOfBirthPage, dateOfBirth).success.value
      .set(NationalInsuranceNumberYesNoPage, false).success.value
      .set(AddressYesNoPage, false).success.value
      .set(VPE1FormYesNoPage, false).success.value
      .set(IncomeDiscretionYesNoPage, true).success.value
      .set(StartDatePage, startDate).success.value

    val result = mapper(userAnswers).get

    result.name mustBe name
    result.dateOfBirth mustBe Some(dateOfBirth)
    result.identification mustBe None
    result.address mustBe None
    result.vulnerableYesNo mustBe false
    result.income mustBe None
    result.incomeDiscretionYesNo mustBe true
    result.entityStart mustBe startDate
  }

}
