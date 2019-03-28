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

package controllers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import models.ProfessionalBody
import org.scalatest.{MustMatchers, WordSpec}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import repositories.ProfessionalBodiesRepository
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ProfessionalBodiesControllerSpec extends WordSpec with MustMatchers {

  implicit val mat: Materializer = ActorMaterializer()(ActorSystem())

  val defaultProfessionalBodies: Seq[ProfessionalBody] = Seq(
    ProfessionalBody("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017"),
    ProfessionalBody("Academic and Research Surgery Society of"),
    ProfessionalBody("Academic Gaming and Simulation in Education and Training Society for"),
    ProfessionalBody("Academic Primary Care Society for"),
    ProfessionalBody("Access Consultants National Register of")
  )

  val professionalBodyJson: JsValue = Json.toJson(defaultProfessionalBodies.head)

  val defaultMockRepo: ProfessionalBodiesRepository = new ProfessionalBodiesRepository {

    override def findAllProfessionalBodies(): Future[Seq[ProfessionalBody]] = Future.successful(defaultProfessionalBodies)

    override def insertProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean] = Future.successful(true)

    override def removeProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean] = Future.successful(true)

  }

  val failingMockRepo: ProfessionalBodiesRepository = new ProfessionalBodiesRepository {

    override def findAllProfessionalBodies(): Future[Seq[ProfessionalBody]] = Future.successful(Seq.empty)

    override def insertProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean] = Future.successful(false)

    override def removeProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean] = Future.successful(false)

  }

  val req = FakeRequest()

  class scenario(repo: ProfessionalBodiesRepository = defaultMockRepo) {
    val controller = new ProfessionalBodiesController(repo)
  }

  "list" should {

    "return status 200" in new scenario {
      status(call(controller.getProfessionalBodies, req)) must be(Status.OK)
    }

    "return professional bodies as JSON" in new scenario {
      contentAsJson(call(controller.getProfessionalBodies, req)) must be(Json.toJson(defaultProfessionalBodies))
    }

  }

  "add" should {

    "return status 200" in new scenario {
      status(call(controller.addProfessionalBody(), req.withJsonBody(professionalBodyJson))) must be(Status.OK)
    }

    "return status 500 when repo cannot save professional body" in new scenario(failingMockRepo) {
      status(call(controller.addProfessionalBody(), req.withJsonBody(professionalBodyJson))) must be(Status.INTERNAL_SERVER_ERROR)
    }

    "return status 400 given invalid professional body" in new scenario {
      status(call(controller.addProfessionalBody(), req.withJsonBody(Json.parse(
        """
          |{"foo":"bar"}
        """.stripMargin)))) must be(Status.BAD_REQUEST)
    }

  }

  "remove" should {

    "return status 200" in new scenario {
      status(call(controller.removeProfessionalBody(), req.withJsonBody(professionalBodyJson))) must be(Status.OK)
    }

    "return status 500 when repo cannot save professional body" in new scenario(failingMockRepo) {
      status(call(controller.removeProfessionalBody(), req.withJsonBody(professionalBodyJson))) must be(Status.INTERNAL_SERVER_ERROR)
    }

    "return status 400 given invalid professional body" in new scenario {
      status(call(controller.removeProfessionalBody(), req.withJsonBody(Json.parse(
        """
          |{"foo":"bar"}
        """.stripMargin)))) must be(Status.BAD_REQUEST)
    }

  }

}
