/*
 * Copyright 2024 HM Revenue & Customs
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

package forms

import forms.behaviours.DateBehaviours
import play.api.data.FormError

import java.time.{LocalDate, ZoneOffset}

class DateRemovedFromTrustFormProviderSpec extends DateBehaviours {

  private val max = LocalDate.now(ZoneOffset.UTC)
  private val entityStartDate = LocalDate.of(2020, 1, 1)

  val form = new DateRemovedFromTrustFormProvider().withPrefixAndEntityStartDate("classOfBeneficiary.whenRemoved", entityStartDate)

  ".value" should {

    val validData = datesBetween(
      min = entityStartDate,
      max = max
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "classOfBeneficiary.whenRemoved.error.required.all")

    behave like dateFieldWithMax(form, "value",
      max = max,
      FormError("value", "classOfBeneficiary.whenRemoved.error.future", List("day", "month", "year"))
    )

    behave like dateFieldWithMin(form, "value",
      min = entityStartDate,
      FormError("value", "classOfBeneficiary.whenRemoved.error.past", List("day", "month", "year"))
    )

  }
}
