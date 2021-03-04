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

import base.SpecBase
import models.beneficiaries.{IndividualBeneficiary, RoleInCompany}
import models.{CombinedPassportOrIdCard, Name, NationalInsuranceNumber, NonUkAddress, UkAddress, UserAnswers}
import pages.individualbeneficiary._
import pages.individualbeneficiary.amend.{IndexPage, PassportOrIdCardDetailsPage, PassportOrIdCardDetailsYesNoPage}

import java.time.LocalDate

class IndividualBeneficiaryExtractorSpec extends SpecBase {

  val answers: UserAnswers = emptyUserAnswers

  val index = 0

  val name: Name = Name("First", None, "Last")
  val date: LocalDate = LocalDate.parse("1996-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val nonUkAddress: NonUkAddress = NonUkAddress("Line 1", "Line 2", None, "FR")

  val extractor = new IndividualBeneficiaryExtractor()

  "should populate user answers when an individual has a NINO" in {

    val nino = NationalInsuranceNumber("nino")

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = Some(date),
      identification = Some(nino),
      address = None,
      vulnerableYesNo = Some(false),
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = Some(true),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, individual, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe true
    result.get(DateOfBirthPage).get mustBe date
    result.get(NationalInsuranceNumberYesNoPage).get mustBe true
    result.get(NationalInsuranceNumberPage).get mustBe "nino"
    result.get(AddressYesNoPage) mustNot be(defined)
    result.get(LiveInTheUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
  }

  "should populate user answers when individual has a role in the company" in {
    val nino = NationalInsuranceNumber("nino")

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = Some(date),
      identification = Some(nino),
      address = None,
      vulnerableYesNo = Some(false),
      roleInCompany = Some(RoleInCompany.Director),
      income = None,
      incomeDiscretionYesNo = Some(true),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, individual, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe true
    result.get(DateOfBirthPage).get mustBe date
    result.get(RoleInCompanyPage).get mustBe RoleInCompany.Director
    result.get(NationalInsuranceNumberYesNoPage).get mustBe true
    result.get(NationalInsuranceNumberPage).get mustBe "nino"
    result.get(AddressYesNoPage) mustNot be(defined)
    result.get(LiveInTheUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
  }

  "should populate user answers when individual has a passport/ID card" in {

    val combined = CombinedPassportOrIdCard("country", "number", date)

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = Some(date),
      identification = Some(combined),
      address = Some(ukAddress),
      vulnerableYesNo = Some(false),
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = Some(true),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, individual, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe true
    result.get(DateOfBirthPage).get mustBe date
    result.get(NationalInsuranceNumberYesNoPage).get mustBe false
    result.get(NationalInsuranceNumberPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe true
    result.get(LiveInTheUkYesNoPage).get mustBe true
    result.get(UkAddressPage).get mustBe ukAddress
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage).get mustBe true
    result.get(PassportOrIdCardDetailsPage).get mustBe combined

  }

  "should populate user answers when individual has a non-UK address and no NINO or passport/ID card" in {

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = Some(date),
      identification = None,
      address = Some(nonUkAddress),
      vulnerableYesNo = Some(false),
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = Some(true),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, individual, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe true
    result.get(DateOfBirthPage).get mustBe date
    result.get(NationalInsuranceNumberYesNoPage).get mustBe false
    result.get(NationalInsuranceNumberPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe true
    result.get(LiveInTheUkYesNoPage).get mustBe false
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage).get mustBe nonUkAddress
    result.get(PassportOrIdCardDetailsYesNoPage).get mustBe false
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
  }

  "should populate user answers when individual has no identification or address" in {

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = None,
      identification = None,
      address = None,
      vulnerableYesNo = Some(false),
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = Some(true),
      entityStart = date,
      provisional = true
    )

    val result = extractor(answers, individual, index).get

    result.get(IndexPage).get mustBe index
    result.get(NamePage).get mustBe name
    result.get(DateOfBirthYesNoPage).get mustBe false
    result.get(DateOfBirthPage) mustNot be(defined)
    result.get(NationalInsuranceNumberYesNoPage).get mustBe false
    result.get(NationalInsuranceNumberPage) mustNot be(defined)
    result.get(AddressYesNoPage).get mustBe false
    result.get(LiveInTheUkYesNoPage) mustNot be(defined)
    result.get(UkAddressPage) mustNot be(defined)
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
  }

}
