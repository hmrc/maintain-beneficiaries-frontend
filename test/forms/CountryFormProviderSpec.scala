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

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}

class CountryFormProviderSpec extends StringFieldBehaviours {

  val messagePrefix = "charityBeneficiary.countryOfResidence"
  val requiredKey = s"$messagePrefix.error.required"
  val lengthKey = s"$messagePrefix.error.length"
  val maxLength = 100
  val regexp = "^[A-Za-z ,.()'-]*$"
  val invalidKey = s"$messagePrefix.error.invalidCharacters"

  val form: Form[String] = new CountryFormProvider().withPrefix(messagePrefix)

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
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

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = regexp,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidKey, Seq(regexp))
    )
  }
}
