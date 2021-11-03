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
import models.{RemoveBeneficiary, TaxableMigrationFlag, TrustDetails}
import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val trustsUrl: String = s"${config.trustsUrl}/trusts"
  private val beneficiariesUrl: String = s"$trustsUrl/beneficiaries"

  def getTrustDetails(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetails] = {
    val url: String = s"$trustsUrl/trust-details/$identifier/transformed"
    http.GET[TrustDetails](url)
  }

  def getBeneficiaries(identifier: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries] = {
    val url: String = s"$beneficiariesUrl/$identifier/transformed"
    http.GET[Beneficiaries](url)
  }

  def addClassOfBeneficiary(identifier: String, beneficiary: ClassOfBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-unidentified/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendClassOfBeneficiary(identifier: String, index: Int, description: String)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-unidentified/$identifier/$index"
    http.POST[JsString, HttpResponse](url, JsString(description))
  }

  def addIndividualBeneficiary(identifier: String, beneficiary: IndividualBeneficiary)
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-individual/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendIndividualBeneficiary(identifier: String, index: Int, individual: IndividualBeneficiary)
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-individual/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(individual))
  }

  def addCharityBeneficiary(identifier: String, beneficiary: CharityBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-charity/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendCharityBeneficiary(identifier: String, index: Int, beneficiary: CharityBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-charity/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addTrustBeneficiary(identifier: String, beneficiary: TrustBeneficiary)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-trust/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendTrustBeneficiary(identifier: String, index: Int, beneficiary: TrustBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-trust/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addCompanyBeneficiary(identifier: String, beneficiary: CompanyBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-company/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendCompanyBeneficiary(identifier: String, index: Int, beneficiary: CompanyBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-company/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addEmploymentRelatedBeneficiary(identifier: String, beneficiary: EmploymentRelatedBeneficiary)
                                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-large/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendEmploymentRelatedBeneficiary(identifier: String, index: Int, beneficiary: EmploymentRelatedBeneficiary)
                                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-large/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def addOtherBeneficiary(identifier: String, beneficiary: OtherBeneficiary)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-other/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def amendOtherBeneficiary(identifier: String, index: Int, beneficiary: OtherBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-other/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def removeBeneficiary(identifier: String, beneficiary: RemoveBeneficiary)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/$identifier/remove"
    http.PUT[JsValue, HttpResponse](url, Json.toJson(beneficiary))
  }

  def isTrust5mld(identifier: String)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    val url: String = s"$trustsUrl/$identifier/is-trust-5mld"
    http.GET[Boolean](url)
  }

  def getTrustMigrationFlag(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TaxableMigrationFlag] = {
    val url = s"$trustsUrl/$identifier/taxable-migration/migrating-to-taxable"
    http.GET[TaxableMigrationFlag](url)
  }

}
