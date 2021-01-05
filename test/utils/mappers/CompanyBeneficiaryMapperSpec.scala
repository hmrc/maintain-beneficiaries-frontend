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
import models.UkAddress
import pages.companyoremploymentrelated.company._

class CompanyBeneficiaryMapperSpec extends SpecBase {

  val name: String = "Company"
  val share: Int = 50
  val date: LocalDate = LocalDate.parse("2019-02-03")
  val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", None, None, "postcode")

  "CompanyBeneficiaryMapper" must {

    "generate company beneficiary model" in {

      val mapper = injector.instanceOf[CompanyBeneficiaryMapper]

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(DiscretionYesNoPage, false).success.value
        .set(ShareOfIncomePage, share).success.value
        .set(AddressYesNoPage, true).success.value
        .set(AddressUkYesNoPage, true).success.value
        .set(UkAddressPage, ukAddress).success.value
        .set(StartDatePage, date).success.value

      val result = mapper(userAnswers).get

      result.name mustBe name
      result.incomeDiscretionYesNo mustBe false
      result.income.get mustBe "50"
      result.address.get mustBe ukAddress
      result.entityStart mustBe date
      result.provisional mustBe true
      result.utr mustNot be(defined)

    }
  }
}
