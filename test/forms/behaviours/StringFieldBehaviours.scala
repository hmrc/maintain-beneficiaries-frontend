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

package forms.behaviours

import forms.Validation
import org.scalacheck.Gen
import play.api.data.{Form, FormError}
import uk.gov.hmrc.domain.Nino
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
          result.errors shouldEqual Seq(lengthError)
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
          result.errors shouldEqual Seq(lengthError)
      }
    }

  }

  def nonEmptyField(form: Form[_],
                    fieldName: String,
                    requiredError: FormError): Unit = {

    "not bind spaces" in {

      val result = form.bind(Map(fieldName -> "    ")).apply(fieldName)
      result.errors shouldBe Seq(requiredError)
    }
  }

  def ninoField(form: Form[_],
                fieldName: String,
                requiredError: FormError): Unit = {

    s"not bind strings which do not match valid nino format " in {
      val generator = RegexpGen.from(Validation.validNinoFormat)
      forAll(generator) {
        string =>
          whenever(!Nino.isValid(string)) {
            val result = form.bind(Map(fieldName -> string)).apply(fieldName)
            result.errors shouldEqual Seq(requiredError)
          }
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
            result.errors shouldEqual Seq(error)
          }
      }
    }
  }
}
