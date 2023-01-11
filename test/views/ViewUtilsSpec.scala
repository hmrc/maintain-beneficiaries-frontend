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

package views

import play.api.data.FormError

class ViewUtilsSpec extends ViewSpecBase {

  "errorHref" must {

    "refer to date field when error is for a single date input (lowercase message contains 'date' and not 'yesno')" in {
      val error = FormError(key = "value", message = "dateOfBirth.error.required")
      val result = ViewUtils.errorHref(error)
      result mustBe "value.day"
    }

    "refer to date field when error is for a single date input (lowercase message contains 'when' and not 'yesno')" in {
      val error = FormError(key = "value", message = "otherIndividual.whenAdded.error.required")
      val result = ViewUtils.errorHref(error)
      result mustBe "value.day"
    }

    "refer to date field when error contains a date argument" in {
      val error = FormError(key = "expiryDate", message = "otherIndividual.passportDetails.expiryDate.error.required", args = Seq("month"))
      val result = ViewUtils.errorHref(error)
      result mustBe "expiryDate.month"
    }

    "not refer to date field when error is for a single input (lowercase message does not contain 'date')" in {
      val error = FormError(key = "value", message = "name.error.firstname.required")
      val result = ViewUtils.errorHref(error)
      result mustBe "value"
    }

    "not refer to date field when error is for a single input (lowercase message contains 'date' and 'yesno')" in {
      val error = FormError(key = "value", message = "otherIndividual.dateOfBirthYesNo.error.required")
      val result = ViewUtils.errorHref(error)
      result mustBe "value-yes"
    }

    "not refer to date field when error is for a single input (lowercase message contains 'when' and 'yesno')" in {
      val error = FormError(key = "value", message = "otherIndividual.whenAddedYesNo.error.required")
      val result = ViewUtils.errorHref(error)
      result mustBe "value-yes"
    }

    "refer to yes field when error is for a yes/no input (lowercase message contains 'yesno')" in {
      val error = FormError(key = "value", message = "otherIndividual.nationalInsuranceYesNo.error.required")
      val result = ViewUtils.errorHref(error)
      result mustBe "value-yes"
    }

    "not refer to date field when error has arguments" in {
      val error = FormError(key = "value", message = "name.error.firstname.required", args = Seq("^[A-Za-z0-9 ,.()/&'-]*$"))
      val result = ViewUtils.errorHref(error)
      result mustBe "value"
    }

  }

}
