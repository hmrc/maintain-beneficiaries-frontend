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

package forms.mappings

import generators.Generators
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.{Form, FormError}

class IncomePercentageMappingsSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with Generators with OptionValues
  with Mappings {
  
  val prefix = "prefix"

  val form = Form(
    "value" -> incomePercentage(prefix)
  )

  "bind valid data" in {

    val result = form.bind(Map("value" -> "42"))

    result.value.value mustEqual 42
  }

  "bind valid untrimmed data" in {

    val result = form.bind(Map("value" -> " 42 "))

    result.value.value mustEqual 42
  }

  "fail to bind an empty percentage" in {

    val result = form.bind(Map.empty[String, String])

    result.errors must contain only FormError("value", s"$prefix.error.required")
  }

  "fail to bind a non-integer" in {

    val result = form.bind(Map("value" -> "42.7"))

    result.errors must contain only FormError("value", s"$prefix.error.integer")
  }

  "fail to bind a non-numeric" in {

    val result = form.bind(Map("value" -> "forty two"))

    result.errors must contain only FormError("value", s"$prefix.error.non_numeric")
  }

  "fail to bind a negative number" in {

    val result = form.bind(Map("value" -> "-5"))

    result.errors must contain only FormError("value", s"$prefix.error.integer")
  }

  "fail to bind a percentage greater than 100" in {

    val result = form.bind(Map("value" -> "314"))

    result.errors must contain only FormError("value", s"$prefix.error.less_than_100")
  }
}
