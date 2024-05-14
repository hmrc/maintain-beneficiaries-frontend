/*
 * Copyright 2024 HM Revenue & Customs
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

import com.mongodb.client.model.Indexes.ascending
import com.mongodb.client.model.ReturnDocument
import config.FrontendAppConfig
import models.UtrSession
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActiveSessionRepositoryImpl @Inject()(
                                             mongo: MongoComponent,
                                             config: FrontendAppConfig
                                           )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[UtrSession](
    mongoComponent = mongo,
    collectionName = "session",
    domainFormat = UtrSession.formats,
    indexes = Seq(
      IndexModel(
        ascending("updatedAt"),
        IndexOptions()
          .unique(false)
          .name("session-updated-at-index")
          .expireAfter(config.cachettlSessionInSeconds, TimeUnit.SECONDS)),
      IndexModel(
        ascending("utr"),
        IndexOptions()
          .unique(false)
          .name("utr-index")
      )
    ),
    replaceIndexes = config.dropIndexes
  )
    with ActiveSessionRepository {

  private def selector(internalId: String): Bson =
    Filters.eq("internalId", internalId)

  override def get(internalId: String): Future[Option[UtrSession]] = {
    val modifier = Updates.set("updatedAt", LocalDateTime.now())

    val updateOption = new FindOneAndUpdateOptions()
      .upsert(false)
      .returnDocument(ReturnDocument.AFTER)

    collection.findOneAndUpdate(selector(internalId), modifier, updateOption).toFutureOption()
  }

  override def set(session: UtrSession): Future[Boolean] = {

    val find = selector(session.internalId)
    val updatedObject = session.copy(updatedAt = LocalDateTime.now)
    val options = ReplaceOptions().upsert(true)

    collection.replaceOne(find, updatedObject, options).headOption().map(_.exists(_.wasAcknowledged()))
  }
}
