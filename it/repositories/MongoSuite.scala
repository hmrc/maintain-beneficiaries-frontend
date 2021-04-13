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

package repositories

import org.scalatest.Assertion
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{DefaultDB, MongoConnection}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MongoSuite extends ScalaFutures {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(30, Seconds), interval = Span(500, Millis))

  // Database boilerplate
  private val connectionString = "mongodb://localhost:27017/maintain-beneficiaries-frontend-it"

  private def getDatabase(connection: MongoConnection): Future[DefaultDB] = {
    connection.database("estates-store-integration")
  }

  private def getConnection(application: Application): Future[MongoConnection] = {
    val mongoDriver = application.injector.instanceOf[ReactiveMongoApi]

    for {
      uri <- MongoConnection.fromString(connectionString)
      connection <- mongoDriver.asyncDriver.connect(uri)
    } yield connection
  }

  protected def dropTheDatabase(connection: MongoConnection) = {
    getDatabase(connection).flatMap(_.drop())
  }

  def application : Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "mongodb.uri" -> connectionString,
      "metrics.enabled" -> false,
      "auditing.enabled" -> false,
      "mongo-async-driver.akka.log-dead-letters" -> 0
    ): _*)
    .overrides(
      bind[ActiveSessionRepository].to(classOf[ActiveSessionRepositoryImpl])
    ).build()

  def assertMongoTest(application: Application)(block: (Application, MongoConnection) => Assertion) : Future[Assertion] =
      for {
        connection <- getConnection(application)
        _ <- dropTheDatabase(connection)
      } yield running(application) {
        block(application, connection)
    }

}
