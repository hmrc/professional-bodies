package repositories

import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.mongo.{MongoConnector, MongoSpecSupport}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class DataMigrationRepositorySpec extends WordSpec
                                  with MustMatchers
                                  with ScalaFutures
                                  with MongoSpecSupport
                                  with IntegrationPatience
                                  with BeforeAndAfterEach{

  "countDataMigrations" should {
    "tell me if the repository is empty" in {
      //initially no dataMigration present.
      whenReady(dataMigrationMongoRepository.countDataMigrations()) { res: Int =>
        res must be(0)
      }
    }

    "tell me if the repository is populated" in {
      whenReady(dataMigrationMongoRepository.insert(DataMigration(1, System.currentTimeMillis()))){ res =>
        if (res.ok) {
          whenReady(dataMigrationMongoRepository.countDataMigrations()) { res: Int =>
            res must be(1)
          }
        }
      }
    }
  }

  "insertDataMigration" should {
    val dataMigration = new DataMigration(1, System.currentTimeMillis())
    "insert a dataMigration" in {
      whenReady(dataMigrationMongoRepository.insertDataMigration(dataMigration)){ res =>
        res must be(true)
        whenReady(dataMigrationMongoRepository.findAll()){ res =>
          res must be(Seq(dataMigration))
        }
      }
    }

    "return Future[false] on write failure" in {
      val stubbedRepo = new DataMigrationMongoRepository(mongoComponent){
        override def insertDataMigration(migration: DataMigration): Future[Boolean] = Future.successful(false)
      }
      whenReady(stubbedRepo.insertDataMigration(dataMigration)){ res =>
        res must be(false)
        whenReady(stubbedRepo.findAll()){ res =>
          res must be(Seq.empty)
        }
      }
    }
  }

  val mongoComponent: ReactiveMongoComponent = new ReactiveMongoComponent {override def mongoConnector: MongoConnector = mongoConnectorForTest}
  val dataMigrationMongoRepository: DataMigrationMongoRepository = new DataMigrationMongoRepository(mongoComponent)

  override def beforeEach(): Unit = Await.result(dataMigrationMongoRepository.drop, Duration(100, "millis"))
}
