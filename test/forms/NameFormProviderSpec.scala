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

import forms.behaviours.{OptionalFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class NameFormProviderSpec extends StringFieldBehaviours with OptionalFieldBehaviours {

  val messageKeyPrefix = "individualBeneficiary.name"
  val form = new NameFormProvider().withPrefix(messageKeyPrefix)

  val maxLength = 35
  val minLength = 1

  ".firstName" must {

    val fieldName = "firstName"
    val requiredKey = s"$messageKeyPrefix.error.firstName.required"
    val lengthKey = s"$messageKeyPrefix.error.firstName.length"
    val regex = "^[A-Za-z0-9 ,.()/&'-]*$"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(regex)
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
  }

  ".middleName" must {

    val fieldName = "middleName"
    val lengthKey = s"$messageKeyPrefix.error.middleName.length"
    val maxLength = 35
    val regex = "^[A-Za-z0-9 ,.()/&'-]*$"


    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(regex))
  }

  ".lastName" must {

    val fieldName = "lastName"
    val requiredKey = s"$messageKeyPrefix.error.lastName.required"
    val lengthKey = s"$messageKeyPrefix.error.lastName.length"
    val regex = "^[A-Za-z0-9 ,.()/&'-]*$"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(regex)
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
  }
}
