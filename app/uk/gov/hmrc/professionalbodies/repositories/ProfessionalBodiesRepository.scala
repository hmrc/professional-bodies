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
import play.api.libs.json.{JsArray, JsString, JsValue, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.MultiBulkWriteResult
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats
import uk.gov.hmrc.professionalbodies.models.Organisation

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ProfessionalBodiesRepository @Inject()(mongo : ReactiveMongoComponent, @Named("professionalBodies") organisations: Seq[Organisation])(implicit val ec: ExecutionContext)
  extends ReactiveRepository[Organisation, BSONObjectID]("professionalBodies", mongo.mongoConnector.db, Organisation.formatOrganisation, objectIdFormats) {
  //class should be empty after initial release
  val res: MultiBulkWriteResult = Await.result(drop.flatMap(_ => bulkInsert(organisations)), 30 seconds)
  println("Bulk insert completed: " + res.ok)

  def fetchOrganisations():Future[Seq[String]] = {
    for {
      organisations <- this.findAll()
    } yield organisations.map(_.name)
  }

  def addOrganisations(organisation: Organisation): Future[Boolean] ={

    this.insert(organisation).map(_.ok)
  }

  def removeOrganisations(organisationName: String): Future[Boolean] ={
    this.remove("name" -> JsString(organisationName)).map(_.ok)
  }
}

// dirty hack to support Guice app routing tests
object DefaultProfessionalBodies {

  def load: Seq[Organisation] = {
    val sourceOrganisations: JsValue =
      Json.
        parse(getClass.
          getResourceAsStream("/json/ApprovedOrganisations.json"))

    val organisations: Seq[String] =
      (sourceOrganisations.
        as[JsArray] \\ "name").
        map(jsval => jsval.toString().
          replaceAll("\"", ""))

    organisations.map(organisation => Organisation(organisation))
  }

}

case class MongoOrganisation(name: String, id: BSONObjectID = BSONObjectID.generate()) {
  def apply(organisation: Organisation): MongoOrganisation = MongoOrganisation(organisation.name)
}

