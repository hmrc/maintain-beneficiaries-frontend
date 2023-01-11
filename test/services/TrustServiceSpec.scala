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

package services

import connectors.TrustConnector
import models.HowManyBeneficiaries.Over101
import models.beneficiaries._
import models.{BeneficiaryType, Description, Name, NationalInsuranceNumber, RemoveBeneficiary}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class TrustServiceSpec extends AnyFreeSpec with MockitoSugar with Matchers with ScalaFutures {

  private val identifier: String = "1234567890"
  implicit val hc: HeaderCarrier = HeaderCarrier()
  val mockConnector: TrustConnector = mock[TrustConnector]
  val service = new TrustServiceImpl(mockConnector)

  val individual: IndividualBeneficiary = IndividualBeneficiary(
    name = Name(firstName = "1234567890 QwErTyUiOp ,.(/)&'- name", middleName = None, lastName = "1234567890 QwErTyUiOp ,.(/)&'- name"),
    dateOfBirth = Some(LocalDate.parse("1983-09-24")),
    identification = None,
    address = None,
    vulnerableYesNo = Some(false),
    roleInCompany = None,
    income = None,
    incomeDiscretionYesNo = Some(false),
    entityStart = LocalDate.of(2012, 4, 15),
    provisional = false
  )

  val classOf: ClassOfBeneficiary = ClassOfBeneficiary(
    "Test Beneficiary",
    LocalDate.of(2019, 9, 23),
    provisional = false
  )

  val companyBeneficiary: CompanyBeneficiary = CompanyBeneficiary(
    name = "Company Beneficiary Name",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.of(2017, 2, 28),
    provisional = false
  )

  val trustBeneficiary: TrustBeneficiary = TrustBeneficiary(
    name = "Trust Beneficiary Name",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.of(2017, 2, 28),
    provisional = false
  )

  val charityBeneficiary: CharityBeneficiary = CharityBeneficiary(
    name = "Humanitarian Endeavours Ltd",
    utr = None,
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  val otherBeneficiary: OtherBeneficiary = OtherBeneficiary(
    description = "Other Endeavours Ltd",
    address = None,
    income = None,
    incomeDiscretionYesNo = Some(true),
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  val employmentRelatedBeneficiary: EmploymentRelatedBeneficiary = EmploymentRelatedBeneficiary(
    name = "Employment Related Endeavors Ltd",
    utr = None,
    address = None,
    description = Description("Other Endeavours Ltd", None, None, None, None),
    howManyBeneficiaries = Over101,
    entityStart = LocalDate.parse("2012-03-14"),
    provisional = false
  )

  "Trust service" - {

    "get beneficiaries" in {

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(
          Beneficiaries(
            List(individual),
            List(classOf),
            List(companyBeneficiary),
            List(employmentRelatedBeneficiary),
            List(trustBeneficiary),
            List(charityBeneficiary),
            List(otherBeneficiary)
          )
        ))

      val result = service.getBeneficiaries(identifier)

      whenReady(result) {
        _ mustBe Beneficiaries(
          List(individual),
          List(classOf),
          List(companyBeneficiary),
          List(employmentRelatedBeneficiary),
          List(trustBeneficiary),
          List(charityBeneficiary),
          List(otherBeneficiary)
        )
      }
    }

    "get beneficiary" in {

      val index = 0

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(Beneficiaries(List(individual), List(classOf), Nil, Nil, List(trustBeneficiary), Nil, List(otherBeneficiary))))

      whenReady(service.getUnidentifiedBeneficiary(identifier, index)) {
        _ mustBe classOf
      }

      whenReady(service.getIndividualBeneficiary(identifier, index)) {
        _ mustBe individual
      }

      whenReady(service.getOtherBeneficiary(identifier, index)) {
        _ mustBe otherBeneficiary
      }

      whenReady(service.getTrustBeneficiary(identifier, index)) {
        _ mustBe trustBeneficiary
      }

    }

    "remove a ClassOfBeneficiary" in {

      when(mockConnector.removeBeneficiary(any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val trustee: RemoveBeneficiary = RemoveBeneficiary(BeneficiaryType.ClassOfBeneficiary,
        index = 0,
        endDate = LocalDate.now()
      )

      val result = service.removeBeneficiary(identifier, trustee)

      whenReady(result) { r =>
        r.status mustBe 200
      }

    }

    ".getIndividualNinos" - {

      "return empty list" - {

        "no individuals" in {

          when(mockConnector.getBeneficiaries(any())(any(), any()))
            .thenReturn(Future.successful(Beneficiaries()))

          val result = Await.result(service.getIndividualNinos(identifier, None), Duration.Inf)

          result mustBe Nil
        }

        "there are individuals but they don't have a NINo" in {

          val individuals = List(
            individual.copy(identification = None)
          )

          when(mockConnector.getBeneficiaries(any())(any(), any()))
            .thenReturn(Future.successful(Beneficiaries(individualDetails = individuals)))

          val result = Await.result(service.getIndividualNinos(identifier, None), Duration.Inf)

          result mustBe Nil
        }

        "there is an individual with a NINo but it's the same index as the one we're amending" in {

          val individuals = List(
            individual.copy(identification = Some(NationalInsuranceNumber("nino")))
          )

          when(mockConnector.getBeneficiaries(any())(any(), any()))
            .thenReturn(Future.successful(Beneficiaries(individualDetails = individuals)))

          val result = Await.result(service.getIndividualNinos(identifier, Some(0)), Duration.Inf)

          result mustBe Nil
        }
      }

      "return NINos" - {

        "individuals have NINos and we're adding (i.e. no index)" in {

          val individuals = List(
            individual.copy(identification = Some(NationalInsuranceNumber("nino1"))),
            individual.copy(identification = Some(NationalInsuranceNumber("nino2")))
          )

          when(mockConnector.getBeneficiaries(any())(any(), any()))
            .thenReturn(Future.successful(Beneficiaries(individualDetails = individuals)))

          val result = Await.result(service.getIndividualNinos(identifier, None), Duration.Inf)

          result mustBe List("nino1", "nino2")
        }

        "individuals have NINos and we're amending" in {

          val individuals = List(
            individual.copy(identification = Some(NationalInsuranceNumber("nino1"))),
            individual.copy(identification = Some(NationalInsuranceNumber("nino2")))
          )

          when(mockConnector.getBeneficiaries(any())(any(), any()))
            .thenReturn(Future.successful(Beneficiaries(individualDetails = individuals)))

          val result = Await.result(service.getIndividualNinos(identifier, Some(0)), Duration.Inf)

          result mustBe List("nino2")
        }
      }
    }

  }

}
