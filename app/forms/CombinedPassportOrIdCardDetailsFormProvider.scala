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

package forms

import java.time.LocalDate

import forms.mappings.{Constraints, Mappings}
import javax.inject.Inject
import models.{CombinedPassportOrIdCard, Passport}
import play.api.data.Form
import play.api.data.Forms.mapping

class CombinedPassportOrIdCardDetailsFormProvider @Inject() extends Mappings with Constraints {
  val maxLengthCountryField = 100
  val maxLengthNumberField = 30

  def withPrefix(prefix: String): Form[CombinedPassportOrIdCard] = Form(
    mapping(
      "country" -> text(s"$prefix.passportOrIdCardDetails.country.error.required")
        .verifying(
          firstError(
            maxLength(maxLengthCountryField, s"$prefix.passportOrIdCardDetails.country.error.length"),
            nonEmptyString("country", s"$prefix.passportOrIdCardDetails.country.error.required")
          )
        ),
      "number" -> text(s"$prefix.passportOrIdCardDetails.number.error.required")
        .verifying(
          firstError(
            maxLength(maxLengthNumberField, s"$prefix.passportOrIdCardDetails.number.error.length"),
            regexp(Validation.passportOrIdCardNumberRegEx, s"$prefix.passportOrIdCardDetails.number.error.invalid"),
            nonEmptyString("number", s"$prefix.passportOrIdCardDetails.number.error.required")
          )
        ),
      "expiryDate" -> localDate(
        invalidKey     = s"$prefix.passportOrIdCardDetails.expiryDate.error.invalid",
        allRequiredKey = s"$prefix.passportOrIdCardDetails.expiryDate.error.required.all",
        twoRequiredKey = s"$prefix.passportOrIdCardDetails.expiryDate.error.required.two",
        requiredKey    = s"$prefix.passportOrIdCardDetails.expiryDate.error.required"
      ).verifying(firstError(
        maxDate(
          LocalDate.of(2099, 12, 31),
          s"$prefix.passportOrIdCardDetails.expiryDate.error.future", "day", "month", "year"
        ),
        minDate(
          LocalDate.of(1500,1,1),
          s"$prefix.passportOrIdCardDetails.expiryDate.error.past", "day", "month", "year"
        )
      ))
    )(CombinedPassportOrIdCard.apply)(CombinedPassportOrIdCard.unapply)
  )
}

