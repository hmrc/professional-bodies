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
import javax.inject.{Inject, Named, Singleton}
import models.ProfessionalBody
import play.api.libs.json.{Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ProfessionalBodiesMongoRepository])
trait ProfessionalBodiesRepository {
  def findAllProfessionalBodies(): Future[Seq[ProfessionalBody]]

  def insertProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean]

  def removeProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean]
}

@Singleton
class ProfessionalBodiesMongoRepository @Inject()(mongo: ReactiveMongoComponent, @Named("professionalBodies") organisations: Seq[MongoProfessionalBody])(implicit val ec: ExecutionContext)
  extends ReactiveRepository[MongoProfessionalBody, BSONObjectID]("professionalBodies", mongo.mongoConnector.db, MongoProfessionalBody.formatMongoOrganisation, objectIdFormats) with ProfessionalBodiesRepository {

  override def findAllProfessionalBodies(): Future[Seq[ProfessionalBody]] = {
    findAll().map(result => result.map(found => ProfessionalBody(found.name, Some(found._id.stringify))))
  }

  override def insertProfessionalBody(organisation: ProfessionalBody): Future[Boolean] = {
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







