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
import models.{RemoveBeneficiary, TrustDetails}
import models.beneficiaries._
import play.api.libs.json.{JsString, JsValue, Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {

  private def getTrustDetailsUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/trust-details"

  def getTrustDetails(utr: String)(implicit hc: HeaderCarrier, ex: ExecutionContext):  Future[TrustDetails] = {
    http.GET[TrustDetails](getTrustDetailsUrl(utr))
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

  private def addIndividualBeneficiaryUrl(utr: String) = s"${config.trustsUrl}/trusts/add-individual-beneficiary/$utr"

  def addIndividualBeneficiary(utr: String, beneficiary: IndividualBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](addIndividualBeneficiaryUrl(utr), Json.toJson(beneficiary))
  }

  private def removeBeneficiaryUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/beneficiaries/remove"

  def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.PUT[JsValue, HttpResponse](removeBeneficiaryUrl(utr), Json.toJson(beneficiary))
  }

  private def amendIndividualBeneficiaryUrl(utr: String, index: Int) =
    s"${config.trustsUrl}/trusts/amend-individual-beneficiary/$utr/$index"

  def amendIndividualBeneficiary(utr: String, index: Int, individual: IndividualBeneficiary)
                                (implicit hc: HeaderCarrier, ec: ExecutionContext) : Future[HttpResponse] = {
      http.POST[JsValue, HttpResponse](amendIndividualBeneficiaryUrl(utr, index), Json.toJson(individual))
    }

  private def addCharityBeneficiaryUrl(utr: String) = s"${config.trustsUrl}/trusts/add-charity-beneficiary/$utr"

  def addCharityBeneficiary(utr: String, beneficiary: CharityBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](addCharityBeneficiaryUrl(utr), Json.toJson(beneficiary))
  }

  private def amendCharityBeneficiaryUrl(utr: String, index: Int) =
    s"${config.trustsUrl}/trusts/amend-charity-beneficiary/$utr/$index"

  def amendCharityBeneficiary(utr: String, index: Int, beneficiary: CharityBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext) : Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](amendCharityBeneficiaryUrl(utr, index), Json.toJson(beneficiary))
  }

  private def addCompanyBeneficiaryUrl(utr: String) = s"${config.trustsUrl}/trusts/add-company-beneficiary/$utr"

  def addCompanyBeneficiary(utr: String, beneficiary: CompanyBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](addCompanyBeneficiaryUrl(utr), Json.toJson(beneficiary))
  }

  private def addOtherBeneficiaryUrl(utr: String) = s"${config.trustsUrl}/trusts/add-other-beneficiary/$utr"

  def addOtherBeneficiary(utr: String, beneficiary: OtherBeneficiary)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](addOtherBeneficiaryUrl(utr), Json.toJson(beneficiary))
  }

  private def amendOtherBeneficiaryUrl(utr: String, index: Int) =
    s"${config.trustsUrl}/trusts/amend-other-beneficiary/$utr/$index"

  def amendOtherBeneficiary(utr: String, index: Int, beneficiary: OtherBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext) : Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](amendOtherBeneficiaryUrl(utr, index), Json.toJson(beneficiary))
  }
}
