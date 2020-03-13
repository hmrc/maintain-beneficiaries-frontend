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
import models.{Name, NationalInsuranceNumber}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TrustServiceSpec() extends FreeSpec with MockitoSugar with MustMatchers with ScalaFutures {

  val mockConnector: TrustConnector = mock[TrustConnector]

  "Trust service" - {

    "get all trustees" in {

      val trusteeInd = TrusteeIndividual(
        name = Name(firstName = "1234567890 QwErTyUiOp ,.(/)&'- name", middleName = None, lastName = "1234567890 QwErTyUiOp ,.(/)&'- name"),
        dateOfBirth = Some(LocalDate.parse("1983-09-24")),
        phoneNumber = None,
        identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
        entityStart = LocalDate.parse("2019-02-28"),
        provisional = true
      )

      val trustees = List(trusteeInd)

      val leadTrusteeIndividual = LeadTrusteeIndividual(
        name = Name(
          firstName = "First",
          middleName = None,
          lastName = "Last"
        ),
        dateOfBirth = LocalDate.parse("2010-10-10"),
        phoneNumber = "+446565657",
        email = None,
        identification = NationalInsuranceNumber("JP121212A"),
        address = None
      )

      when(mockConnector.getTrustees(any())(any(), any()))
        .thenReturn(Future.successful(Trustees(trustees)))

      when(mockConnector.getLeadTrustee(any())(any(), any()))
        .thenReturn(Future.successful(leadTrusteeIndividual))

      val service = new TrustServiceImpl(mockConnector)

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.getAllTrustees("1234567890")

      whenReady(result) { r =>
        r mustBe AllTrustees(
          lead = Some(leadTrusteeIndividual),
          trustees = trustees
        )
      }

    }

    "get trustees" in {

      val trusteeInd = TrusteeIndividual(
        name = Name(firstName = "1234567890 QwErTyUiOp ,.(/)&'- name", middleName = None, lastName = "1234567890 QwErTyUiOp ,.(/)&'- name"),
        dateOfBirth = Some(LocalDate.parse("1983-09-24")),
        phoneNumber = None,
        identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
        entityStart = LocalDate.parse("2019-02-28"),
        provisional = true
      )

      when(mockConnector.getTrustees(any())(any(), any()))
        .thenReturn(Future.successful(Trustees(List(trusteeInd))))

      val service = new TrustServiceImpl(mockConnector)

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.getBeneficiaries("1234567890")

      whenReady(result) { r =>
        r mustBe Trustees(List(trusteeInd))
      }

    }

    "remove a trustee" in {

      when(mockConnector.removeTrustee(any(),any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, None)))

      val service = new TrustServiceImpl(mockConnector)

      val trustee : RemoveTrustee =  RemoveTrustee(
        index = 0,
        endDate = LocalDate.now()
      )

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.removeTrustee("1234567890", trustee)

      whenReady(result) { r =>
        r.status mustBe 200
      }

    }


    "get trustee" in {

      def trusteeInd(id: Int) = TrusteeIndividual(
        name = Name(firstName = s"First $id", middleName = None, lastName = s"Last $id"),
        dateOfBirth = Some(LocalDate.parse("1983-09-24")),
        phoneNumber = None,
        identification = Some(TrustIdentification(None, Some("JS123456A"), None, None)),
        entityStart = LocalDate.parse("2019-02-28"),
        provisional = true
      )

      val expectedResult = trusteeInd(2)

      val trustees = List(trusteeInd(1), trusteeInd(2), trusteeInd(3))

      when(mockConnector.getTrustees(any())(any(), any()))
        .thenReturn(Future.successful(Trustees(trustees)))

      val service = new TrustServiceImpl(mockConnector)

      implicit val hc : HeaderCarrier = HeaderCarrier()

      val result = service.getTrustee("1234567890", 1)

      whenReady(result) { r =>
        r mustBe expectedResult
      }

    }

  }

}
