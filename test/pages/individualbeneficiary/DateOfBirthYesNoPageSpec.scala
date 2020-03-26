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

package pages.individualbeneficiary

import java.time.LocalDate

import pages.behaviours.PageBehaviours

class DateOfBirthYesNoPageSpec extends PageBehaviours {

  "DateOfBirthYesNo page" must {

    beRetrievable[Boolean](DateOfBirthYesNoPage)

    beSettable[Boolean](DateOfBirthYesNoPage)

    beRemovable[Boolean](DateOfBirthYesNoPage)

    "implement cleanup logic when NO selected" in {
      val userAnswers = emptyUserAnswers
        .set(DateOfBirthPage, LocalDate.of(2012, 12, 20))
        .flatMap(_.set(DateOfBirthYesNoPage, false))

      userAnswers.get.get(DateOfBirthPage) mustNot be(defined)
    }
  }
}
