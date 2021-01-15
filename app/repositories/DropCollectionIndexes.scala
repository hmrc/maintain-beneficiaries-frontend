package repositories

import play.api.{Configuration, Logging}
import reactivemongo.play.json.collection.JSONCollection

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

abstract class DropCollectionIndexes @Inject()(mongo: MongoDriver,
                                               config: Configuration
                                              )(implicit ec: ExecutionContext) extends Logging {

  val collectionName: String

  private final def logIndexes: Future[Unit] = {
    for {
      collection <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
      indexes <- collection.indexesManager.list()
    } yield {
      logger.info(s"[PlaybackRepository] indexes found on mongo collection $indexes")
      ()
    }
  }

  final val dropIndexes: Unit = {

    val dropIndexesFeatureEnabled: Boolean = config.get[Boolean]("microservice.services.features.mongo.dropIndexes")

    for {
      _ <- logIndexes
      _ <- if (dropIndexesFeatureEnabled) {
        for {
          collection <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
          _ <- collection.indexesManager.dropAll()
          _ <- Future.successful(logger.info(s"[PlaybackRepository] dropped indexes on collection $collectionName"))
          _ <- logIndexes
        } yield ()
      } else {
        logger.info(s"[PlaybackRepository] indexes not modified")
        Future.successful(())
      }
    } yield ()
  }

}
