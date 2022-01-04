/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.ImplementedBy
import models.UtrSession
import play.api.Configuration
import play.api.libs.json._
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.Helpers.idWrites

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActiveSessionRepositoryImpl @Inject()(mongo: MongoDriver,
                                            config: Configuration
                                           )(implicit ec: ExecutionContext) extends IndexesManager(mongo, config) with ActiveSessionRepository {

  override val collectionName: String = "session"

  override val cacheTtl: Int = config.get[Int]("mongodb.session.ttlSeconds")

  override val lastUpdatedIndexName: String = "session-updated-at-index"

  override def idIndex: Aux[BSONSerializationPack.type] = Index.apply(BSONSerializationPack)(
    key = Seq("utr" -> IndexType.Ascending),
    name = Some("utr-index"),
    expireAfterSeconds = None,
    options = BSONDocument.empty,
    unique = false,
    background = false,
    dropDups = false,
    sparse = false,
    version = None,
    partialFilter = None,
    storageEngine = None,
    weights = None,
    defaultLanguage = None,
    languageOverride = None,
    textIndexVersion = None,
    sphereIndexVersion = None,
    bits = None,
    min = None,
    max = None,
    bucketSize = None,
    collation = None,
    wildcardProjection = None
  )

  private def selector(internalId: String): JsObject = Json.obj(
    "internalId" -> internalId
  )

  override def get(internalId: String): Future[Option[UtrSession]] = {
    findCollectionAndUpdate[UtrSession](selector(internalId))
  }

  override def set(session: UtrSession): Future[Boolean] = {

    val modifier = Json.obj(
      "$set" -> session.copy(updatedAt = LocalDateTime.now)
    )

    for {
      col <- collection
      r <- col.update(ordered = false).one(selector(session.internalId), modifier, upsert = true, multi = false)
    } yield r.ok
  }
}

@ImplementedBy(classOf[ActiveSessionRepositoryImpl])
trait ActiveSessionRepository {

  def get(internalId: String): Future[Option[UtrSession]]

  def set(session: UtrSession): Future[Boolean]
}
