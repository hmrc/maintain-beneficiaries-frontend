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

import java.time.LocalDate

import base.SpecBase
import models.{Description, HowManyBeneficiaries, UkAddress}
import pages.companyoremploymentrelated.employment._

class EmploymentRelatedBeneficiaryMapperSpec extends SpecBase {

  val name: String = "Large"
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")
  val description: Description = Description("Description", None, None, None, None)
  val numberOfBeneficiaries: HowManyBeneficiaries = HowManyBeneficiaries.Over201
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "EmploymentRelatedBeneficiaryMapper" must {

    val mapper = injector.instanceOf[EmploymentRelatedBeneficiaryMapper]

    "return None for empty user answers" in {

      val result = mapper(emptyUserAnswers)
      result mustBe None
    }

    "generate employment related beneficiary model" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(AddressYesNoPage, true).success.value
        .set(AddressUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(DescriptionPage, description).success.value
        .set(NumberOfBeneficiariesPage, numberOfBeneficiaries).success.value
        .set(StartDatePage, date).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.address.get mustBe ukAddress
      result.description mustBe description
      result.howManyBeneficiaries mustBe "201"
      result.entityStart mustBe date
      result.provisional mustBe true
      result.utr mustNot be(defined)

    }
  }
}
