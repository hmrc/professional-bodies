package repositories

import models.ProfessionalBody
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

  class MockDataMigrationRepository (var dataMigrationDocumentCount: Int = 0) extends DataMigrationRepository {
    override def insertDataMigration(migration: DataMigration): Future[Boolean] = {
      dataMigrationDocumentCount += 1
      println(dataMigrationDocumentCount)
      Future.successful(true)
    }
    override def countDataMigrations(): Future[Int] = Future.successful(dataMigrationDocumentCount)
  }

  class MockProfessionalBodiesRepository(insertPBToMongoSuccess: Boolean)
    extends ProfessionalBodiesRepository {
    var professionalBodies: Seq[MongoProfessionalBody] = _
    override def insertProfessionalBodies(professionalBodiesToInsert: Seq[MongoProfessionalBody]): Future[Boolean] = {
      if (insertPBToMongoSuccess) {
        professionalBodies = professionalBodiesToInsert
        println(professionalBodies)
      }
      Future.successful(insertPBToMongoSuccess)
    }
    override def findAllProfessionalBodies(): Future[Seq[ProfessionalBody]] = Future.successful(professionalBodies.map(body => ProfessionalBody.apply(body.name)))
    override def insertProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean] = Future.successful(true)
    override def removeProfessionalBody(professionalBody: ProfessionalBody): Future[Boolean] = Future.successful(true)
  }

  class SheduledTaskScenario (insertPBToMongoSuccess: Boolean){
    val professionalBodies = Seq(
      ProfessionalBody("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017"),
      ProfessionalBody("Academic and Research Surgery Society of"),
      ProfessionalBody("Academic Gaming and Simulation in Education and Training Society for"),
      ProfessionalBody("Academic Primary Care Society for"),
      ProfessionalBody("Access Consultants National Register of")
    )
    val mongoProfessionalBody: Seq[MongoProfessionalBody] = professionalBodies.map(organisation => MongoProfessionalBody(organisation.name))
    val populateTagCollection: MockDataMigrationRepository = new MockDataMigrationRepository()
    val professionalBodiesRepository: MockProfessionalBodiesRepository = new MockProfessionalBodiesRepository(insertPBToMongoSuccess)
    val scheduledJob = new AddProfessionalBodiesJob(mongoProfessionalBody, professionalBodiesRepository, populateTagCollection, false)
  }