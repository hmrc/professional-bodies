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

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsArray, JsValue, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats
import uk.gov.hmrc.professionalbodies.models.Organisation

import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class ProfessionalBodiesRepository @Inject()(mongo : ReactiveMongoComponent)
  extends ReactiveRepository[Organisation, BSONObjectID]("professionalBodies", mongo.mongoConnector.db, Organisation.formatOrgansiation, objectIdFormats) {
  //class should be empty after initial release

  this.drop
  val sourceOrganisations: JsValue = Json.parse(getClass.getResourceAsStream("/json/ApprovedOrganisations.json"))
  val organisations: Seq[String] = (sourceOrganisations.as[JsArray] \\ "name").map(jsval => jsval.toString().replaceAll("\"",""))
  
  organisations.foreach(organisation => this.insert(Organisation(organisation)))

}


