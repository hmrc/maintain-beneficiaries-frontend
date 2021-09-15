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

package base

import controllers.actions._
import models.{TypeOfTrust, UserAnswers}
import navigation.FakeNavigator
import org.scalatest.{BeforeAndAfter, TestSuite, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.BodyParsers
import repositories.{ActiveSessionRepository, PlaybackRepository}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}

import java.time.LocalDate

trait SpecBaseHelpers extends GuiceOneAppPerSuite with TryValues with Mocked with BeforeAndAfter with FakeTrustsApp {
  this: TestSuite =>

  final val ENGLISH = "en"
  final val WELSH = "cy"

  lazy val userInternalId = "internalId"

  def emptyUserAnswers: UserAnswers = UserAnswers(
    internalId = userInternalId,
    identifier = "UTRUTRUTR",
    whenTrustSetup = LocalDate.now(),
    trustType = Some(TypeOfTrust.WillTrustOrIntestacyTrust),
    isTaxable = true,
    isUnderlyingData5mld = false,
    migratingFromNonTaxableToTaxable = false
  )

  val bodyParsers: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]

  val fakeNavigator = new FakeNavigator()

  protected def applicationBuilder(userAnswers: Option[models.UserAnswers] = None,
                                   affinityGroup: AffinityGroup = AffinityGroup.Organisation,
                                   enrolments: Enrolments = Enrolments(Set.empty[Enrolment])): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[IdentifierAction].toInstance(new FakeIdentifierAction(bodyParsers, affinityGroup)),
        bind[PlaybackIdentifierAction].toInstance(new FakePlaybackIdentifierAction()),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[PlaybackRepository].toInstance(playbackRepository),
        bind[ActiveSessionRepository].toInstance(mockSessionRepository)
      )
}

trait SpecBase extends PlaySpec with SpecBaseHelpers
