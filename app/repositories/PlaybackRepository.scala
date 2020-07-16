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

package repositories

import java.time.LocalDateTime

import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import models.{MongoDateTimeFormats, UserAnswers}
import org.slf4j.LoggerFactory
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.WriteConcern
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.DateFormatter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlaybackRepositoryImpl @Inject()(
                                        mongo: MongoDriver,
                                        config: Configuration,
                                        dateFormatter: DateFormatter
                                      )(implicit ec: ExecutionContext, m: Materializer) extends PlaybackRepository {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  private val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.playback.ttlSeconds")

  private def collection: Future[JSONCollection] =
    for {
      _ <- ensureIndexes
      res <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
    } yield res

  private val lastUpdatedIndex = Index(
    key = Seq("updatedAt" -> IndexType.Ascending),
    name = Some("user-answers-updated-at-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  private val internalIdAndUtrIndex = Index(
    key = Seq("internalId" -> IndexType.Ascending, "utr" -> IndexType.Ascending),
    name = Some("internal-id-and-utr-compound-index")
  )

  private lazy val ensureIndexes = for {
      collection              <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
      createdLastUpdatedIndex <- collection.indexesManager.ensure(lastUpdatedIndex)
      createdIdIndex          <- collection.indexesManager.ensure(internalIdAndUtrIndex)
    } yield createdLastUpdatedIndex && createdIdIndex

  override def get(internalId: String, utr: String): Future[Option[UserAnswers]] = {

    logger.debug(s"PlaybackRepository getting user answers for $internalId")

    val selector = Json.obj(
      "internalId" -> internalId,
      "utr" -> utr
    )

    val modifier = Json.obj(
      "$set" -> Json.obj(
        "updatedAt" -> MongoDateTimeFormats.localDateTimeWrite.writes(LocalDateTime.now)
      )
    )

    for {
      col <- collection
      r <- col.findAndUpdate(selector, modifier, fetchNewObject = true, upsert = false)
    } yield r.result[UserAnswers]
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "internalId" -> userAnswers.internalId,
      "utr" -> userAnswers.utr
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (updatedAt = LocalDateTime.now))
    )

    for {
      col <- collection
      r <- col.update(ordered = false).one(selector, modifier, upsert = true, multi = false)
    } yield r.ok
  }

  override def resetCache(internalId: String, utr: String): Future[Option[JsObject]] = {

    logger.debug(s"PlaybackRepository resetting cache for $internalId")

    val selector = Json.obj(
      "internalId" -> internalId,
      "utr" -> utr
    )

    for {
      col <- collection
      r <- col.findAndRemove(selector, None, None, WriteConcern.Default, None, None, Seq.empty)
    } yield r.value
  }
}

trait PlaybackRepository {

  def get(internalId: String, utr: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]

  def resetCache(internalId: String, utr: String): Future[Option[JsObject]]
}
