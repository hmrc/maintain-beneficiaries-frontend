package repositories

import org.scalatest.Assertion
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.MongoConnection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MongoSuite extends ScalaFutures {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(30, Seconds), interval = Span(500, Millis))

  // Database boilerplate
  private val connectionString = "mongodb://localhost:27017/maintain-beneficiaries-frontend-it"

  private def getDatabase(connection: MongoConnection) = {
    connection.database("estates-store-integration")
  }

  private def getConnection(application: Application) = {
    val mongoDriver = application.injector.instanceOf[ReactiveMongoApi]

    for {
      uri <- MongoConnection.parseURI(connectionString)
      connection <- mongoDriver.driver.connection(uri, true)
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
    running(application) {
      for {
        connection <- Future.fromTry(getConnection(application))
        _ <- dropTheDatabase(connection)
      } yield block(application, connection)
    }

}
