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

import com.google.inject.ImplementedBy
import connectors.TrustConnector
import javax.inject.Inject
import models.RemoveBeneficiary
import models.beneficiaries.{Beneficiaries, CharityBeneficiary, ClassOfBeneficiary, IndividualBeneficiary, OtherBeneficiary}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class TrustServiceImpl @Inject()(connector: TrustConnector) extends TrustService {

  override def getBeneficiaries(utr: String)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[Beneficiaries] =
    connector.getBeneficiaries(utr)

  override def getUnidentifiedBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[ClassOfBeneficiary] =
    getBeneficiaries(utr).map(_.unidentified(index))

  override def getCharityBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[CharityBeneficiary] =
    getBeneficiaries(utr).map(_.charity(index))

  override def getIndividualBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[IndividualBeneficiary] =
    getBeneficiaries(utr).map(_.individualDetails(index))

  override def getOtherBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[OtherBeneficiary] =
    getBeneficiaries(utr).map(_.other(index))

  override def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    connector.removeBeneficiary(utr, beneficiary)

}

@ImplementedBy(classOf[TrustServiceImpl])
trait TrustService {

  def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries]

  def getUnidentifiedBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[ClassOfBeneficiary]

  def getCharityBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[CharityBeneficiary]

  def getIndividualBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[IndividualBeneficiary]

  def getOtherBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[OtherBeneficiary]

  def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[HttpResponse]
}
