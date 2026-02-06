/*
 * Copyright 2026 HM Revenue & Customs
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

import config.FrontendAppConfig
import models.UserAnswers
import org.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model._
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlaybackRepositoryImpl @Inject() (
  val mongoComponent: MongoComponent,
  val config: FrontendAppConfig
)(implicit val ec: ExecutionContext)
    extends PlayMongoRepository[UserAnswers](
      collectionName = "user-answers",
      mongoComponent = mongoComponent,
      domainFormat = Format(UserAnswers.reads, UserAnswers.writes),
      indexes = Seq(
        IndexModel(
          ascending("updatedAt"),
          IndexOptions()
            .unique(false)
            .name("user-answers-updated-at-index")
            .expireAfter(config.cachettlplaybackInSeconds, TimeUnit.SECONDS)
        ),
        IndexModel(
          ascending("newId"),
          IndexOptions()
            .unique(false)
            .name("internal-id-and-utr-and-sessionId-compound-index")
        )
      ),
      replaceIndexes = config.dropIndexes
    )
    with PlaybackRepository {

  private def selector(internalId: String, identifier: String, sessionId: String): Bson =
    equal("newId", s"$internalId-$identifier-$sessionId")

  def get(internalId: String, identifier: String, sessionId: String): Future[Option[UserAnswers]] = {

    val modifier = Updates.set("updatedAt", LocalDateTime.now)

    val updateOption = new FindOneAndUpdateOptions().upsert(false).returnDocument(ReturnDocument.BEFORE)

    collection.findOneAndUpdate(selector(internalId, identifier, sessionId), modifier, updateOption).toFutureOption()
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {
    val find          = selector(userAnswers.internalId, userAnswers.identifier, userAnswers.sessionId)
    val updatedObject = userAnswers.copy(updatedAt = LocalDateTime.now)
    val options       = ReplaceOptions().upsert(true)

    collection.replaceOne(find, updatedObject, options).headOption().map(_.exists(_.wasAcknowledged()))
  }

}
