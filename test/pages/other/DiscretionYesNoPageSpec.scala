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

package pages.other

import pages.behaviours.PageBehaviours

class DiscretionYesNoPageSpec extends PageBehaviours {

  "DiscretionYesNoPage" must {

    beRetrievable[Boolean](DiscretionYesNoPage)

    beSettable[Boolean](DiscretionYesNoPage)

    beRemovable[Boolean](DiscretionYesNoPage)

    "implement cleanup logic" when {
      "YES selected" in {

        val userAnswers = emptyUserAnswers
          .set(ShareOfIncomePage, 50).success.value

        val result = userAnswers.set(DiscretionYesNoPage, true).success.value

        result.get(ShareOfIncomePage) mustNot be(defined)
      }
    }
  }
}
