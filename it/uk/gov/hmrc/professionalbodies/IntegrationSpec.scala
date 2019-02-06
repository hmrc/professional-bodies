package uk.gov.hmrc.professionalbodies

import akka.stream.Materializer
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.professionalbodies.models.Organisation
import uk.gov.hmrc.professionalbodies.repositories.{DefaultProfessionalBodies, ProfessionalBodiesRepository}

import scala.concurrent.ExecutionContext

class IntegrationSpec extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterAll with ScalaFutures {

  implicit override lazy val app: Application = GuiceApplicationBuilder().configure(
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false,
    "mongodb.uri" -> "mongodb://localhost:27017/professional-bodies-test"
  ).build()

  implicit val mat: Materializer = app.materializer
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  val repo: ProfessionalBodiesRepository = app.injector.instanceOf[ProfessionalBodiesRepository]

  override protected def afterAll(): Unit = repo.drop

  def callEndPoint(endpoint: String, method: String): Result = {
    route(app, FakeRequest(method, s"/$endpoint")) match {
      case Some(result) => await(result)
      case _ => fail()
    }
  }

  def callEndPoint(endpoint: String, method: String, org: JsValue): Result =
    route(app, FakeRequest(method, s"/$endpoint").withJsonBody(org)) match {
      case Some(result) => await(result)
      case _ => fail()
    }


  def sortedResult (result: Result): Seq[Organisation] = {
    jsonBodyOf(result).as[JsArray].value.map(_.toString().replaceAll("\"","")).sorted.map(organisation => Organisation(organisation))
  }

  "The App" should {
    "return the organisations as Json" in {
      val result = await(callEndPoint("organisations", GET))
      status(result) shouldBe OK
      sortedResult(result).size shouldBe DefaultProfessionalBodies.load.size
    }

    "add organisation to db" in {
      repo.drop
      val name = "a new org that I added"
      val org = Json.parse(
        s"""
          |{"name": "a new org that I added"}
        """.stripMargin)

      val res = callEndPoint("addOrganisation", POST, org)
      status(res) shouldBe OK
      Thread.sleep(500)

      val inserted = repo.findAll().futureValue
      println(inserted)

      inserted.size shouldBe 1
      inserted.head shouldBe Organisation(name)
    }

    "remove organisation from db" in {
//      val name = "a new org"
//      val org = Json.parse(
//        s"""
//          |{"name": "$name"}
//        """.stripMargin)
//
//      val res = await(callEndPoint("removeOrganisation", DELETE, org))
//      status(res) shouldBe OK
//
//      val result = repo.find("name" -> JsString(name))
//      result.isEmpty shouldBe true
    }
  }
}