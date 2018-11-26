package uk.gov.hmrc.professionalbodies

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.mongo.{MongoConnector, MongoSpecSupport}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.professionalbodies.models.Organisation
import uk.gov.hmrc.professionalbodies.repositories.ProfessionalBodiesRepository

import scala.concurrent.ExecutionContext.Implicits.global


class RepositorySpec extends UnitSpec with BeforeAndAfterEach with BeforeAndAfterAll with MongoSpecSupport {

  val mongoComponenet = new ReactiveMongoComponent {
    override def mongoConnector: MongoConnector = mongoConnectorForTest
  }

  val repository = new ProfessionalBodiesRepository(mongoComponenet)
  val organisations = Seq(Organisation("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017"),
    Organisation("Academic and Research Surgery Society of"),
    Organisation("Academic Gaming and Simulation in Education and Training Society for"),
    Organisation("Academic Primary Care Society for"),
    Organisation("Access Consultants National Register of")
  )


  override def beforeEach(): Unit = await(repository.drop)

  override def afterAll(): Unit = await(repository.drop)

  "The repository" should {
    "return All the organisation" in {
      await(repository.bulkInsert(organisations))
      val result = await(repository.findAll())
      result shouldBe(organisations)

    }
  }
}

