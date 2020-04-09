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

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class StringFormProviderSpec extends StringFieldBehaviours {

  val prefix = "classOfBeneficiary.description"

  val requiredKey = s"$prefix.error.required"
  val lengthKey = s"$prefix.error.length"
  val invalidFormatKey = s"$prefix.error.invalidFormat"
  val maxLength = 56

  val form = new StringFormProvider().withPrefix(prefix, maxLength)

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.nameRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = Validation.nameRegex,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidFormatKey, Seq(Validation.nameRegex))
    )
  }
}
