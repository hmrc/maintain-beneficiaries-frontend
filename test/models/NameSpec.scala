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

package models

import base.SpecBase

class NameSpec extends SpecBase {

  private val nameWithNoMiddleName: Name = Name("Joe", None, "Bloggs")
  private val nameWithMiddleName: Name = Name("Joe", Some("Joseph"), "Bloggs")

  ".displayName" must {
    "display first name and last name only" when {
      "has a middle name" in {
        nameWithMiddleName.displayName mustBe "Joe Bloggs"
      }

      "has no middle name" in {
        nameWithNoMiddleName.displayName mustBe "Joe Bloggs"
      }
    }
  }

  ".displayFullName" must {
    "display full name" when {
      "has a middle name" in {
        nameWithMiddleName.displayFullName mustBe "Joe Joseph Bloggs"
      }

      "has no middle name" in {
        nameWithNoMiddleName.displayFullName mustBe "Joe Bloggs"
      }
    }
  }
}
