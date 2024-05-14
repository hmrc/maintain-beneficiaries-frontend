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

package navigation

import base.SpecBase
import controllers.charityortrust.trust.amend.{routes => amendRts}
import controllers.charityortrust.trust.{routes => rts}
import models.{CheckMode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.charityortrust.trust._

class TrustBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new TrustBeneficiaryNavigator
  val index: Int = 0

  "Charity beneficiary navigator" when {

      "taxable" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

        "Name page -> Discretion yes no page" in {
          navigator.nextPage(NamePage, NormalMode, baseAnswers)
            .mustBe(rts.DiscretionYesNoController.onPageLoad(NormalMode))
        }

        "Discretion yes no page" when {
          "-> Yes -> Address yes no page" in {
            val answers = baseAnswers
              .set(DiscretionYesNoPage, true).success.value

            navigator.nextPage(DiscretionYesNoPage, NormalMode, answers)
              .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Share of income page" in {
            val answers = baseAnswers
              .set(DiscretionYesNoPage, false).success.value

            navigator.nextPage(DiscretionYesNoPage, NormalMode, answers)
              .mustBe(rts.ShareOfIncomeController.onPageLoad(NormalMode))
          }
        }

        "Share of income page -> Country of residence yes no page" in {
          navigator.nextPage(ShareOfIncomePage, NormalMode, baseAnswers)
            .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(NormalMode))
        }

        "Country of residence yes no page" when {
          "-> Yes -> Country of residence UK yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, NormalMode, answers)
              .mustBe(rts.CountryOfResidenceUkYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Address yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, NormalMode, answers)
              .mustBe(rts.AddressYesNoController.onPageLoad(NormalMode))
          }
        }

        "Country of residence UK yes no page" when {
          "-> Yes -> Address yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, NormalMode, answers)
              .mustBe(rts.AddressYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Country of residence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, NormalMode, answers)
              .mustBe(rts.CountryOfResidenceController.onPageLoad(NormalMode))
          }
        }

        "Country of residence page -> Address yes no page" in {
          navigator.nextPage(CountryOfResidencePage, NormalMode, baseAnswers)
            .mustBe(rts.AddressYesNoController.onPageLoad(NormalMode))
        }

        "Address yes no page" when {
          "-> Yes -> Address in the UK yes no page" in {
            val answers = baseAnswers
              .set(AddressYesNoPage, true).success.value

            navigator.nextPage(AddressYesNoPage, NormalMode, answers)
              .mustBe(rts.AddressUkYesNoController.onPageLoad(NormalMode))
          }

          "-> No" when {
            "NormalMode" must {
              "-> Start date page" in {
                val answers = baseAnswers
                  .set(AddressYesNoPage, false).success.value

                navigator.nextPage(AddressYesNoPage, NormalMode, answers)
                  .mustBe(rts.StartDateController.onPageLoad())
              }
            }

            "CheckMode" must {
              "-> Check your answers page" in {
                val answers = baseAnswers
                  .set(IndexPage, index).success.value
                  .set(AddressYesNoPage, false).success.value

                navigator.nextPage(AddressYesNoPage, CheckMode, answers)
                  .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers(index))
              }
            }
          }
        }

        "Address in the UK yes no page" when {
          "-> Yes -> UK address page" in {
            val answers = baseAnswers
              .set(AddressUkYesNoPage, true).success.value

            navigator.nextPage(AddressUkYesNoPage, NormalMode, answers)
              .mustBe(rts.UkAddressController.onPageLoad(NormalMode))
          }

          "-> No -> Non-UK address page" in {
            val answers = baseAnswers
              .set(AddressUkYesNoPage, false).success.value

            navigator.nextPage(AddressUkYesNoPage, NormalMode, answers)
              .mustBe(rts.NonUkAddressController.onPageLoad(NormalMode))
          }
        }

        "UK address page" when {
          "NormalMode" must {
            "-> Start date page" in {
              navigator.nextPage(UkAddressPage, NormalMode, baseAnswers)
                .mustBe(rts.StartDateController.onPageLoad())
            }
          }

          "CheckMode" must {
            "-> Check your answers page" in {
              val answers = baseAnswers
                .set(IndexPage, index).success.value

              navigator.nextPage(UkAddressPage, CheckMode, answers)
                .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers(index))
            }
          }
        }

        "Non-UK address page" when {
          "NormalMode" must {
            "-> Start date page" in {
              navigator.nextPage(NonUkAddressPage, NormalMode, baseAnswers)
                .mustBe(rts.StartDateController.onPageLoad())
            }
          }

          "CheckMode" must {
            "-> Check your answers page" in {
              val answers = baseAnswers
                .set(IndexPage, index).success.value

              navigator.nextPage(NonUkAddressPage, CheckMode, answers)
                .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers(index))
            }
          }
        }

        "Start date page -> Check your answers page" in {
          navigator.nextPage(StartDatePage, NormalMode, baseAnswers)
            .mustBe(rts.CheckDetailsController.onPageLoad())
        }
      }

      "non-taxable" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

        "Name page -> Country of residence yes no page" in {
          navigator.nextPage(NamePage, NormalMode, baseAnswers)
            .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(NormalMode))
        }

        "Country of residence yes no page" when {
          "-> Yes -> Country of residence UK yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, true).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, NormalMode, answers)
              .mustBe(rts.CountryOfResidenceUkYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Address yes no page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceYesNoPage, NormalMode, answers)
              .mustBe(rts.AddressYesNoController.onPageLoad(NormalMode))
          }
        }

        "Country of residence UK yes no page" when {
          "-> Yes" when {
            "NormalMode" must {
              "-> Start date page" in {
                val answers = baseAnswers
                  .set(CountryOfResidenceUkYesNoPage, true).success.value

                navigator.nextPage(CountryOfResidenceUkYesNoPage, NormalMode, answers)
                  .mustBe(rts.StartDateController.onPageLoad())
              }
            }

            "CheckMode" must {
              "-> Check your answers page" in {
                val answers = baseAnswers
                  .set(IndexPage, index).success.value
                  .set(CountryOfResidenceUkYesNoPage, true).success.value

                navigator.nextPage(CountryOfResidenceUkYesNoPage, CheckMode, answers)
                  .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers(index))
              }
            }
          }

          "-> No -> Country of residence page" in {
            val answers = baseAnswers
              .set(CountryOfResidenceUkYesNoPage, false).success.value

            navigator.nextPage(CountryOfResidenceUkYesNoPage, NormalMode, answers)
              .mustBe(rts.CountryOfResidenceController.onPageLoad(NormalMode))
          }
        }

        "Country of residence page" when {
          "NormalMode" must {
            "-> Start date page" in {
              navigator.nextPage(CountryOfResidencePage, NormalMode, baseAnswers)
                .mustBe(rts.StartDateController.onPageLoad())
            }
          }

          "CheckMode" must {
            "-> Check your answers page" in {
              val answers = baseAnswers
                .set(IndexPage, index).success.value

              navigator.nextPage(CountryOfResidencePage, CheckMode, answers)
                .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers(index))
            }
          }
        }

        "Start date page -> Check your answers page" in {
          navigator.nextPage(StartDatePage, NormalMode, baseAnswers)
            .mustBe(rts.CheckDetailsController.onPageLoad())
        }
      }
  }
}
