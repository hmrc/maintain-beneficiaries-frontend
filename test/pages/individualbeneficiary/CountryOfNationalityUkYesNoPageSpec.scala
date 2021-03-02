/*
 * Copyright 2021 HM Revenue & Customs
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

package pages.individualbeneficiary

import pages.behaviours.PageBehaviours

class CountryOfNationalityUkYesNoPageSpec extends PageBehaviours {

  "CountryOfNationalityUkYesNoPage" must {

    beRetrievable[Boolean](CountryOfNationalityYesNoPage)

    beSettable[Boolean](CountryOfNationalityYesNoPage)

    beRemovable[Boolean](CountryOfNationalityYesNoPage)

    "implement cleanup logic when YES selected" in {
      val userAnswers = emptyUserAnswers
        .set(CountryOfNationalityPage, "FR").success.value

      val result = userAnswers.set(CountryOfNationalityUkYesNoPage, true).success.value

      result.get(CountryOfNationalityPage) mustNot be(defined)
    }
  }
}
