/*
 * Copyright 2025 HM Revenue & Customs
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

package generators

import models.BeneficiaryType._
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import java.time.{Instant, LocalDate, ZoneOffset}

trait ModelGenerators {

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

  implicit lazy val arbitraryYesNoDontKnow: Arbitrary[YesNoDontKnow] = {
    Arbitrary {
      Gen.oneOf(YesNoDontKnow.values)
    }
  }

  implicit lazy val arbitraryIdCard: Arbitrary[IdCard] =
    Arbitrary {
      for {
        number <- arbitrary[String]
        expiry <- datesBetween(LocalDate.now, LocalDate.now.plusYears(10))
        country <- arbitrary[String]
      } yield IdCard(country, number, expiry)
    }

  implicit lazy val arbitraryPassport: Arbitrary[Passport] =
    Arbitrary {
      for {
        number <- arbitrary[String]
        expiry <- datesBetween(LocalDate.now, LocalDate.now.plusYears(10))
        country <- arbitrary[String]
      } yield Passport(country, number, expiry)
    }

  implicit lazy val arbitraryCombinedPassportOrIdCard: Arbitrary[CombinedPassportOrIdCard] =
    Arbitrary {
      for {
        number <- arbitrary[String]
        expiry <- datesBetween(LocalDate.now, LocalDate.now.plusYears(10))
        country <- arbitrary[String]
      } yield CombinedPassportOrIdCard(country, number, expiry)
    }

  implicit lazy val arbitraryNationalInsuranceNumber: Arbitrary[NationalInsuranceNumber] =
    Arbitrary {
      for {
        number <- arbitrary[String]
      } yield NationalInsuranceNumber(number)
    }

  implicit lazy val arbitraryUkAddress: Arbitrary[UkAddress] =
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- arbitrary[String]
        line3 <- arbitrary[Option[String]]
        line4 <- arbitrary[Option[String]]
        postcode <- arbitrary[String]
      } yield UkAddress(line1, line2, line3, line4, postcode)
    }

  implicit lazy val arbitraryNonUkAddress: Arbitrary[NonUkAddress] =
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- arbitrary[String]
        line3 <- arbitrary[Option[String]]
        country <- arbitrary[String]
      } yield NonUkAddress(line1, line2, line3, country)
    }

  implicit lazy val arbitraryAddress: Arbitrary[Address] = {
    Arbitrary(Gen.oneOf(
      arbitraryUkAddress.arbitrary,
      arbitraryNonUkAddress.arbitrary
    ))
  }

  implicit lazy val arbitraryName: Arbitrary[Name] =
    Arbitrary {
      for {
        firstName <- arbitrary[String]
        middleName <- arbitrary[Option[String]]
        lastName <- arbitrary[String]
      } yield Name(firstName, middleName, lastName)
    }

  implicit lazy val arbitraryIndividualIdentification : Arbitrary[IndividualIdentification] = {
    Arbitrary {
      Gen.oneOf(
        arbitraryPassport.arbitrary, arbitraryIdCard.arbitrary, arbitraryNationalInsuranceNumber.arbitrary
      ).map {
        case p:Passport => p.copy(countryOfIssue = "GB")
        case x => x
      }
    }
  }

  implicit lazy val arbitraryLocalDate : Arbitrary[LocalDate] =
    Arbitrary {
      Gen.const(LocalDate.of(2010, 10, 10))
    }

  implicit lazy val arbitraryBeneficiaryType: Gen[BeneficiaryType] =
    Gen.oneOf[BeneficiaryType](
      IndividualBeneficiary,
      ClassOfBeneficiary,
      CompanyBeneficiary,
      EmploymentRelatedBeneficiary,
      TrustBeneficiary,
      CharityBeneficiary,
      OtherBeneficiary
    )

  implicit lazy val arbitraryHowManyBeneficiaries: Arbitrary[HowManyBeneficiaries] =
    Arbitrary {
      Gen.oneOf(HowManyBeneficiaries.values)
    }

  implicit lazy val arbitraryDescription: Arbitrary[Description] =
    Arbitrary {
      for {
        description <- arbitrary[String]
        description1 <- arbitrary[Option[String]]
        description2 <- arbitrary[Option[String]]
        description3 <- arbitrary[Option[String]]
        description4 <- arbitrary[Option[String]]
      } yield Description(description, description1, description2, description3, description4)
    }

}
