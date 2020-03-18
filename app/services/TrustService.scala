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
import models.beneficiaries.{Beneficiaries, Beneficiary}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class TrustServiceImpl @Inject()(connector: TrustConnector) extends TrustService {

  override def getBeneficiaries(utr: String)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[Beneficiaries] =
    connector.getBeneficiaries(utr)

  override def getBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Beneficiary] =
    getBeneficiaries(utr).map(_.individualDetails(index))

}

@ImplementedBy(classOf[TrustServiceImpl])
trait TrustService {

  def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries]

  def getBeneficiary(utr: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Beneficiary]

}
