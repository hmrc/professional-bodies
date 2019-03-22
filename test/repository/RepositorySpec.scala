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

package repository

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, SequentialNestedSuiteExecution}
import play.api.libs.json.JsString
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.{UpdateWriteResult, WriteConcern, WriteResult}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{MongoConnector, MongoSpecSupport}
import uk.gov.hmrc.play.test.UnitSpec
import models.ProfessionalBody
import repositories.{MongoProfessionalBody, ProfessionalBodiesMongoRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class RepositorySpec
  extends UnitSpec
    with BeforeAndAfterAll
    with MongoSpecSupport
    with SequentialNestedSuiteExecution
    with ScalaFutures {

  val mongoComponent: ReactiveMongoComponent = new ReactiveMongoComponent {
    override def mongoConnector: MongoConnector = mongoConnectorForTest
  }

  val professionalBody = Seq(
    ProfessionalBody("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017"),
    ProfessionalBody("Academic and Research Surgery Society of"),
    ProfessionalBody("Academic Gaming and Simulation in Education and Training Society for"),
    ProfessionalBody("Academic Primary Care Society for"),
    ProfessionalBody("Access Consultants National Register of")
  )

  val mongoProfessionalBody: Seq[MongoProfessionalBody] = professionalBody.map(organisation => MongoProfessionalBody(organisation.name))

  class MongoScenario(success: Boolean = true) {
    val repository: ProfessionalBodiesMongoRepository = new ProfessionalBodiesMongoRepository(mongoComponent, mongoProfessionalBody) {
      override def removeById(id: BSONObjectID, writeConcern: WriteConcern = WriteConcern.Default)(implicit ec: ExecutionContext): Future[WriteResult] = if (!success) {
        Future.successful(UpdateWriteResult(false, 0, 0, Seq.empty, Seq.empty, None, None, None))
      } else super.removeById(id, writeConcern)

      override def insert(entity: MongoProfessionalBody)(implicit ec: ExecutionContext): Future[WriteResult] = {
        if (!success){
          Future.successful(UpdateWriteResult(false, 0, 0, Seq.empty, Seq.empty, None, None, None))
        } else super.insert(entity)
      }
    }
  }

  override def afterAll(): Unit = new MongoScenario { await(repository.drop) }

  "The repository" should {
    "return All the organisation" in new MongoScenario {
      whenReady(repository.findAllProfessionalBodies()) { res =>
        res.map(_.name) shouldBe professionalBody.map(org => org.name)
      }
    }

/*    "return all mongoOrganisations" in new MongoScenario {
      whenReady(repository.fetchOrganisationsAdmin()) { res =>
        res shouldBe mongoOrganisations
      }
    }*/

    "add organisation to db" in new MongoScenario {
      val name = "this is definitely a new org"
      val res: Boolean = repository.insertProfessionalBody(ProfessionalBody(name)).futureValue
      res shouldBe true
      val inserted: List[MongoProfessionalBody] = repository.find("name" -> JsString(name)).futureValue
      inserted.size shouldBe 1
      inserted.head.name shouldBe name
    }

    "throw an illegal state exception when write to mongo fails" in new MongoScenario(false) {
      val ex: IllegalStateException = intercept[IllegalStateException] {
        Await.result(repository.insertProfessionalBody(ProfessionalBody("foo")), 2 seconds)
      }
      ex.getMessage shouldBe "Write to repository unsuccessful"
    }

    "remove organisation from db" in new MongoScenario {
      val mongoOrganisation = MongoProfessionalBody("fu")
      whenReady(repository.insert(mongoOrganisation)) { written =>
        val organisation = ProfessionalBody(mongoOrganisation.name, Some(mongoOrganisation._id.stringify))
        whenReady(repository.removeProfessionalBody(organisation)) { res =>
          res shouldBe true
          whenReady(repository.findById(mongoOrganisation._id)) { maybeMongoOrg =>
            maybeMongoOrg.isDefined shouldBe false
          }
        }
      }
    }

    "throw illegal argument exception given invalid BSONObjectID" in new MongoScenario {
      an[IllegalArgumentException] shouldBe thrownBy(repository.removeProfessionalBody(ProfessionalBody("foo", Some("definitely not a BSON Object ID"))))
    }

    "throw illegal argument exception given no ID" in new MongoScenario {
      an[IllegalArgumentException] shouldBe thrownBy(repository.removeProfessionalBody(ProfessionalBody("foo", None)))
    }

    "throw an illegal state exception when delete from mongo fails" in new MongoScenario(false) {
      val ex: IllegalStateException = intercept[IllegalStateException] {
        Await.result(repository.removeProfessionalBody(ProfessionalBody("foo", Some(BSONObjectID.generate().stringify))), 2 seconds)
      }
      ex.getMessage shouldBe "Delete from repository unsuccessful"
    }
  }
}
