/*
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

package uk.gov.hmrc.professionalbodies.repository

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import uk.gov.hmrc.professionalbodies.models.Organisation
import uk.gov.hmrc.professionalbodies.repositories.ProfessionalBodiesRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class RepositorySpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {

  class Scenario(professionalBodies: List[Organisation] = List.empty) {

    var saved: Option[Organisation] = None
    var SavedName: Option[Seq[(String, Json.JsValueWrapper)]] = null

    val repository: ProfessionalBodiesRepository = new ProfessionalBodiesRepository(app.injector.instanceOf[ReactiveMongoComponent],) {

      override def find(query: (String, Json.JsValueWrapper)*)(implicit ec: ExecutionContext): Future[List[Organisation]] = {
        Future.successful(professionalBodies)
      }
      override def insert(entity: Organisation)(implicit ec: ExecutionContext): Future[WriteResult] = {
        saved = Some(entity)
        Future.successful(UpdateWriteResult(ok = true, 1, 1, Seq.empty, Seq.empty, None, None, None))
      }
      override def remove(query: (String, Json.JsValueWrapper)*)(implicit ec: ExecutionContext): Future[WriteResult] = {
        SavedName = Some(query)
        Future.successful(UpdateWriteResult(ok = true, 1, 1, Seq.empty, Seq.empty, None, None, None))
      }
    }
  }

  "the repository" should {
    "post organisation to db" in new Scenario {
      val org = Organisation("foo")
      repository.addOrganisations(org)
      Thread.sleep(10)
      saved.get must be(org)
    }
  }

  "the repository" should {
    "remove organisation from db" in new Scenario {
      var orgName = "foo"
      repository.removeOrganisations(orgName)
      Thread.sleep(10)
      SavedName.get must be(orgName)
    }
  }

}
*/
