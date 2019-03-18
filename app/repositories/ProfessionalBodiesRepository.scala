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

import javax.inject.{Inject, Named, Singleton}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.MultiBulkWriteResult
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats
import models.ProfessionalBody
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

// TODO naming: we should refer consistently to "professional bodies" rather than "organisations" (a professional body is a particular type of organisation)
// TODO rename to ProfessionalBodiesMongoRepository and extract a trait called ProfessionalBodiesRepository which declares following functions:
// findAllProfessionalBodies(): Future[Seq[ProfessionalBody]]
// insertProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean]
// deleteProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean]
@Singleton
class ProfessionalBodiesRepository @Inject()(mongo : ReactiveMongoComponent, @Named("professionalBodies") organisations: Seq[MongoProfessionalBody])(implicit val ec: ExecutionContext)
  extends ReactiveRepository[MongoProfessionalBody, BSONObjectID]("professionalBodies", mongo.mongoConnector.db, MongoProfessionalBody.formatMongoOrganisation, objectIdFormats) {

  // TODO move data insertion into scheduled task
  val res: MultiBulkWriteResult = Await.result(drop.flatMap(_ => bulkInsert(organisations)), 30 seconds)
  
  def findAllProfessionalBodies(): Future[Seq[ProfessionalBody]] = {
    findAll().map(result => result.map(found => ProfessionalBody(found.name, Some(found._id.stringify))))
  }

  def insertProfessionalBody(organisation: ProfessionalBody): Future[Boolean] = {
    insert(MongoProfessionalBody.apply(organisation.name)).map { res =>
      if (!res.ok) {
        throw new IllegalStateException("Write to repository unsuccessful")
      } else res.ok
    }
  }

  private def removeProfessionalBodies(organisationBSONObjectID: BSONObjectID): Future[Boolean] = {
    removeById(organisationBSONObjectID).map { res =>
      if (!res.ok) {
        throw new IllegalStateException("Delete from repository unsuccessful")
      } else res.ok
    }
  }

  def removeProfessionalBody(organisation: ProfessionalBody): Future[Boolean] = {
    removeProfessionalBodies(BSONObjectID.parse(organisation.id.getOrElse(throw new IllegalArgumentException("ID of organisation to delete must be specified"))).get)
  }
}

case class MongoProfessionalBody(name: String, _id: BSONObjectID = BSONObjectID.generate())

object MongoProfessionalBody {
  implicit val formatMongoOrganisation: OFormat[MongoProfessionalBody] = Json.format[MongoProfessionalBody]
}







