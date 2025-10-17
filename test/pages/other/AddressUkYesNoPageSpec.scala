/*
 * Copyright 2025 HM Revenue & Customs
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

package pages.other

import models.{NonUkAddress, UkAddress}
import pages.behaviours.PageBehaviours

class AddressUkYesNoPageSpec extends PageBehaviours {

  "AddressUkYesNoPage" must {

    beRetrievable[Boolean](AddressUkYesNoPage)

    beSettable[Boolean](AddressUkYesNoPage)

    beRemovable[Boolean](AddressUkYesNoPage)

    "implement cleanup logic" when {

      "YES selected" in {
        val userAnswers = emptyUserAnswers
          .set(NonUkAddressPage, NonUkAddress("Line 1", "Line 2", None, "FR")).success.value

        val result = userAnswers.set(AddressUkYesNoPage, true).success.value

        result.get(NonUkAddressPage) mustNot be(defined)
      }

      "NO selected" in {

        val userAnswers = emptyUserAnswers
          .set(UkAddressPage, UkAddress("Line 1", "Line 2", None, None, "AB1 1AB")).success.value

        val result = userAnswers.set(AddressUkYesNoPage, false).success.value

        result.get(UkAddressPage) mustNot be(defined)
      }
    }
  }
}
