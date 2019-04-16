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


import javax.inject.{Inject, Named}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.MultiBulkWriteResult
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.lock.LockMongoRepository
import scala.concurrent.duration._
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats

import scala.concurrent.{Await, ExecutionContext}

class AddProfessionalBodiesJob @Inject()(mongo: ReactiveMongoComponent) {

  val repo = LockMongoRepository(mongo.mongoConnector.db)

  object AddProfessionalBodies @Inject()(mongo: ReactiveMongoComponent, @Named("professionalBodies") organisations: Seq[MongoProfessionalBody])(implicit val ec: ExecutionContext)
    extends ReactiveRepository[MongoProfessionalBody, BSONObjectID]("professionalBodies", mongo.mongoConnector.db, MongoProfessionalBody.formatMongoOrganisation, objectIdFormats){
    def run: MultiBulkWriteResult = Await.result(drop.flatMap(_ => bulkInsert(organisations)), 30 seconds)
    run
  }
  AddProfessionalBodies.
}
