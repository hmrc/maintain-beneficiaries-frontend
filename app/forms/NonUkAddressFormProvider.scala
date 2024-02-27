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

package forms

import forms.helpers.WhitespaceHelper._
import forms.mappings.Mappings
import models.NonUkAddress
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class NonUkAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[NonUkAddress] = Form(
    mapping(
      "line1" ->
        text("nonUkAddress.error.line1.required")
          .verifying(
            firstError(
              nonEmptyString("line1", "nonUkAddress.error.line1.required"),
              maxLength(35, "nonUkAddress.error.line1.length"),
              regexp(Validation.addressLineRegex, "nonUkAddress.error.line1.invalidCharacters")
            )),
      "line2" ->
        text("nonUkAddress.error.line2.required")
          .verifying(
            firstError(
              nonEmptyString("line2", "nonUkAddress.error.line2.required"),
              maxLength(35, "nonUkAddress.error.line2.length"),
              regexp(Validation.addressLineRegex, "nonUkAddress.error.line2.invalidCharacters")
            )),
      "line3" ->
        optional(text()
          .verifying(
            firstError(
              maxLength(35, "nonUkAddress.error.line3.length"),
              regexp(Validation.addressLineRegex, "nonUkAddress.error.line3.invalidCharacters")
            ))).transform(emptyToNone, identity[Option[String]]),
      "country" ->
        text("nonUkAddress.error.country.required")
          .verifying(
            firstError(
              maxLength(35, "nonUkAddress.error.country.length"),
              nonEmptyString("country", "nonUkAddress.error.country.required")
            ))
    )(NonUkAddress.apply)(NonUkAddress.unapply)
  )
}
