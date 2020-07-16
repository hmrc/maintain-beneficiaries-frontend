package repositories

import models.UtrSession
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.language.implicitConversions

class ActiveSessionRepositorySpec extends AsyncFreeSpec with MustMatchers
  with ScalaFutures with OptionValues with MongoSuite {

  "a session repository" - {

    "must return None when no cache exists" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "Int-328969d0-557e-4559-sdba-074d0597107e"

        val repository = app.injector.instanceOf[ActiveSessionRepository]
        repository.get(internalId).futureValue mustBe None
    }

    "must return a UtrSession when one exists" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "Int-328969d0-557e-2559-96ba-074d0597107e"

        val repository = app.injector.instanceOf[ActiveSessionRepository]

        val session = UtrSession(internalId, "utr")

        val initial = repository.set(session).futureValue

        initial mustBe true

        repository.get(internalId).futureValue.value.utr mustBe "utr"
    }

    "must override an existing session for an internalId" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "Int-328969d0-557e-4559-96ba-0d4d0597107e"

        val repository = app.injector.instanceOf[ActiveSessionRepository]

        val session = UtrSession(internalId, "utr")

        repository.set(session).futureValue

        repository.get(internalId).futureValue.value.utr mustBe "utr"
        repository.get(internalId).futureValue.value.internalId mustBe internalId

        // update

        val session2 = UtrSession(internalId, "utr2")

        repository.set(session2).futureValue

        repository.get(internalId).futureValue.value.utr mustBe "utr2"
        repository.get(internalId).futureValue.value.internalId mustBe internalId
    }
  }
}
