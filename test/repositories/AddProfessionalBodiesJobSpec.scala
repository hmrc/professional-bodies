package repositories

import models.ProfessionalBody
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Await

class AddProfessionalBodiesJobSpec extends WordSpec with MustMatchers with ScalaFutures with IntegrationPatience{

  "run" should {
    "should check the write flag" in new SheduledTaskScenario (true) {
      populateTagCollection.dataMigrationDocumentCount must be(0)
    }

    "add professional bodies and set flag" in new SheduledTaskScenario (true) {
      whenReady(scheduledJob.run()) { result =>
        result must be(true)
        professionalBodiesRepository.professionalBodies must be(mongoProfessionalBody)
        populateTagCollection.dataMigrationDocumentCount must be(1)
      }
    }

    "should only have created one document" in new SheduledTaskScenario (true) {
      whenReady(scheduledJob.run()) { result =>
        result must be(true)
        populateTagCollection.dataMigrationDocumentCount must be(1)
        whenReady(scheduledJob.run()) { result =>
          // it didn't run twice
          result must be(false)
          populateTagCollection.dataMigrationDocumentCount must be(1)
        }
      }
    }

    "return false on mongo write failure" in new SheduledTaskScenario (false) {
      whenReady(scheduledJob.run()) { result =>
        result must be(false)
        professionalBodiesRepository.professionalBodies must be(null)
        populateTagCollection.dataMigrationDocumentCount must be(0)
      }
    }
  }
}




