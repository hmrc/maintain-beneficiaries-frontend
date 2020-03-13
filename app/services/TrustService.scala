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

import connectors.TrustConnector
import javax.inject.Inject
import models.{AllTrustees, LeadTrustee, RemoveTrustee, Trustee, Trustees}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait TrustService {

  def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Trustees]

}

class TrustServiceImpl @Inject()(connector: TrustConnector) extends TrustService {

  override def getBeneficiaries(utr: String)(implicit hc:HeaderCarrier, ec:ExecutionContext) = {
    connector.getBeneficiaries(utr)
  }

}
