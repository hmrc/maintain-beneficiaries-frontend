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

package controllers.classofbeneficiary.add

import java.time.LocalDate

import base.SpecBase
import connectors.TrustConnector
import models.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.classofbeneficiary.{DescriptionPage, EntityStartPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HttpResponse
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.AnswerSection
import views.html.classofbeneficiary.add.CheckDetailsView

import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val description: String = "Description"
  private val date: LocalDate = LocalDate.parse("2019-02-03")

  private lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoad().url
  private lazy val submitDetailsRoute = routes.CheckDetailsController.onSubmit().url
  private lazy val onwardRoute = controllers.routes.AddABeneficiaryController.onPageLoad().url

  private val userAnswers = emptyUserAnswers
    .set(DescriptionPage, description).success.value
    .set(EntityStartPage, date).success.value

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(DescriptionPage, description).success.value
        .set(EntityStartPage, date).success.value

      val bound = new AnswerRowConverter().bind(userAnswers, "", mock[CountryOptions])

      val answerSection = AnswerSection(None, Seq(
        bound.stringQuestion(DescriptionPage, "classOfBeneficiary.description", controllers.classofbeneficiary.add.routes.DescriptionController.onPageLoad().url),
        bound.dateQuestion(EntityStartPage, "classOfBeneficiary.entityStart", controllers.classofbeneficiary.add.routes.EntityStartController.onPageLoad().url)
      ).flatten)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(answerSection)(fakeRequest, messages).toString
    }

    "redirect to the 'add a beneficiary' page when submitted" in {

      val mockTrustConnector = mock[TrustConnector]

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = Agent)
          .overrides(bind[TrustConnector].toInstance(mockTrustConnector))
          .build()

      when(mockTrustConnector.addClassOfBeneficiary(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))

      val captor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass[UserAnswers](classOf[UserAnswers])

      val request = FakeRequest(POST, submitDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      verify(playbackRepository).set(captor.capture)

      captor.getValue.data mustBe Json.obj()

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

    "Clear out the user answers for the add class of beneficiary journey on submission" in {

      val mockTrustConnector = mock[TrustConnector]

      reset(playbackRepository)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers),
          affinityGroup = Agent)
          .overrides(
            bind[TrustConnector].toInstance(mockTrustConnector)
          ).build()

      when(mockTrustConnector.addClassOfBeneficiary(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK)))
      when(playbackRepository.set(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, submitDetailsRoute)

      whenReady(route(application, request).value) { _ =>
        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(playbackRepository).set(uaCaptor.capture())
        uaCaptor.getValue.data mustBe Json.obj()
      }
    }

  }
}