/*
 * Copyright 2019 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DataMigrationMongoRepository])
trait DataMigrationRepository {
  def insertDataMigration(migration: DataMigration): Future[Boolean]
  def countDataMigrations(): Future[Int]
}

@Singleton
class DataMigrationMongoRepository @Inject()(mongo: ReactiveMongoComponent)(implicit val ec: ExecutionContext)
  extends ReactiveRepository[DataMigration, BSONObjectID]("dataMigrations", mongo.mongoConnector.db, DataMigration.formatOrganisation, objectIdFormats)
    with DataMigrationRepository {

  override def insertDataMigration(migration: DataMigration): Future[Boolean] = insert(migration).map(res => res.ok)

  override def countDataMigrations(): Future[Int] = count
}
case class DataMigration(version: Int, timestamp: Long, _id: BSONObjectID = BSONObjectID.generate())

object DataMigration {
  implicit val formatOrganisation: OFormat[DataMigration] = Json.format[DataMigration]
}
