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

package extractors

import java.time.LocalDate

import generators.ModelGenerators
import models.beneficiaries.{IndividualBeneficiary, RoleInCompany}
import models.{CombinedPassportOrIdCard, Name, NationalInsuranceNumber, TypeOfTrust, UkAddress, UserAnswers}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

class IndividualBeneficiaryExtractorSpec extends FreeSpec with ScalaCheckPropertyChecks with ModelGenerators with MustMatchers {

  import pages.individualbeneficiary._

  val answers: UserAnswers = UserAnswers(
    "Id",
    "UTRUTRUTR",
    LocalDate.of(1987, 12, 31),
    TypeOfTrust.WillTrustOrIntestacyTrust,
    Json.obj()
  )
  val index = 0

  val name = Name("First", None, "Last")
  val date = LocalDate.parse("1996-02-03")
  val address = UkAddress("Line 1", "Line 2", None, None, "postcode")

  val extractor = new IndividualBeneficiaryExtractor()

  "should populate user answers when an individual has a NINO" in {

    val nino = NationalInsuranceNumber("nino")

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = Some(date),
      identification = Some(nino),
      address = None,
      vulnerableYesNo = false,
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = true,
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
      vulnerableYesNo = false,
      roleInCompany = Some(RoleInCompany.Director),
      income = None,
      incomeDiscretionYesNo = true,
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
      address = Some(address),
      vulnerableYesNo = false,
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = true,
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
    result.get(UkAddressPage).get mustBe address
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage).get mustBe true
    result.get(PassportOrIdCardDetailsPage).get mustBe combined

  }

  "should populate user answers when individual has no NINO or passport/ID card" in {

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = Some(date),
      identification = None,
      address = Some(address),
      vulnerableYesNo = false,
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = true,
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
    result.get(UkAddressPage).get mustBe address
    result.get(NonUkAddressPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsYesNoPage) mustNot be(defined)
    result.get(PassportOrIdCardDetailsPage) mustNot be(defined)
  }

  "should populate user answers when individual has no identification or address" in {

    val individual = IndividualBeneficiary(
      name = name,
      dateOfBirth = None,
      identification = None,
      address = None,
      vulnerableYesNo = false,
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = true,
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
