package uk.gov.hmrc.professionalbodies

import akka.stream.Materializer
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json.toJson
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.mongo.MongoSpecSupport
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.professionalbodies.models.Organisation
import uk.gov.hmrc.professionalbodies.repositories.ProfessionalBodiesRepository
import play.api.test.Helpers._
import reactivemongo.api.commands.MultiBulkWriteResult

import scala.concurrent.ExecutionContext.Implicits.global

class IntegrationSpec extends UnitSpec with BeforeAndAfterEach with BeforeAndAfterAll with GuiceOneAppPerSuite with MongoSpecSupport {

  override def beforeEach(): Unit = await(repository.drop)
  override def afterAll(): Unit = await(repository.drop)

  implicit override lazy val app: Application = GuiceApplicationBuilder().configure(
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false,
    "mongodb.uri" -> "mongodb://localhost:27017/professional-bodies-test"
  ).build()

  implicit val mat: Materializer = app.materializer

  val repository: ProfessionalBodiesRepository = app.injector.instanceOf[ProfessionalBodiesRepository]
  val organisations = Seq("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017",
    "Academic and Research Surgery Society of",
    "Academic Gaming and Simulation in Education and Training Society for",
    "Academic Primary Care Society for",
    "Access Consultants National Register of")

  def insertOrgnisations (orgnaistions: Seq[String]): MultiBulkWriteResult = {
    await(repository.bulkInsert(organisations.map(Organisation(_))))
  }
  def callEndPoint(): Result ={
    route(app, FakeRequest(GET, "/organisations")) match {
      case Some(result) => await(result)
      case _ => fail()
    }
  }

  "The App" should {
    "return the organisations as Json" in {
      insertOrgnisations(organisations)
      val result = callEndPoint()
      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe toJson(organisations)
    }

    "return return Empty Json if there are no organisations" in {
      val result = callEndPoint()
      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe toJson(Seq.empty[String])
    }
  }
}