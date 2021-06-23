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
import models.HowManyBeneficiaries.Over201
import models.beneficiaries._
import models.{Description, Name, NormalMode}
import utils.Constants.MAX

import java.time.LocalDate

class BeneficiaryNavigatorSpec extends SpecBase {

  private val navigator: BeneficiaryNavigator = injector.instanceOf[BeneficiaryNavigator]

  private val individual = IndividualBeneficiary(
    name = Name(firstName = "First", middleName = None, lastName = "Last"),
    dateOfBirth = None,
    identification = None,
    address = None,
    entityStart = LocalDate.parse("2020-01-01"),
    vulnerableYesNo = None,
    roleInCompany = None,
    income = None,
    incomeDiscretionYesNo = None,
    provisional = false
  )

  private val trust = TrustBeneficiary(
    name = "Trust Beneficiary Name",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2020-01-01"),
    provisional = false
  )

  private val charity = CharityBeneficiary(
    name = "Humanitarian Endeavours Ltd",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2020-01-01"),
    provisional = false
  )

  private val company = CompanyBeneficiary(
    name = "Humanitarian Company Ltd",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2020-01-01"),
    provisional = false
  )

  private val large = EmploymentRelatedBeneficiary(
    name = "Employment Related Endeavours",
    utr = None,
    address = None,
    description = Description("Description", None, None, None, None),
    howManyBeneficiaries = Over201,
    entityStart = LocalDate.parse("2020-01-01"),
    provisional = false
  )

  private val unidentified = ClassOfBeneficiary(
    description = "Unidentified Beneficiary",
    entityStart = LocalDate.parse("2020-01-01"),
    provisional = false
  )

  private val other = OtherBeneficiary(
    description = "Other Endeavours Ltd",
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2020-01-01"),
    provisional = false
  )

