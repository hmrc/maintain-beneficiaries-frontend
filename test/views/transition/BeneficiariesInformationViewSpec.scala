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

package views.transition

import views.behaviours.ViewBehaviours
import views.html.transition.BeneficiariesInformationView

class BeneficiariesInformationViewSpec extends ViewBehaviours {

  "BeneficiariesInformation view" must {

    val application = applicationBuilder().build()

    val view = application.injector.instanceOf[BeneficiariesInformationView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPageTitleWithSectionSubheading(applyView, "beneficiariesMoreInformation")

    behave like pageWithGuidance(applyView, "beneficiariesMoreInformation",
      "p1",
      "p2",
      "subheading",
      "p3",
      "b1",
      "b2",
      "b3",
      "subheading2",
      "p4",
      "b4",
      "b5",
      "b6",
      "b7",
      "subheading3",
      "p5",
      "b8",
      "b9",
      "p6")
  }
}
