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
import models.beneficiaries.{Beneficiaries, CharityBeneficiary, ClassOfBeneficiary, IndividualBeneficiary, TrustBeneficiary}
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
        incomeDiscretionYesNo = false,
        entityStart = LocalDate.of(2012, 4, 15)
      )

      val classOf = ClassOfBeneficiary("Test Beneficiary", LocalDate.of(2019, 9, 23))

      val trustBeneficiary = TrustBeneficiary(
        name = "Trust Beneficiary Name",
        address = None,
        income = None,
        incomeDiscretionYesNo = true,
        entityStart = LocalDate.of(2017, 2, 28))

      val charityBeneficiary = CharityBeneficiary(
        name = "Humanitarian Endeavours Ltd",
        utr = None,
        address = None,
        income = None,
        incomeDiscretionYesNo = true,
        entityStart = LocalDate.parse("2012-03-14")
      )

      when(mockConnector.getBeneficiaries(any())(any(), any()))
        .thenReturn(Future.successful(
          Beneficiaries(
            List(individual),
            List(classOf),
            List.empty,
            List(trustBeneficiary),
            List(charityBeneficiary)
          )
        ))

      val service = new TrustServiceImpl(mockConnector)

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.getBeneficiaries("1234567890")

      whenReady(result) {
        _ mustBe Beneficiaries(
          List(individual),
          List(classOf),
          List.empty,
          List(trustBeneficiary),
          List(charityBeneficiary))
      }
    }
  }
}
