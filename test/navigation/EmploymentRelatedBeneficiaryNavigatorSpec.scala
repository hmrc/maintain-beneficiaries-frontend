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

package navigation

import base.SpecBase
import controllers.companyoremploymentrelated.employment.routes._
import models.{CheckMode, NormalMode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.companyoremploymentrelated.employment._

class EmploymentRelatedBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new EmploymentRelatedBeneficiaryNavigator
  val index: Int = 0

  "Employment related beneficiary navigator" when {

      "taxable" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

        "Name page -> Country of residence yes no page" in {
          navigator.nextPage(NamePage, NormalMode, baseAnswers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(NormalMode))
        }

        "Country of residence yes no page" when {

          val page = CountryOfResidenceYesNoPage

          "-> Yes -> Country of residence in the UK yes no page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(CountryOfResidenceUkYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Address yes no page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(AddressYesNoController.onPageLoad(NormalMode))
          }
        }

        "Country of residence in the UK yes no page" when {

          val page = CountryOfResidenceUkYesNoPage

          "-> Yes -> Address yes no page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(AddressYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Country of residence page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(CountryOfResidenceController.onPageLoad(NormalMode))
          }
        }

        "Country of residence page -> Address yes no page" in {
          navigator.nextPage(CountryOfResidencePage, NormalMode, baseAnswers)
            .mustBe(AddressYesNoController.onPageLoad(NormalMode))
        }

        "Address yes no page" when {

          val page = AddressYesNoPage

          "-> Yes -> Address in the UK yes no page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(AddressUkYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Description page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(DescriptionController.onPageLoad(NormalMode))
          }
        }

        "Address in the UK yes no page" when {

          val page = AddressUkYesNoPage

          "-> Yes -> UK address page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(UkAddressController.onPageLoad(NormalMode))
          }

          "-> No => Non-UK address page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(NonUkAddressController.onPageLoad(NormalMode))
          }
        }

        "UK address page -> Description page" in {
          navigator.nextPage(UkAddressPage, NormalMode, baseAnswers)
            .mustBe(DescriptionController.onPageLoad(NormalMode))
        }

        "Non-UK address page -> Description page" in {
          navigator.nextPage(NonUkAddressPage, NormalMode, baseAnswers)
            .mustBe(DescriptionController.onPageLoad(NormalMode))
        }

        "Description page -> Number of beneficiaries page" in {
          navigator.nextPage(DescriptionPage, NormalMode, baseAnswers)
            .mustBe(NumberOfBeneficiariesController.onPageLoad(NormalMode))
        }

        "Number of beneficiaries page" when {

          val page = NumberOfBeneficiariesPage

          "NormalMode" must {
            "-> Start date page" in {
              navigator.nextPage(page, NormalMode, baseAnswers)
                .mustBe(StartDateController.onPageLoad())
            }
          }

          "CheckMode" must {
            "-> Check your answers page" in {
              val answers = baseAnswers
                .set(IndexPage, index).success.value

              navigator.nextPage(page, CheckMode, answers)
                .mustBe(controllers.companyoremploymentrelated.employment.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
            }
          }
        }

        "Start date page -> Check your answers page" in {
          navigator.nextPage(StartDatePage, baseAnswers)
            .mustBe(CheckDetailsController.onPageLoad())
        }
      }

      "non-taxable" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

        "Name page -> Country of residence yes no page" in {
          navigator.nextPage(NamePage, NormalMode, baseAnswers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(NormalMode))
        }

        "Country of residence yes no page" when {

          val page = CountryOfResidenceYesNoPage

          "-> Yes -> Country of residence in the UK yes no page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(CountryOfResidenceUkYesNoController.onPageLoad(NormalMode))
          }

          "-> No -> Description page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(DescriptionController.onPageLoad(NormalMode))
          }
        }

        "Country of residence in the UK yes no page" when {

          val page = CountryOfResidenceUkYesNoPage

          "-> Yes -> Description page" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(DescriptionController.onPageLoad(NormalMode))
          }

          "-> No -> Country of residence page" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, NormalMode, answers)
              .mustBe(CountryOfResidenceController.onPageLoad(NormalMode))
          }
        }

        "Country of residence page -> Description page" in {
          navigator.nextPage(CountryOfResidencePage, NormalMode, baseAnswers)
            .mustBe(DescriptionController.onPageLoad(NormalMode))
        }

        "Description page -> Number of beneficiaries page" in {
          navigator.nextPage(DescriptionPage, NormalMode, baseAnswers)
            .mustBe(NumberOfBeneficiariesController.onPageLoad(NormalMode))
        }

        "Number of beneficiaries page" when {

          val page = NumberOfBeneficiariesPage

          "NormalMode" must {
            "-> Start date page" in {
              navigator.nextPage(page, NormalMode, baseAnswers)
                .mustBe(StartDateController.onPageLoad())
            }
          }

          "CheckMode" must {
            "-> Check your answers page" in {
              val answers = baseAnswers
                .set(IndexPage, index).success.value

              navigator.nextPage(page, CheckMode, answers)
                .mustBe(controllers.companyoremploymentrelated.employment.amend.routes.CheckDetailsController.renderFromUserAnswers(index))
            }
          }
        }

        "Start date page -> Check your answers page" in {
          navigator.nextPage(StartDatePage, baseAnswers)
            .mustBe(CheckDetailsController.onPageLoad())
        }
      }
  }
}
