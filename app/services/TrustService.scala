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

package services

import com.google.inject.ImplementedBy
import connectors.TrustConnector
import models.beneficiaries._
import models.{NationalInsuranceNumber, RemoveBeneficiary}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustServiceImpl @Inject()(connector: TrustConnector) extends TrustService {

  override def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries] =
    connector.getBeneficiaries(utr)

  override def getUnidentifiedBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[ClassOfBeneficiary] =
    getBeneficiaries(utr).map(_.unidentified(index))

  override def getCharityBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[CharityBeneficiary] =
    getBeneficiaries(utr).map(_.charity(index))

  override def getIndividualBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[IndividualBeneficiary] =
    getBeneficiaries(utr).map(_.individualDetails(index))

  override def getOtherBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[OtherBeneficiary] =
    getBeneficiaries(utr).map(_.other(index))

  override def getTrustBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustBeneficiary] =
    getBeneficiaries(utr).map(_.trust(index))

  override def getCompanyBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[CompanyBeneficiary] =
    getBeneficiaries(utr).map(_.company(index))

  override def getEmploymentBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[EmploymentRelatedBeneficiary] =
    getBeneficiaries(utr).map(_.employmentRelated(index))

  override def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    connector.removeBeneficiary(utr, beneficiary)

  override def getIndividualNinos(identifier: String, index: Option[Int])
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[String]] = {
    getBeneficiaries(identifier).map(_.individualDetails
      .zipWithIndex
      .filterNot(x => index.contains(x._2))
      .flatMap(_._1.identification)
      .collect {
        case NationalInsuranceNumber(nino) => nino
      }
    )
  }

}

@ImplementedBy(classOf[TrustServiceImpl])
trait TrustService {

  def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries]

  def getUnidentifiedBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[ClassOfBeneficiary]

  def getCharityBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[CharityBeneficiary]

  def getIndividualBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[IndividualBeneficiary]

  def getOtherBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[OtherBeneficiary]

  def getTrustBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustBeneficiary]

  def getCompanyBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[CompanyBeneficiary]

  def getEmploymentBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[EmploymentRelatedBeneficiary]

  def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]

  def getIndividualNinos(identifier: String, index: Option[Int])
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[String]]
}
