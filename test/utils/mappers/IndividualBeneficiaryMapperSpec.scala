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
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import models.{Name, NationalInsuranceNumber, NonUkAddress, UkAddress}
import pages.individualbeneficiary.{AddressYesNoPage, DateOfBirthPage, IncomeDiscretionYesNoPage, IncomePercentagePage, LiveInTheUkYesNoPage, NamePage, NationalInsuranceNumberPage, NationalInsuranceNumberYesNoPage, NonUkAddressPage, StartDatePage, UkAddressPage, VPE1FormYesNoPage}

class IndividualBeneficiaryMapperSpec extends SpecBase {

  private val name = Name("First", None, "Last")
  private val dateOfBirth = LocalDate.parse("2010-02-03")
  private val nino = "AA123456A"
  private val startDate = LocalDate.parse("2019-03-09")

  "IndividualBeneficiaryMapper" must {

    "generate class of individual model with nino and income discretion" in {

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, true).success.value
        .set(NationalInsuranceNumberPage, nino).success.value
        .set(AddressYesNoPage, false).success.value
        .set(VPE1FormYesNoPage, false).success.value
        .set(IncomeDiscretionYesNoPage, true).success.value
        .set(StartDatePage, startDate).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe Some(dateOfBirth)
      result.nationalInsuranceNumber mustBe Some(nino)
      result.address mustBe None
      result.vulnerableYesNo mustBe false
      result.income mustBe None
      result.incomeDiscretionYesNo mustBe true
      result.entityStart mustBe startDate
    }
    "generate class of individual model with UK address and no income discretion" in {
      val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, false).success.value
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
      result.nationalInsuranceNumber mustBe None
      result.address mustBe Some(ukAddress)
      result.vulnerableYesNo mustBe false
      result.incomeDiscretionYesNo mustBe false
      result.income mustBe Some("45")
      result.entityStart mustBe startDate
    }
    "generate class of individual model with non-UK address" in {

      val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), "country")

      val mapper = injector.instanceOf[IndividualBeneficiaryMapper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DateOfBirthPage, dateOfBirth).success.value
        .set(NationalInsuranceNumberYesNoPage, false).success.value
        .set(AddressYesNoPage, true).success.value
        .set(LiveInTheUkYesNoPage, false).success.value
        .set(NonUkAddressPage, nonUkAddress).success.value
        .set(VPE1FormYesNoPage, false).success.value
        .set(IncomeDiscretionYesNoPage, true).success.value
        .set(StartDatePage, startDate).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.dateOfBirth mustBe Some(dateOfBirth)
      result.nationalInsuranceNumber mustBe None
      result.address mustBe Some(nonUkAddress)
      result.vulnerableYesNo mustBe false
      result.income mustBe None
      result.incomeDiscretionYesNo mustBe true
      result.entityStart mustBe startDate
    }

  }
}
