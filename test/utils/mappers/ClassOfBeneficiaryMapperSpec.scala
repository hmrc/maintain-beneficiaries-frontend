/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}

import java.time.LocalDate

class ClassOfBeneficiaryMapperSpec extends SpecBase {

  val description: String = "Description"
  val date: LocalDate = LocalDate.parse("2019-02-03")

  "ClassOfBeneficiaryMapper" must {

    val mapper = injector.instanceOf[ClassOfBeneficiaryMapper]

    "return None for empty user answers" in {

      val result = mapper(emptyUserAnswers)
      result mustBe None
    }

    "generate class of beneficiary model" in {

      val userAnswers = emptyUserAnswers
        .set(DescriptionPage, description).success.value
        .set(EntityStartPage, date).success.value

      val result = mapper(userAnswers).get

      result.description mustBe description
      result.entityStart mustBe date

    }
  }
}
