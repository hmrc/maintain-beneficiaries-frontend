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

package pages.charityortrust.charity

import pages.behaviours.PageBehaviours

class CountryOfResidenceYesNoPageSpec extends PageBehaviours {

  "CountryOfResidenceYesNoPage" must {

    beRetrievable[Boolean](CountryOfResidenceYesNoPage)

    beSettable[Boolean](CountryOfResidenceYesNoPage)

    beRemovable[Boolean](CountryOfResidenceYesNoPage)

    "implement cleanup logic when NO selected" in {
      val userAnswers = emptyUserAnswers
        .set(CountryOfResidenceUkYesNoPage, false).success.value
        .set(CountryOfResidencePage, "FR").success.value

      val result = userAnswers.set(CountryOfResidenceYesNoPage, false).success.value

      result.get(CountryOfResidenceUkYesNoPage) mustNot be(defined)
      result.get(CountryOfResidencePage) mustNot be(defined)
    }
  }
}
