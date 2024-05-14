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

package base

import controllers.actions._
import models.{TypeOfTrust, UserAnswers}
import navigation.FakeNavigator
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.BodyParsers
import repositories.{ActiveSessionRepository, PlaybackRepository}
import uk.gov.hmrc.auth.core.AffinityGroup

import java.time.LocalDate

trait SpecBase extends PlaySpec
  with GuiceOneAppPerSuite
  with TryValues
  with ScalaFutures
  with IntegrationPatience
  with Mocked
  with FakeTrustsApp
  with OptionValues
  with EitherValues {

  val defaultAppConfigurations: Map[String, Any] = Map(
    "auditing.enabled" -> false,
    "metrics.enabled" -> false,
    "play.filters.disabled" -> List("play.filters.csrf.CSRFFilter", "play.filters.csp.CSPFilter")
  )

  val ENGLISH = "en"
  val WELSH = "cy"

  lazy val userInternalId = "internalId"
  lazy val userUtr = "UTRUTRUTR"
  lazy val userSessionId = "sessionId"
  lazy val newId = s"$userInternalId-$userUtr-$userSessionId"

  def emptyUserAnswers: UserAnswers = UserAnswers(
    internalId = userInternalId,
    identifier = userUtr,
    sessionId = userSessionId,
    newId = newId,
    whenTrustSetup = LocalDate.now(),
    trustType = Some(TypeOfTrust.WillTrustOrIntestacyTrust),
    isTaxable = true,
    isUnderlyingData5mld = false,
    migratingFromNonTaxableToTaxable = false
  )

  def bodyParsers: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]

  val fakeNavigator = new FakeNavigator()

  def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                         affinityGroup: AffinityGroup = AffinityGroup.Organisation): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[IdentifierAction].toInstance(new FakeIdentifierAction(bodyParsers, affinityGroup)),
        bind[PlaybackIdentifierAction].toInstance(new FakePlaybackIdentifierAction()),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[PlaybackRepository].toInstance(playbackRepository),
        bind[ActiveSessionRepository].toInstance(mockSessionRepository)
      )
      .configure(defaultAppConfigurations)

}
