package uk.gov.hmrc.professionalbodies

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, SequentialNestedSuiteExecution}
import play.api.libs.json.JsString
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.mongo.{MongoConnector, MongoSpecSupport}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.professionalbodies.models.Organisation
import uk.gov.hmrc.professionalbodies.repositories.ProfessionalBodiesRepository

import scala.concurrent.ExecutionContext.Implicits.global

class RepositorySpec
  extends UnitSpec
    with BeforeAndAfterAll
    with MongoSpecSupport
    with SequentialNestedSuiteExecution
    with ScalaFutures {

  val mongoComponent: ReactiveMongoComponent = new ReactiveMongoComponent {
    override def mongoConnector: MongoConnector = mongoConnectorForTest
  }

  val organisations = Seq(
    Organisation("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017"),
    Organisation("Academic and Research Surgery Society of"),
    Organisation("Academic Gaming and Simulation in Education and Training Society for"),
    Organisation("Academic Primary Care Society for"),
    Organisation("Access Consultants National Register of"))

  val repository = new ProfessionalBodiesRepository(mongoComponent, organisations)


  override def afterAll(): Unit = await(repository.drop)

  "The repository" should {
    "return All the organisation" in {
      val result = await(repository.find())
      println(result)
      result shouldBe organisations
    }

    "add organisation to db" in {
      val name = "this is definitely a new org"
      val res = repository.addOrganisations(Organisation(name)).futureValue
      res.ok shouldBe true
      res.n shouldBe 1
      val inserted = repository.find("name" -> JsString(name)).futureValue
      println(inserted)
      inserted.size shouldBe 1
      inserted.head shouldBe Organisation(name)
    }

    "remove organisation from db" in {
      val res = repository.removeOrganisations(organisations.head.name).futureValue
      res.ok shouldBe true
      val result = repository.find("name" -> JsString(organisations.head.name)).futureValue
      result.isEmpty shouldBe true
    }
  }

}



