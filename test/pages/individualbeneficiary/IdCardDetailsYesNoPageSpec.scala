/*
 * Copyright 2022 HM Revenue & Customs
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

import models.Passport
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class IdCardDetailsYesNoPageSpec extends PageBehaviours {

  val data: Passport = Passport("country", "number", LocalDate.of(2020, 1, 1))

  "IdCardDetailsYesNo page" must {

    beRetrievable[Boolean](IdCardDetailsYesNoPage)

    beSettable[Boolean](IdCardDetailsYesNoPage)

    beRemovable[Boolean](IdCardDetailsYesNoPage)

    "implement cleanup logic when NO selected" in {
      val userAnswers = emptyUserAnswers
        .set(PassportDetailsPage, data)
        .flatMap(_.set(IdCardDetailsYesNoPage, false))

      userAnswers.get.get(IdCardDetailsPage) mustNot be(defined)
    }
  }
}
