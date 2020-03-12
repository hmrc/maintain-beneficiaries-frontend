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

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.stream.Materializer
import com.google.inject.Inject
import models.UserAnswers
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.DateFormatter

import scala.concurrent.{ExecutionContext, Future}

class PlaybackRepository @Inject()(
                                    mongo: ReactiveMongoApi,
                                    config: Configuration,
                                    dateFormatter: DateFormatter
                                  )(implicit ec: ExecutionContext, m: Materializer) extends MongoRepository {

  private val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.playback.ttlSeconds")

  private def collection: Future[JSONCollection] = {
      mongo.database.map(_.collection[JSONCollection](collectionName))
  }

  private val lastUpdatedIndex = Index(
    key = Seq("updatedAt" -> IndexType.Ascending),
    name = Some("user-answers-updated-at-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  private val internalAuthIdIndex = Index(
    key = Seq("internalId" -> IndexType.Ascending),
    name = Some("internal-auth-id-index")
  )

  val started = Future.sequence {
      Seq(
        collection.map(_.indexesManager.ensure(lastUpdatedIndex)),
        collection.map(_.indexesManager.ensure(internalAuthIdIndex))
      )
  }.map(_ => ())

  override def get(internalId: String): Future[Option[UserAnswers]] = {

    val selector = Json.obj(
      "internalId" -> internalId
    )

    val modifier = Json.obj(
      "$set" -> Json.obj(
        "updatedAt" -> Json.obj(
          "$date" -> Timestamp.valueOf(LocalDateTime.now)
        )
      )
    )

    collection.flatMap {
      _.findAndUpdate(selector, modifier, fetchNewObject = true, upsert = false).map(_.result[UserAnswers])
    }
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "internalId" -> userAnswers.internalAuthId
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (updatedAt = LocalDateTime.now))
    )

    collection.flatMap {
      _.update(ordered = false).one(selector, modifier, upsert = true, multi = false).map {
        result => result.ok
      }
    }
  }
}

trait MongoRepository {

  val started: Future[Unit]

  def get(internalId: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]
}
