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

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class NationalInsuranceNumberFormProviderSpec extends StringFieldBehaviours {

  val prefix = "individualBeneficiary.nationalInsuranceNumber"

  val requiredKey = s"$prefix.error.required"
  val invalidFormatKey = s"$prefix.error.invalidFormat"
  val notUniqueKey = s"$prefix.error.notUnique"

  val form: Form[String] = new NationalInsuranceNumberFormProvider().apply(prefix, Nil)

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.ninoRegex)
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

    behave like ninoField(
      form = new NationalInsuranceNumberFormProvider(),
      prefix = prefix,
      fieldName = fieldName,
      requiredError = FormError(fieldName, invalidFormatKey, Seq(fieldName)),
      notUniqueError = FormError(fieldName, notUniqueKey)
    )
  }
}
