/*
 * Copyright 2021 HM Revenue & Customs
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
import models.beneficiaries._
import models.{RemoveBeneficiary, TrustDetails}
import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val trustsUrl: String = s"${config.trustsUrl}/trusts"
  private val beneficiariesUrl: String = s"$trustsUrl/beneficiaries"

  def getTrustDetails(utr: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetails] = {
    val url: String = s"$trustsUrl/$utr/trust-details"
    http.GET[TrustDetails](url)
  }

  def getBeneficiaries(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries] = {
    val url: String = s"$beneficiariesUrl/$utr/transformed"
    http.GET[Beneficiaries](url)
  }

  def addClassOfBeneficiary(utr: String, beneficiary: ClassOfBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-unidentified/$utr"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendClassOfBeneficiary(utr: String, index: Int, description: String)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-unidentified/$utr/$index"
    http.POST[JsString, HttpResponse](url, JsString(description))
  }

  def addIndividualBeneficiary(utr: String, beneficiary: IndividualBeneficiary)
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-individual/$utr"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendIndividualBeneficiary(utr: String, index: Int, individual: IndividualBeneficiary)
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-individual/$utr/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(individual))
  }

  def addCharityBeneficiary(utr: String, beneficiary: CharityBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-charity/$utr"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendCharityBeneficiary(utr: String, index: Int, beneficiary: CharityBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-charity/$utr/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addTrustBeneficiary(utr: String, beneficiary: TrustBeneficiary)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-trust/$utr"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendTrustBeneficiary(utr: String, index: Int, beneficiary: TrustBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-trust/$utr/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addCompanyBeneficiary(utr: String, beneficiary: CompanyBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-company/$utr"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendCompanyBeneficiary(utr: String, index: Int, beneficiary: CompanyBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-company/$utr/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addEmploymentRelatedBeneficiary(utr: String, beneficiary: EmploymentRelatedBeneficiary)
                                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-large/$utr"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendEmploymentRelatedBeneficiary(utr: String, index: Int, beneficiary: EmploymentRelatedBeneficiary)
                                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-large/$utr/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addOtherBeneficiary(utr: String, beneficiary: OtherBeneficiary)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-other/$utr"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendOtherBeneficiary(utr: String, index: Int, beneficiary: OtherBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-other/$utr/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def removeBeneficiary(utr: String, beneficiary: RemoveBeneficiary)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/$utr/remove"
    http.PUT[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

}
