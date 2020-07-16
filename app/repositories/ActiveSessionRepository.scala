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
import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import models.{MongoDateTimeFormats, UtrSession}
import org.slf4j.LoggerFactory
import play.api.Configuration
import play.api.libs.json._
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.DateFormatter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActiveSessionRepositoryImpl @Inject()(
                                    mongo: MongoDriver,
                                    config: Configuration,
                                    dateFormatter: DateFormatter
                                  )(implicit ec: ExecutionContext, m: Materializer) extends ActiveSessionRepository {

  private val logger = LoggerFactory.getLogger("application." + this.getClass.getCanonicalName)

  private val collectionName: String = "session"

  private val cacheTtl = config.get[Int]("mongodb.session.ttlSeconds")

  private def collection: Future[JSONCollection] =
    for {
      _ <- ensureIndexes
      res <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
    } yield res

  private val lastUpdatedIndex = Index(
    key = Seq("updatedAt" -> IndexType.Ascending),
    name = Some("session-updated-at-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  private val utrIndex = Index(
    key = Seq("utr" -> IndexType.Ascending),
    name = Some("utr-index")
  )

  private lazy val ensureIndexes = for {
      collection              <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
      createdLastUpdatedIndex <- collection.indexesManager.ensure(lastUpdatedIndex)
      createdIdIndex          <- collection.indexesManager.ensure(utrIndex)
    } yield createdLastUpdatedIndex && createdIdIndex

  override def get(internalId: String): Future[Option[UtrSession]] = {

    logger.debug(s"ActiveSessionRepository getting active utr for $internalId")

    val selector = Json.obj("internalId" -> internalId)

    val modifier = Json.obj(
      "$set" -> Json.obj(
        "updatedAt" -> MongoDateTimeFormats.localDateTimeWrite.writes(LocalDateTime.now)
      )
    )

    for {
      col <- collection
      r <- col.findAndUpdate(selector, modifier, fetchNewObject = true, upsert = false)
    } yield r.result[UtrSession]
  }

  override def set(session: UtrSession): Future[Boolean] = {

    val selector = Json.obj("internalId" -> session.internalId)

    val modifier = Json.obj(
      "$set" -> (session.copy(updatedAt = LocalDateTime.now))
    )

    for {
      col <- collection
      r <- col.update(ordered = false).one(selector, modifier, upsert = true, multi = false)
    } yield r.ok
  }
}

@ImplementedBy(classOf[ActiveSessionRepositoryImpl])
trait ActiveSessionRepository {

  def get(internalId: String): Future[Option[UtrSession]]

  def set(session: UtrSession): Future[Boolean]
}