  "BeneficiaryNavigator" when {

    ".addBeneficiaryRoute" when {

      "all types maxed out except individual" must {
        "redirect to individual name page" in {

          val beneficiaries = Beneficiaries(
            individualDetails = Nil,
            unidentified = List.fill(MAX)(unidentified),
            company = List.fill(MAX)(company),
            employmentRelated = List.fill(MAX)(large),
            trust = List.fill(MAX)(trust),
            charity = List.fill(MAX)(charity),
            other = List.fill(MAX)(other)
          )

          navigator.addBeneficiaryRoute(beneficiaries).url mustBe
            controllers.individualbeneficiary.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "all types maxed out except unidentified" must {
        "redirect to unidentified description page" in {

          val beneficiaries = Beneficiaries(
            individualDetails = List.fill(MAX)(individual),
            unidentified = Nil,
            company = List.fill(MAX)(company),
            employmentRelated = List.fill(MAX)(large),
            trust = List.fill(MAX)(trust),
            charity = List.fill(MAX)(charity),
            other = List.fill(MAX)(other)
          )

          navigator.addBeneficiaryRoute(beneficiaries).url mustBe
            controllers.classofbeneficiary.add.routes.DescriptionController.onPageLoad().url
        }
      }

      "all types maxed out except company" must {
        "redirect to company name page" in {

          val beneficiaries = Beneficiaries(
            individualDetails = List.fill(MAX)(individual),
            unidentified = List.fill(MAX)(unidentified),
            company = Nil,
            employmentRelated = List.fill(MAX)(large),
            trust = List.fill(MAX)(trust),
            charity = List.fill(MAX)(charity),
            other = List.fill(MAX)(other)
          )

          navigator.addBeneficiaryRoute(beneficiaries).url mustBe
            controllers.companyoremploymentrelated.company.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "all types maxed out except large" must {
        "redirect to large name page" in {

          val beneficiaries = Beneficiaries(
            individualDetails = List.fill(MAX)(individual),
            unidentified = List.fill(MAX)(unidentified),
            company = List.fill(MAX)(company),
            employmentRelated = Nil,
            trust = List.fill(MAX)(trust),
            charity = List.fill(MAX)(charity),
            other = List.fill(MAX)(other)
          )

          navigator.addBeneficiaryRoute(beneficiaries).url mustBe
            controllers.companyoremploymentrelated.employment.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "all types maxed out except trust" must {
        "redirect to trust name page" in {

          val beneficiaries = Beneficiaries(
            individualDetails = List.fill(MAX)(individual),
            unidentified = List.fill(MAX)(unidentified),
            company = List.fill(MAX)(company),
            employmentRelated = List.fill(MAX)(large),
            trust = Nil,
            charity = List.fill(MAX)(charity),
            other = List.fill(MAX)(other)
          )

          navigator.addBeneficiaryRoute(beneficiaries).url mustBe
            controllers.charityortrust.trust.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "all types maxed out except charity" must {
        "redirect to charity name page" in {

          val beneficiaries = Beneficiaries(
            individualDetails = List.fill(MAX)(individual),
            unidentified = List.fill(MAX)(unidentified),
            company = List.fill(MAX)(company),
            employmentRelated = List.fill(MAX)(large),
            trust = List.fill(MAX)(trust),
            charity = Nil,
            other = List.fill(MAX)(other)
          )

          navigator.addBeneficiaryRoute(beneficiaries).url mustBe
            controllers.charityortrust.charity.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "all types maxed out except other" must {
        "redirect to other description page" in {

          val beneficiaries = Beneficiaries(
            individualDetails = List.fill(MAX)(individual),
            unidentified = List.fill(MAX)(unidentified),
            company = List.fill(MAX)(company),
            employmentRelated = List.fill(MAX)(large),
            trust = List.fill(MAX)(trust),
            charity = List.fill(MAX)(charity),
            other = Nil
          )

          navigator.addBeneficiaryRoute(beneficiaries).url mustBe
            controllers.other.routes.DescriptionController.onPageLoad(NormalMode).url
        }
      }

      "more than one type that isn't maxed out" must {
        "redirect to add now page" when {

          "no types maxed out" in {

            val beneficiaries = Beneficiaries(
              individualDetails = Nil,
              unidentified = Nil,
              company = Nil,
              employmentRelated = Nil,
              trust = Nil,
              charity = Nil,
              other = Nil
            )

            navigator.addBeneficiaryRoute(beneficiaries).url mustBe
              controllers.routes.AddNowController.onPageLoad().url
          }

          "one type maxed out" in {

            val beneficiaries = Beneficiaries(
              individualDetails = List.fill(MAX)(individual),
              unidentified = Nil,
              company = Nil,
              employmentRelated = Nil,
              trust = Nil,
              charity = Nil,
              other = Nil
            )

            navigator.addBeneficiaryRoute(beneficiaries).url mustBe
              controllers.routes.AddNowController.onPageLoad().url
          }

          "all types maxed out bar 2" in {

            val beneficiaries = Beneficiaries(
              individualDetails = List.fill(MAX)(individual),
              unidentified = List.fill(MAX)(unidentified),
              company = List.fill(MAX)(company),
              employmentRelated = List.fill(MAX)(large),
              trust = List.fill(MAX)(trust),
              charity = Nil,
              other = Nil
            )

            navigator.addBeneficiaryRoute(beneficiaries).url mustBe
              controllers.routes.AddNowController.onPageLoad().url
          }

          "all types maxed out bar charity and trust" in {

            val beneficiaries = Beneficiaries(
              individualDetails = List.fill(MAX)(individual),
              unidentified = List.fill(MAX)(unidentified),
              company = List.fill(MAX)(company),
              employmentRelated = List.fill(MAX)(large),
              trust = Nil,
              charity = Nil,
              other = List.fill(MAX)(other)
            )

            navigator.addBeneficiaryRoute(beneficiaries).url mustBe
              controllers.charityortrust.routes.CharityOrTrustController.onPageLoad().url
          }

          "all types maxed out bar company and large" in {

            val beneficiaries = Beneficiaries(
              individualDetails = List.fill(MAX)(individual),
              unidentified = List.fill(MAX)(unidentified),
              company = Nil,
              employmentRelated = Nil,
              trust = List.fill(MAX)(trust),
              charity = List.fill(MAX)(charity),
              other = List.fill(MAX)(other)
            )

            navigator.addBeneficiaryRoute(beneficiaries).url mustBe
              controllers.companyoremploymentrelated.routes.CompanyOrEmploymentRelatedController.onPageLoad().url
          }
        }
      }
    }
  }

}
