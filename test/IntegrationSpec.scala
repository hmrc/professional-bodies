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

import akka.stream.Materializer
import models.ProfessionalBody
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, JsString, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{DataMigrationMongoRepository, ProfessionalBodiesMongoRepository}

import scala.concurrent.{ExecutionContext, Future}

class IntegrationSpec extends WordSpec with GuiceOneAppPerSuite with BeforeAndAfterAll with ScalaFutures with Eventually with IntegrationPatience with SequentialNestedSuiteExecution with Matchers{

  implicit override lazy val app: Application = GuiceApplicationBuilder().configure(
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false,
    "mongodb.uri" -> "mongodb://localhost:27017/professional-bodies-test"
  ).build()

  implicit val mat: Materializer = app.materializer
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  val repo: ProfessionalBodiesMongoRepository = app.injector.instanceOf[ProfessionalBodiesMongoRepository]
  val dataMigrationRepo: DataMigrationMongoRepository = app.injector.instanceOf[DataMigrationMongoRepository]

  override protected def beforeAll(): Unit = eventually {
    status(route(app, FakeRequest("GET", "/admin/health")).get) should be(Status.OK)
  }

  def callEndPoint(method: String): Future[Result] = {
    route(app, FakeRequest(method, "/professionalBodies")) match {
      case Some(result) => result
      case _ => fail()
    }
  }

  def callEndPoint(method: String, professionalBody: JsValue): Future[Result] = {
    route(app, FakeRequest(method, "/professionalBodies").withJsonBody(professionalBody)) match {
      case Some(result) => result
      case _ => fail()
    }
  }

  def sortedResult (result: Result) = {
    //jsonBodyOf(result).as[JsArray].value.map(_.toString().replaceAll("\"","")).sorted.map(organisation => ProfessionalBody(          organisation))
    result.body.asInstanceOf[JsValue].as[JsArray].value.map(_.toString()).sorted.map(org => ProfessionalBody(org))
  }

  "The App" should {
    "return the organisations as Json" in {
      val result = callEndPoint(GET)
      status(result) shouldBe OK
      sortedResult(await(result)).size shouldBe DefaultProfessionalBodies.load.size
    }

    "add organisation to db" ignore {
      repo.drop
      val name = "a new org that I added"
      val org = Json.parse(
        s"""
          |{"name": "a new org that I added"}
        """.stripMargin)

      val res = callEndPoint(POST, org)
      status(res) shouldBe CREATED
      Thread.sleep(500)

      val inserted = repo.findAll().futureValue

      inserted.size shouldBe 1
      inserted.head.name shouldBe name
    }

    "adding a valid json as invalid organisation to body" ignore  {
      val org = Json.parse(
        s"""
           |{"fu": "bar"}
        """.stripMargin)
      val res = callEndPoint(POST, org)
      status(res) shouldBe BAD_REQUEST
    }

    "remove organisation from db" ignore {
      val professionalBody = contentAsJson(callEndPoint(GET)).as[JsArray].value.head

      val professionalBodyName = (professionalBody \ "name").get

      val res = callEndPoint(DELETE, professionalBody)
      status(res) shouldBe ACCEPTED

      val result = repo.find("name" -> JsString(professionalBodyName.as[String]))
      result.value shouldBe None
    }

    // hack!! we use sequential nested suite execution and clean up here because Play Mongo has shutdown when "afterAll" is called
    // the alternative is to establish our own mongo connection in "afterAll" drop collections directly
    "clean up after itself" in {
      whenReady(repo.drop) { professionalBodiesDropped =>
        whenReady(dataMigrationRepo.drop) { dataMigrationsDropped =>
          (professionalBodiesDropped && dataMigrationsDropped) should be(true)

        }
      }
    }
  }
}