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
import models.{Name, Passport}
import models.beneficiaries.{Beneficiaries, IndividualBeneficiary}
import play.api.data.FormError

import java.time.LocalDate

class PassportDetailsFormProviderSpec extends StringFieldBehaviours {

  private val prefix = "individualBeneficiary"
  private val underTest = new PassportDetailsFormProvider(frontendAppConfig)
  private val form = underTest.withPrefix(prefix, Beneficiaries())

  private val countryRequiredKey = s"$prefix.passportDetails.country.error.required"
  private val countryLengthKey = s"$prefix.passportDetails.country.error.length"
  private val maxLengthCountryField = 100

  private val numberRequiredKey = s"$prefix.passportDetails.number.error.required"
  private val numberInvalidKey = s"$prefix.passportDetails.number.error.invalid"
  private val numberLengthKey = s"$prefix.passportDetails.number.error.length"
  private val uniqueNumberKey = s"$prefix.passportDetails.number.error.unique"
  private val maxLengthNumberField = 30


  ".country" must {

    val fieldName = "country"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthCountryField)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthCountryField,
      lengthError = FormError(fieldName, countryLengthKey, Seq(maxLengthCountryField))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, countryRequiredKey)
    )
  }

  "number" must {

    val fieldName = "number"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthNumberField)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthNumberField,
      lengthError = FormError(fieldName, numberLengthKey, Seq(maxLengthNumberField))
    )

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = Validation.passportOrIdCardNumberRegEx,
      generator = stringsWithMaxLength(maxLengthNumberField),
      error = FormError(fieldName, numberInvalidKey, Seq(Validation.passportOrIdCardNumberRegEx))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, numberRequiredKey)
    )

    val passportNumber = "123"
    val individualBeneficiary = IndividualBeneficiary(
      name = Name(firstName = "First", middleName = None, lastName = "Last"),
      dateOfBirth = None,
      identification = Some(Passport("country", passportNumber, LocalDate.now())),
      address = None,
      entityStart = LocalDate.parse("2020-02-02"),
      vulnerableYesNo = Some(false),
      roleInCompany = None,
      income = None,
      incomeDiscretionYesNo = Some(false),
      provisional = false
    )

    "bind form when the passport number is unique" in {
      val result = form.bind(Map(fieldName -> passportNumber)).apply(fieldName)
      result.errors mustEqual Seq.empty
      result.value mustBe Some(passportNumber)
    }

    "not bind form and return a form error when the passport number isn't unique" in {
      val form = underTest.withPrefix(prefix, Beneficiaries(List(individualBeneficiary)))
      val formError = FormError(fieldName, uniqueNumberKey)

      val result = form.bind(Map(fieldName -> passportNumber)).apply(fieldName)
      result.errors mustEqual Seq(formError)
      result.value mustBe Some(passportNumber)
    }
  }
}
