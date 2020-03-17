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

import forms.FormSpec
import generators.Generators
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.Form

trait OptionalFieldBehaviours extends FormSpec with ScalaCheckPropertyChecks with Generators {


  def optionalField(form: Form[_],
                    fieldName: String,
                    validDataGenerator: Gen[String]): Unit = {

    "bind valid data" in {
      forAll(validDataGenerator) {
        dataItem =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value shouldBe dataItem
      }
    }


    "bind when key is not present at all" in {

      val result = form.bind(emptyForm).apply(fieldName)
      result.errors shouldBe empty
    }

    "bind blank values" in {

      val result = form.bind(Map(fieldName -> "")).apply(fieldName)
      result.errors shouldBe empty
    }

  }
}
