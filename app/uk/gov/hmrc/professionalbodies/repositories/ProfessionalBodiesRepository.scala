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

package uk.gov.hmrc.professionalbodies.repositories

import javax.inject.{Inject, Named, Singleton}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.MultiBulkWriteResult
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats
import uk.gov.hmrc.professionalbodies.models.{MongoOrganisation, Organisation}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ProfessionalBodiesRepository @Inject()(mongo : ReactiveMongoComponent, @Named("professionalBodies") organisations: Seq[MongoOrganisation])(implicit val ec: ExecutionContext)
  extends ReactiveRepository[MongoOrganisation, BSONObjectID]("professionalBodies", mongo.mongoConnector.db, MongoOrganisation.formatMongoOrganisation, objectIdFormats) {
  //class should be empty after initial release
  val res: MultiBulkWriteResult = Await.result(drop.flatMap(_ => bulkInsert(organisations)), 30 seconds)
  println("Bulk insert completed: " + res.ok)

  def fetchOrganisations(): Future[Seq[MongoOrganisation]] = findAll()

  def addOrganisations(organisation: Organisation): Future[Boolean] = this.insert(MongoOrganisation.apply(organisation.name)).map(_.ok)

  def removeOrganisations(organisationBSONObjectID: BSONObjectID): Future[Boolean] = this.removeById(organisationBSONObjectID).map(_.ok)
}







