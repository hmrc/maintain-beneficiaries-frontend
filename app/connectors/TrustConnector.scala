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

package connectors

import config.FrontendAppConfig
import models.beneficiaries._
import models.{RemoveBeneficiary, TaxableMigrationFlag, TrustDetails}
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClientV2, config: FrontendAppConfig) {

  private val trustsUrl: String = s"${config.trustsUrl}/trusts"
  private val beneficiariesUrl: String = s"$trustsUrl/beneficiaries"

  def getTrustDetails(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetails] = {
    val url: String = s"$trustsUrl/trust-details/$identifier/transformed"
        http.get(url"$url").execute[TrustDetails]
  }

  def getBeneficiaries(identifier: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Beneficiaries] = {
    val url: String = s"$beneficiariesUrl/$identifier/transformed"
        http.get(url"$url").execute[Beneficiaries]
  }

  def addClassOfBeneficiary(identifier: String, beneficiary: ClassOfBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-unidentified/$identifier"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def amendClassOfBeneficiary(identifier: String, index: Int, description: String)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-unidentified/$identifier/$index"
        http.post(url"$url").withBody(JsString(description)).execute[HttpResponse]
  }

  def addIndividualBeneficiary(identifier: String, beneficiary: IndividualBeneficiary)
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-individual/$identifier"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def amendIndividualBeneficiary(identifier: String, index: Int, individual: IndividualBeneficiary)
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-individual/$identifier/$index"
        http.post(url"$url").withBody(Json.toJson(individual)).execute[HttpResponse]
  }

  def addCharityBeneficiary(identifier: String, beneficiary: CharityBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-charity/$identifier"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def amendCharityBeneficiary(identifier: String, index: Int, beneficiary: CharityBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-charity/$identifier/$index"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def addTrustBeneficiary(identifier: String, beneficiary: TrustBeneficiary)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-trust/$identifier"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def amendTrustBeneficiary(identifier: String, index: Int, beneficiary: TrustBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-trust/$identifier/$index"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def addCompanyBeneficiary(identifier: String, beneficiary: CompanyBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-company/$identifier"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def amendCompanyBeneficiary(identifier: String, index: Int, beneficiary: CompanyBeneficiary)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-company/$identifier/$index"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def addEmploymentRelatedBeneficiary(identifier: String, beneficiary: EmploymentRelatedBeneficiary)
                                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-large/$identifier"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def amendEmploymentRelatedBeneficiary(identifier: String, index: Int, beneficiary: EmploymentRelatedBeneficiary)
                                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-large/$identifier/$index"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def addOtherBeneficiary(identifier: String, beneficiary: OtherBeneficiary)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/add-other/$identifier"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def amendOtherBeneficiary(identifier: String, index: Int, beneficiary: OtherBeneficiary)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/amend-other/$identifier/$index"
        http.post(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def removeBeneficiary(identifier: String, beneficiary: RemoveBeneficiary)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$beneficiariesUrl/$identifier/remove"
        http.put(url"$url").withBody(Json.toJson(beneficiary)).execute[HttpResponse]
  }

  def isTrust5mld(identifier: String)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    val url: String = s"$trustsUrl/$identifier/is-trust-5mld"
        http.get(url"$url").execute[Boolean]
  }

  def getTrustMigrationFlag(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TaxableMigrationFlag] = {
    val url = s"$trustsUrl/$identifier/taxable-migration/migrating-to-taxable"
        http.get(url"$url").execute[TaxableMigrationFlag]
  }

}
