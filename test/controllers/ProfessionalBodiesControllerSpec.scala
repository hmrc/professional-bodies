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

import akka.stream.Materializer
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.Matchers
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.test.UnitSpec
import models.ProfessionalBody
import repositories.{MongoProfessionalBody, ProfessionalBodiesMongoRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfessionalBodiesControllerSpec extends UnitSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar {

  val mockRepository: ProfessionalBodiesMongoRepository = mock[ProfessionalBodiesMongoRepository]
  val fakeRequestGetOrganisations = FakeRequest("GET", "/organisations")
  val fakeRequestAddOrganisation = FakeRequest("POST", "/addOrganisation")
  val env: Environment = Environment.simple()
  val configuration: Configuration = Configuration.load(env)
  implicit val messageApi = new DefaultMessagesApi(env, configuration, new DefaultLangs(configuration))
  val controller = new ProfessionalBodiesController(mockRepository)
  implicit val mat: Materializer = app.materializer

  val organisations = Seq(
    ProfessionalBody("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017"),
    ProfessionalBody("Academic and Research Surgery Society of"),
    ProfessionalBody("Academic Gaming and Simulation in Education and Training Society for"),
    ProfessionalBody("Academic Primary Care Society for"),
    ProfessionalBody("Access Consultants National Register of")
  )

  val mongoOrgs: Seq[MongoProfessionalBody] = organisations.map(org => MongoProfessionalBody(org.name))

  def theRepoWillReturnSomeOrganisations: OngoingStubbing[Future[Seq[ProfessionalBody]]] = {
    when(mockRepository.findAllProfessionalBodies()).thenReturn(Future.successful(organisations))
  }

/*  def theRepoWillReturnSomeAdminOrganisations: OngoingStubbing[Future[Seq[MongoOrganisation]]] = {
    when(mockRepository.fetchOrganisationsAdmin()).thenReturn(Future.successful(mongoOrgs))
  }*/

  def theRepoWillReturnBooleanWhenAddingOrgs(organisation: ProfessionalBody, boolean: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockRepository.insertProfessionalBody(organisation)).thenReturn(Future.successful(boolean))
  }

  def theRepoWillReturnBooleanWhenDeletingOrgs(organisation: ProfessionalBody, boolean: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockRepository.removeProfessionalBody(organisation)).thenReturn(Future.successful(boolean))
  }


  "GET /" should {
    "return 200" in {
      theRepoWillReturnSomeOrganisations
      val result = controller.getProfessionalBodies()(fakeRequestGetOrganisations)
      status(result) shouldBe Status.OK
    }

    "return the list of organisations names" in {
      theRepoWillReturnSomeOrganisations
      val result = await(controller.getProfessionalBodies()(fakeRequestGetOrganisations))
      jsonBodyOf(result) shouldBe Json.toJson(organisations)
    }
  }

/*  "GET /adminOrganisations" should {

    "return 200" in {
      theRepoWillReturnSomeAdminOrganisations
      val result = controller.getAdminOrganisations()(fakeRequestGetOrganisations)
      status(result) shouldBe Status.OK
    }

    "return the list of organisations" in {
      theRepoWillReturnSomeAdminOrganisations
      val result = await(controller.getAdminOrganisations()(fakeRequestGetOrganisations))
      jsonBodyOf(result) shouldBe Json.toJson(mongoOrgs)
    }
  }*/

  "POST /addOrganisation" should {

    "return 200" in {
      val organisation = ProfessionalBody("bar")
      theRepoWillReturnBooleanWhenAddingOrgs(organisation, boolean = true)
      val req = FakeRequest().withJsonBody(Json.toJson(organisation))
      val result: Future[Result] = call(controller.addProfessionalBody(), req)
      status(result) shouldBe Status.OK
    }

    "return 500 when repo returns false on being unable to add valid Org" in {
      val organisation = ProfessionalBody("bar")
      theRepoWillReturnBooleanWhenAddingOrgs(organisation, boolean = false)
      val req = FakeRequest().withJsonBody(Json.toJson(organisation))
      val result: Future[Result] = call(controller.addProfessionalBody(), req)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return 400 when invalid org is added" in {
      val req = FakeRequest().withJsonBody(Json.toJson("{\"fu\":\"bar\"}"))
      val result: Future[Result] = call(controller.addProfessionalBody(), req)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "DELETE /removeOrganisation" should {

    "return 200" in {
      val organisation = ProfessionalBody("bar", Some("id"))
      theRepoWillReturnBooleanWhenDeletingOrgs(organisation, boolean = true)
      val req = FakeRequest().withJsonBody(Json.toJson(organisation))
      val result: Future[Result] = call(controller.removeProfessionalBody(), req)
      status(result) shouldBe Status.OK
    }

    "return 500 when repo returns false on being unable to remove valid Org" in {
      val organisation = ProfessionalBody("bar", Some("id"))
      theRepoWillReturnBooleanWhenDeletingOrgs(organisation, boolean = false)
      val req = FakeRequest().withJsonBody(Json.toJson(organisation))
      val result: Future[Result] = call(controller.removeProfessionalBody(), req)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return 400 when invalid org is sent to be removed" in {
      val req = FakeRequest().withJsonBody(Json.toJson("{\"fu\":\"barId\"}"))
      val result: Future[Result] = call(controller.removeProfessionalBody(), req)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
