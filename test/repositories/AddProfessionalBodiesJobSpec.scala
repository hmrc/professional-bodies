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




