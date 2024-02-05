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
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class UkAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new UkAddressFormProvider()()

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "ukAddress.error.line1.required"
    val lengthKey = "ukAddress.error.line1.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
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

  ".line2" must {

    val fieldName = "line2"
    val requiredKey = "ukAddress.error.line2.required"
    val lengthKey = "ukAddress.error.line2.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
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

  ".line3" must {

    val fieldName = "line3"
    val lengthKey = "ukAddress.error.line3.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  line3  ", "line4" -> "line4", "postcode" -> "AB12CD"))
      result.value.value.line3 mustBe Some("line3")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  ", "line4" -> "line4", "postcode" -> "AB12CD"))
      result.value.value.line3 mustBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "", "line4" -> "line4", "postcode" -> "AB12CD"))
      result.value.value.line3 mustBe None
    }

    "filter out smart apostrophes and replace with straight ones" in {
      val result = form bind Map("line1" -> "value1", "line2" -> "value2", "line3" -> "Besses o’ th‘Barn",
        "line4" -> "Manchester", "postcode" -> "M5 6AG")
      result.value.value.line3.get mustBe "Besses o' th'Barn"
    }

  }

  ".line4" must {

    val fieldName = "line4"
    val lengthKey = "ukAddress.error.line4.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "line3", "line4" -> "  line4  ", "postcode" -> "AB12CD"))
      result.value.value.line4 mustBe Some("line4")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "line3", "line4" -> "  ", "postcode" -> "AB12CD"))
      result.value.value.line4 mustBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "line3", "line4" -> "", "postcode" -> "AB12CD"))
      result.value.value.line4 mustBe None
    }

    "filter out smart apostrophes and replace with straight ones" in {
      val result = form bind Map("line1" -> "value1", "line2" -> "value2", "line3" -> "value3",
        "line4" -> "Besses o’ th‘Barn", "postcode" -> "M5 6AG")
      result.value.value.line4 mustBe Some("Besses o' th'Barn")
    }

  }

  ".postcode" must {

    val fieldName = "postcode"
    val requiredKey = "ukAddress.error.postcode.required"
    val invalidKey = "error.postcodeInvalid"

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = Validation.postcodeRegex,
      generator = arbitrary[String],
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, invalidKey)
    )

  }

}
