/*
 * Copyright 2022 HM Revenue & Customs
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

package forms.behaviours

import forms.{NationalInsuranceNumberFormProvider, Validation}
import org.scalacheck.Gen
import play.api.data.{Form, FormError}
import uk.gov.hmrc.domain.Nino
import utils.Constants.MAX
import wolfendale.scalacheck.regexp.RegexpGen

trait StringFieldBehaviours extends FieldBehaviours with OptionalFieldBehaviours {

  def fieldWithMaxLength(form: Form[_],
                         fieldName: String,
                         maxLength: Int,
                         lengthError: FormError): Unit = {

    s"not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength) -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }
  }

  def fieldWithMinLength(form : Form[_],
                         fieldName : String,
                         minLength : Int,
                         lengthError : FormError) : Unit = {

    s"not bind strings shorter than $minLength characters" in {

      val length = if (minLength > 0 && minLength < 2) minLength else minLength -1

      forAll(stringsWithMaxLength(length) -> "shortString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }
  }

  def nonEmptyField(form: Form[_],
                    fieldName: String,
                    requiredError: FormError): Unit = {

    "not bind spaces" in {

      val result = form.bind(Map(fieldName -> "    ")).apply(fieldName)
      result.errors mustBe Seq(requiredError)
    }
  }

  def ninoField(form: NationalInsuranceNumberFormProvider,
                prefix: String,
                fieldName: String,
                requiredError: FormError,
                notUniqueError: FormError): Unit = {

    val nino = "AA000000A"

    s"not bind strings which do not match valid nino format" in {
      val generator = RegexpGen.from(Validation.validNinoFormat)
      forAll(generator) {
        string =>
          whenever(!Nino.isValid(string)) {
            val result = form.apply(prefix, Nil).bind(Map(fieldName -> string)).apply(fieldName)
            result.errors mustEqual Seq(requiredError)
          }
      }
    }

    "not bind NINos that have been used for other individuals" in {
      val intGenerator = Gen.choose(1, MAX)
      forAll(intGenerator) {
        size =>
          val ninos = List.fill(size)(nino)
          val result = form.apply(prefix, ninos).bind(Map(fieldName -> nino)).apply(fieldName)
          result.errors mustEqual Seq(notUniqueError)
      }
    }

    "bind valid NINos when no individuals" in {
      val result = form.apply(prefix, Nil).bind(Map(fieldName -> nino)).apply(fieldName)
      result.errors mustEqual Nil
      result.value.value mustBe nino
    }

    "bind valid NINos when no other individuals have that NINo" in {
      val value: String = "AA111111A"
      val result = form.apply(prefix, List(value)).bind(Map(fieldName -> nino)).apply(fieldName)
      result.errors mustEqual Nil
      result.value.value mustBe nino
    }

    "not bind NINo that has been used for another individual but is case-sensitively different" when {

      "bound value is in lower case" in {
        val otherNino: String = "AA111111A"
        val boundNino: String = "aa111111a"
        val result = form.apply(prefix, List(otherNino)).bind(Map(fieldName -> boundNino)).apply(fieldName)
        result.errors mustEqual Seq(notUniqueError)
      }

      "bound value is in upper case" in {
        val otherNino: String = "aa111111a"
        val boundNino: String = "AA111111A"
        val result = form.apply(prefix, List(otherNino)).bind(Map(fieldName -> boundNino)).apply(fieldName)
        result.errors mustEqual Seq(notUniqueError)
      }

      "bound value is in mixed case" in {
        val otherNino: String = "aA111111a"
        val boundNino: String = "Aa111111A"
        val result = form.apply(prefix, List(otherNino)).bind(Map(fieldName -> boundNino)).apply(fieldName)
        result.errors mustEqual Seq(notUniqueError)
      }
    }
  }

  def fieldWithRegexpWithGenerator(form: Form[_],
                                   fieldName: String,
                                   regexp: String,
                                   generator: Gen[String],
                                   error: FormError): Unit = {

    s"not bind strings which do not match $regexp" in {
      forAll(generator) {
        string =>
          whenever(!string.matches(regexp) && string.nonEmpty) {
            val result = form.bind(Map(fieldName -> string)).apply(fieldName)
            result.errors mustEqual Seq(error)
          }
      }
    }
  }
}
