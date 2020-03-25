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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models.{RemoveBeneficiary, TrustStartDate}
import models.beneficiaries.{Beneficiaries, ClassOfBeneficiary}
import play.api.libs.json.{JsString, JsValue, Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {

  private def getTrustStartDateUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/trust-start-date"

  def getTrustStartDate(utr: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[TrustStartDate] = {
    http.GET[TrustStartDate](getTrustStartDateUrl(utr))
  }

  private def getBeneficiariesUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/transformed/beneficiaries"

  def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[Beneficiaries] = {
    http.GET[Beneficiaries](getBeneficiariesUrl(utr))
  }

  private def amendClassOfBeneficiaryUrl(utr: String, index: Int) = s"${config.trustsUrl}/trusts/amend-unidentified-beneficiary/$utr/$index"

  def amendClassOfBeneficiary(utr: String, index: Int, description: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsString, HttpResponse](amendClassOfBeneficiaryUrl(utr, index), JsString(description))(implicitly[Writes[JsString]], HttpReads.readRaw, hc, ec)
  }

  private def addClassOfBeneficiaryUrl(utr: String) = s"${config.trustsUrl}/trusts/add-unidentified-beneficiary/$utr"

  def addClassOfBeneficiary(utr: String, beneficiary: ClassOfBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](addClassOfBeneficiaryUrl(utr), Json.toJson(beneficiary))
  }

  private def removeBeneficiaryUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/beneficiaries/remove"

  def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.PUT[JsValue, HttpResponse](removeBeneficiaryUrl(utr), Json.toJson(beneficiary))
  }
}
