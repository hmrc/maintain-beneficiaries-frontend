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

package utils

import base.SpecBase
import models.HowManyBeneficiaries.Over201
import models.TypeOfTrust.EmployeeRelated
import models.beneficiaries.RoleInCompany.Director
import models.beneficiaries._
import models.{Description, Name}
import viewmodels.addAnother.{AddRow, AddToRows}

import java.time.LocalDate

class AddABeneficiaryViewHelperSpec extends SpecBase {

  private val viewHelper: AddABeneficiaryViewHelper = injector.instanceOf[AddABeneficiaryViewHelper]

  private val index = 0

  private val name = Name(firstName = "Joe", middleName = None, lastName = "Bloggs")
  private val companyName = "Name"
  private val description = "Description"

  private val income = "50"
  private val utr = "utr"

  private val individualBeneficiary = IndividualBeneficiary(
    name = name,
    dateOfBirth = None,
    identification = None,
    address = None,
    entityStart = LocalDate.parse("2019-02-28"),
    vulnerableYesNo = None,
    roleInCompany = None,
    income = None,
    incomeDiscretionYesNo = None,
    provisional = false
  )

  private val unidentifiedBeneficiary = ClassOfBeneficiary(
    description = description,
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = false
  )

  private val companyBeneficiary = CompanyBeneficiary(
    name = companyName,
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val employmentRelatedBeneficiary = EmploymentRelatedBeneficiary(
    name = companyName,
    utr = None,
    address = None,
    description = Description("Description", None, None, None, None),
    howManyBeneficiaries = Over201,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val trustBeneficiary = TrustBeneficiary(
    name = companyName,
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.of(2017, 2, 28),
    provisional = false
  )

  private val charityBeneficiary = CharityBeneficiary(
    name = companyName,
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  private val otherBeneficiary = OtherBeneficiary(
    description = description,
    address = None,
    income = None,
    incomeDiscretionYesNo = None,
    entityStart = LocalDate.parse("2019-02-28"),
    provisional = false
  )

  "AddABeneficiaryViewHelper" when {

    "not migrating from non-taxable to taxable" must {

      val migratingFromNonTaxableToTaxable = false

      "render complete row" when {

        "individual" in {

          val result = viewHelper.rows(
            beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary)),
            migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
          )

          result mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = name.displayName,
                typeLabel = "Named individual",
                changeUrl = Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.individualbeneficiary.remove.routes.RemoveIndividualBeneficiaryController.onPageLoad(index).url)
              )
            )
          )
        }

