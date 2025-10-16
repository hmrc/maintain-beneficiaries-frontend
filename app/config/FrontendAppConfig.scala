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

package config

import controllers.routes
import play.api.i18n.{Lang, Messages}
import play.api.mvc.Call
import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (
                                    contactFrontendConfig: ContactFrontendConfig,
                                    servicesConfig: ServicesConfig
                                  ) {

  final val ENGLISH = "en"
  final val WELSH = "cy"
  final val UK_COUNTRY_CODE = "GB"

  val appName: String = servicesConfig.getString("appName")

  val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl.get}/contact/beta-feedback?service=${contactFrontendConfig.serviceId.get}"

  lazy val maintainATrustOverview: String = servicesConfig.getString("urls.maintainATrustOverview")

  lazy val loginUrl: String = servicesConfig.getString("urls.login")

  lazy val loginContinueUrl: String = servicesConfig.getString("urls.loginContinue")
  lazy val logoutUrl: String = servicesConfig.getString("urls.logout")

  lazy val logoutAudit: Boolean =
    servicesConfig.getBoolean("microservice.services.features.auditing.logout")

  lazy val countdownLength: Int = servicesConfig.getInt("timeout.countdown")
  lazy val timeoutLength: Int = servicesConfig.getInt("timeout.length")

  lazy val trustsUrl: String = servicesConfig.baseUrl("trusts")

  lazy val trustAuthUrl: String = servicesConfig.baseUrl("trusts-auth")

  lazy val trustsStoreUrl: String = servicesConfig.baseUrl("trusts-store")

  lazy val locationCanonicalList: String = servicesConfig.getString("location.canonical.list.all")
  lazy val locationCanonicalListCY: String = servicesConfig.getString("location.canonical.list.allCY")

  lazy val languageTranslationEnabled: Boolean =
    servicesConfig.getBoolean("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  private def getDate(entry: String): LocalDate = {

    def getInt(path: String): Int = servicesConfig.getInt(path)

    LocalDate.of(
      getInt(s"dates.$entry.year"),
      getInt(s"dates.$entry.month"),
      getInt(s"dates.$entry.day")
    )
  }

  lazy val minDate: LocalDate = getDate("minimum")
  lazy val maxDate: LocalDate = getDate("maximum")

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case WELSH => "urls.welshHelpline"
      case _ => "urls.trustsHelpline"
    }

    servicesConfig.getString(path)
  }

  val cachettlplaybackInSeconds: Long = servicesConfig.getString("mongodb.playback.ttlSeconds").toLong
  val cachettlSessionInSeconds: Long = servicesConfig.getString("mongodb.session.ttlSeconds").toLong
  val dropIndexes: Boolean = servicesConfig.getBoolean("microservice.services.features.mongo.dropIndexes")
}
