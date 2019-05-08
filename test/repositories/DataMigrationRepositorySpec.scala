package repositories

import models.ProfessionalBody
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.{UpdateWriteResult, WriteConcern, WriteResult}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.MongoConnector

import scala.concurrent.{ExecutionContext, Future}

class DataMigrationRepositorySpec extends WordSpec with MustMatchers with ScalaFutures{

  "countDataMigrations" should {
    "tell me if the repository is populated" in {
      WriteResult must be(0)

    }
  }

  "insertDataMigration" should {
    "tell me if the repository is populated" in {
      WriteResult must be(0)

    }
  }



  class MongoScenario(success: Boolean = true) {
    val mongoComponent: ReactiveMongoComponent = new ReactiveMongoComponent {override def mongoConnector: MongoConnector = mongoConnectorForTest}

    val professionalBody = Seq(
      ProfessionalBody("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017"),
      ProfessionalBody("Academic and Research Surgery Society of"),
      ProfessionalBody("Academic Gaming and Simulation in Education and Training Society for"),
      ProfessionalBody("Academic Primary Care Society for"),
      ProfessionalBody("Access Consultants National Register of")
    )

    val mongoProfessionalBody: Seq[MongoProfessionalBody] = professionalBody.map(organisation => MongoProfessionalBody(organisation.name))
    
  }

}
