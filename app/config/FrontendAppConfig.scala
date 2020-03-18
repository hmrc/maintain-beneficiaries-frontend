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

package config

import controllers.routes
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {

  private val contactBaseUrl: String = servicesConfig.baseUrl("contact-frontend")

  private val contactHost: String = config.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier: String = "maintain-beneficiaries-frontend"

  private val assetsUrl: String = config.get[String]("assets.url")

  lazy val maintainATrustOverview: String = config.get[String]("urls.maintainATrustOverview")

  val assetsPrefix: String   = assetsUrl + config.get[String]("assets.version")
  val analyticsToken: String = config.get[String](s"google-analytics.token")
  val analyticsHost: String  = config.get[String](s"google-analytics.host")

  val reportAProblemPartialUrl: String = s"$contactBaseUrl/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl: String   = s"$contactBaseUrl/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  val betaFeedbackUrl: String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  val betaFeedbackUnauthenticatedUrl: String = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  lazy val loginUrl: String = config.get[String]("urls.login")
  lazy val loginContinueUrl: String = config.get[String]("urls.loginContinue")
  lazy val lostUtrUrl : String = config.get[String]("urls.lostUtr")
  lazy val logoutUrl: String = config.get[String]("urls.logout")

  lazy val trustsUrl: String = config.get[Service]("microservice.services.trusts").baseUrl

  lazy val trustsStoreUrl: String = config.get[Service]("microservice.services.trusts-store").baseUrl

  lazy val enrolmentStoreProxyUrl: String = config.get[Service]("microservice.services.enrolment-store-proxy").baseUrl

  lazy val relationshipName: String =
    config.get[String]("microservice.services.self.relationship-establishment.name")
  lazy val relationshipIdentifier: String =
    config.get[String]("microservice.services.self.relationship-establishment.identifier")

  def claimATrustUrl(utr: String) =
    config.get[Service]("microservice.services.claim-a-trust-frontend").baseUrl + s"/claim-a-trust/save/$utr"

  def verifyIdentityForATrustUrl(utr: String) =
    config.get[Service]("microservice.services.verify-your-identity-for-a-trust-frontend").baseUrl + s"/verify-your-identity-for-a-trust/save/$utr"

  lazy val agentsSubscriptionsUrl: String = config.get[String]("urls.agentSubscriptions")
  lazy val agentServiceRegistrationUrl = s"$agentsSubscriptionsUrl?continue=$loginContinueUrl"
  lazy val agentInvitationsUrl: String = config.get[String]("urls.agentInvitations")

  lazy val locationCanonicalList: String = config.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListNonUK: String = config.get[String]("location.canonical.list.nonUK")

  lazy val languageTranslationEnabled: Boolean =
    config.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}