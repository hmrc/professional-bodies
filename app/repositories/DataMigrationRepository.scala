package repositories

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DataMigrationMongoRepository])
trait DataMigrationRepository {
  def insertDataMigration(migration: DataMigration): Future[Boolean]
  def countDataMigrations(): Future[Int]
}

@Singleton
class DataMigrationMongoRepository @Inject()(mongo: ReactiveMongoComponent)(implicit val ec: ExecutionContext)
  extends ReactiveRepository[DataMigration, BSONObjectID]("dataMigrations", mongo.mongoConnector.db, DataMigration.formatOrganisation, objectIdFormats)
    with DataMigrationRepository {

  override def insertDataMigration(migration: DataMigration): Future[Boolean] = ???

  override def countDataMigrations(): Future[Int] = ???
}
case class DataMigration(version: Int, timestamp: Long, _id: BSONObjectID = BSONObjectID.generate())

object DataMigration {
  implicit val formatOrganisation: OFormat[DataMigration] = Json.format[DataMigration]
}