        "unidentified" in {

          val result = viewHelper.rows(
            beneficiaries = Beneficiaries(unidentified = List(unidentifiedBeneficiary)),
            migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
          )

          result mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = description,
                typeLabel = "Class of beneficiaries",
                changeUrl = Some(controllers.classofbeneficiary.amend.routes.DescriptionController.onPageLoad(index).url),
                removeUrl = Some(controllers.classofbeneficiary.remove.routes.RemoveClassOfBeneficiaryController.onPageLoad(index).url)
              )
            )
          )
        }

        "company" in {

          val result = viewHelper.rows(
            beneficiaries = Beneficiaries(company = List(companyBeneficiary)),
            migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
          )

          result mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = companyName,
                typeLabel = "Named company",
                changeUrl = Some(controllers.companyoremploymentrelated.company.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.companyoremploymentrelated.company.remove.routes.RemoveCompanyBeneficiaryController.onPageLoad(index).url)
              )
            )
          )
        }

        "employment-related" in {

          val result = viewHelper.rows(
            beneficiaries = Beneficiaries(employmentRelated = List(employmentRelatedBeneficiary)),
            migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
          )

          result mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = companyName,
                typeLabel = "Employment related",
                changeUrl = Some(controllers.companyoremploymentrelated.employment.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.companyoremploymentrelated.employment.remove.routes.RemoveEmploymentBeneficiaryController.onPageLoad(index).url)
              )
            )
          )
        }

        "trust" in {

          val result = viewHelper.rows(
            beneficiaries = Beneficiaries(trust = List(trustBeneficiary)),
            migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
          )

          result mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = companyName,
                typeLabel = "Named trust",
                changeUrl = Some(controllers.charityortrust.trust.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.charityortrust.trust.remove.routes.RemoveTrustBeneficiaryController.onPageLoad(index).url)
              )
            )
          )
        }

        "charity" in {

          val result = viewHelper.rows(
            beneficiaries = Beneficiaries(charity = List(charityBeneficiary)),
            migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
          )

          result mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = companyName,
                typeLabel = "Named charity",
                changeUrl = Some(controllers.charityortrust.charity.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.charityortrust.charity.remove.routes.RemoveCharityBeneficiaryController.onPageLoad(index).url)
              )
            )
          )
        }

        "other" in {

          val result = viewHelper.rows(
            beneficiaries = Beneficiaries(other = List(otherBeneficiary)),
            migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
          )

          result mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = description,
                typeLabel = "Other beneficiary",
                changeUrl = Some(controllers.other.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.other.remove.routes.RemoveOtherBeneficiaryController.onPageLoad(index).url)
              )
            )
          )
        }
      }
    }

    "migrating from non-taxable to taxable" must {

      val migratingFromNonTaxableToTaxable = true

      "render in-progress row" when {

        "individual" when {

          val expectedResult = AddToRows(
            inProgress = List(
              AddRow(
                name = name.displayName,
                typeLabel = "Named individual",
                changeUrl = Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRedirect(index).url),
                removeUrl = Some(controllers.individualbeneficiary.remove.routes.RemoveIndividualBeneficiaryController.onPageLoad(index).url)
              )
            ),
            complete = Nil
          )

          "discretion and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(vulnerableYesNo = Some(true)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(incomeDiscretionYesNo = Some(false)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "vulnerable not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(incomeDiscretionYesNo = Some(true)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "role in company not answered for employee-related trust" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(incomeDiscretionYesNo = Some(true), vulnerableYesNo = Some(true)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable,
              trustType = Some(EmployeeRelated)
            )

            result mustBe expectedResult
          }
        }

        "company" when {

          val expectedResult = AddToRows(
            inProgress = List(
              AddRow(
                name = companyName,
                typeLabel = "Named company",
                changeUrl = Some(controllers.companyoremploymentrelated.company.amend.routes.CheckDetailsController.extractAndRedirect(index).url),
                removeUrl = Some(controllers.companyoremploymentrelated.company.remove.routes.RemoveCompanyBeneficiaryController.onPageLoad(index).url)
              )
            ),
            complete = Nil
          )

          "discretion and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(company = List(companyBeneficiary)),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(company = List(companyBeneficiary.copy(incomeDiscretionYesNo = Some(false)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }

        "trust" when {

          val expectedResult = AddToRows(
            inProgress = List(
              AddRow(
                name = companyName,
                typeLabel = "Named trust",
                changeUrl = Some(controllers.charityortrust.trust.amend.routes.CheckDetailsController.extractAndRedirect(index).url),
                removeUrl = Some(controllers.charityortrust.trust.remove.routes.RemoveTrustBeneficiaryController.onPageLoad(index).url)
              )
            ),
            complete = Nil
          )

          "discretion and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(trust = List(trustBeneficiary)),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(trust = List(trustBeneficiary.copy(incomeDiscretionYesNo = Some(false)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }

        "charity" when {

          val expectedResult = AddToRows(
            inProgress = List(
              AddRow(
                name = companyName,
                typeLabel = "Named charity",
                changeUrl = Some(controllers.charityortrust.charity.amend.routes.CheckDetailsController.extractAndRedirect(index).url),
                removeUrl = Some(controllers.charityortrust.charity.remove.routes.RemoveCharityBeneficiaryController.onPageLoad(index).url)
              )
            ),
            complete = Nil
          )

          "discretion and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(charity = List(charityBeneficiary)),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(charity = List(charityBeneficiary.copy(incomeDiscretionYesNo = Some(false)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }

        "other" when {

          val expectedResult = AddToRows(
            inProgress = List(
              AddRow(
                name = description,
                typeLabel = "Other beneficiary",
                changeUrl = Some(controllers.other.amend.routes.CheckDetailsController.extractAndRedirect(index).url),
                removeUrl = Some(controllers.other.remove.routes.RemoveOtherBeneficiaryController.onPageLoad(index).url)
              )
            ),
            complete = Nil
          )

          "discretion and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(other = List(otherBeneficiary)),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income not answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(other = List(otherBeneficiary.copy(incomeDiscretionYesNo = Some(false)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }
      }

      "render complete row" when {

        "individual" when {

          val expectedResult = AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = name.displayName,
                typeLabel = "Named individual",
                changeUrl = Some(controllers.individualbeneficiary.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.individualbeneficiary.remove.routes.RemoveIndividualBeneficiaryController.onPageLoad(index).url)
              )
            )
          )

          "not employee-related trust" when {

            "discretion true and vulnerable answered" in {

              val result = viewHelper.rows(
                beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(incomeDiscretionYesNo = Some(true), vulnerableYesNo = Some(true)))),
                migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
              )

              result mustBe expectedResult
            }

            "discretion false and share of income and vulnerable answered" in {

              val result = viewHelper.rows(
                beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(incomeDiscretionYesNo = Some(true), vulnerableYesNo = Some(true)))),
                migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
              )

              result mustBe expectedResult
            }
          }

          "employee-related trust" when {

            "discretion true and vulnerable and role in company answered" in {

              val result = viewHelper.rows(
                beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(incomeDiscretionYesNo = Some(true), vulnerableYesNo = Some(true), roleInCompany = Some(Director)))),
                migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable,
                trustType = Some(EmployeeRelated)
              )

              result mustBe expectedResult
            }

            "discretion false and share of income, vulnerable and role in company answered" in {

              val result = viewHelper.rows(
                beneficiaries = Beneficiaries(individualDetails = List(individualBeneficiary.copy(incomeDiscretionYesNo = Some(false), income = Some(income), vulnerableYesNo = Some(true), roleInCompany = Some(Director)))),
                migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable,
                trustType = Some(EmployeeRelated)
              )

              result mustBe expectedResult
            }
          }
        }

        "company" when {

          val expectedResult = AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = companyName,
                typeLabel = "Named company",
                changeUrl = Some(controllers.companyoremploymentrelated.company.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.companyoremploymentrelated.company.remove.routes.RemoveCompanyBeneficiaryController.onPageLoad(index).url)
              )
            )
          )

          "discretion true" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(company = List(companyBeneficiary.copy(incomeDiscretionYesNo = Some(true)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(company = List(companyBeneficiary.copy(incomeDiscretionYesNo = Some(false), income = Some(income)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "has UTR" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(company = List(companyBeneficiary.copy(utr = Some(utr)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }

        "trust" when {

          val expectedResult = AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = companyName,
                typeLabel = "Named trust",
                changeUrl = Some(controllers.charityortrust.trust.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.charityortrust.trust.remove.routes.RemoveTrustBeneficiaryController.onPageLoad(index).url)
              )
            )
          )

          "discretion true" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(trust = List(trustBeneficiary.copy(incomeDiscretionYesNo = Some(true)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(trust = List(trustBeneficiary.copy(incomeDiscretionYesNo = Some(false), income = Some(income)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "has UTR" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(trust = List(trustBeneficiary.copy(utr = Some(utr)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }

        "charity" when {

          val expectedResult = AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = companyName,
                typeLabel = "Named charity",
                changeUrl = Some(controllers.charityortrust.charity.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.charityortrust.charity.remove.routes.RemoveCharityBeneficiaryController.onPageLoad(index).url)
              )
            )
          )

          "discretion true" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(charity = List(charityBeneficiary.copy(incomeDiscretionYesNo = Some(true)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(charity = List(charityBeneficiary.copy(incomeDiscretionYesNo = Some(false), income = Some(income)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "has UTR" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(charity = List(charityBeneficiary.copy(utr = Some(utr)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }

        "other" when {

          val expectedResult = AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = description,
                typeLabel = "Other beneficiary",
                changeUrl = Some(controllers.other.amend.routes.CheckDetailsController.extractAndRender(index).url),
                removeUrl = Some(controllers.other.remove.routes.RemoveOtherBeneficiaryController.onPageLoad(index).url)
              )
            )
          )

          "discretion true" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(other = List(otherBeneficiary.copy(incomeDiscretionYesNo = Some(true)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }

          "discretion false and share of income answered" in {

            val result = viewHelper.rows(
              beneficiaries = Beneficiaries(other = List(otherBeneficiary.copy(incomeDiscretionYesNo = Some(false), income = Some(income)))),
              migratingFromNonTaxableToTaxable = migratingFromNonTaxableToTaxable
            )

            result mustBe expectedResult
          }
        }
      }
    }
  }
}
