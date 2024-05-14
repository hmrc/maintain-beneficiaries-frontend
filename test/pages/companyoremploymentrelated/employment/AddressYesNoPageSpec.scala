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

package pages.companyoremploymentrelated.employment

import models.{NonUkAddress, UkAddress}
import pages.behaviours.PageBehaviours

class AddressYesNoPageSpec extends PageBehaviours {

  "AddressYesNoPage" must {

    beRetrievable[Boolean](AddressYesNoPage)

    beSettable[Boolean](AddressYesNoPage)

    beRemovable[Boolean](AddressYesNoPage)

    "implement cleanup logic" when {
      "NO selected" in {

        val userAnswers = emptyUserAnswers
          .set(AddressUkYesNoPage, true).success.value
          .set(UkAddressPage, UkAddress("Line 1", "Line 2", None, None, "AB1 1AB")).success.value
          .set(NonUkAddressPage, NonUkAddress("Line 1", "Line 2", None, "FR")).success.value

        val result = userAnswers.set(AddressYesNoPage, false).success.value

        result.get(AddressUkYesNoPage) mustNot be(defined)
        result.get(UkAddressPage) mustNot be(defined)
        result.get(NonUkAddressPage) mustNot be(defined)
      }
    }
  }
}
