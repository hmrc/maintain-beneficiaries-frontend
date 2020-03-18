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

package services

import java.time.LocalDate

import connectors.TrustConnector
import models.Name
import models.beneficiaries.{Beneficiaries, ClassOfBeneficiary, IndividualBeneficiary}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TrustServiceSpec() extends FreeSpec with MockitoSugar with MustMatchers with ScalaFutures {

  val mockConnector: TrustConnector = mock[TrustConnector]

  "Trust service" - {

    "get beneficiaries" in {

      val individual = IndividualBeneficiary(
        name = Name(firstName = "1234567890 QwErTyUiOp ,.(/)&'- name", middleName = None, lastName = "1234567890 QwErTyUiOp ,.(/)&'- name"),
        dateOfBirth = Some(LocalDate.parse("1983-09-24")),
        nationalInsuranceNumber = None,
        address = None,
        vulnerableYesNo = false,
        income = None,
        incomeYesNo = false
      )

      val classOf = ClassOfBeneficiary("Test Beneficiary")

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(Beneficiaries(List(individual), List(classOf))))

      val service = new TrustServiceImpl(mockConnector)

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.getBeneficiaries("1234567890")

      whenReady(result) {
        _ mustBe Beneficiaries(List(individual), List(classOf))
      }

    }

    "get beneficiary" in {

      val index = 0

      val individual = IndividualBeneficiary(
        name = Name(firstName = "1234567890 QwErTyUiOp ,.(/)&'- name", middleName = None, lastName = "1234567890 QwErTyUiOp ,.(/)&'- name"),
        dateOfBirth = Some(LocalDate.parse("1983-09-24")),
        nationalInsuranceNumber = None,
        address = None,
        vulnerableYesNo = false,
        income = None,
        incomeYesNo = false
      )

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(Beneficiaries(List(individual), Nil)))

      val service = new TrustServiceImpl(mockConnector)

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.getBeneficiary("1234567890", index)

      whenReady(result) {
        _ mustBe individual
      }

    }

  }

}
