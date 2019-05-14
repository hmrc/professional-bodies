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

package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import play.api.http.Status
import play.api.test.FakeRequest
import repositories.{DataMigration, DataMigrationRepository}
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HealthCheckControllerSpec extends WordSpec with MustMatchers with ScalaFutures {

  implicit val mat: ActorMaterializer = ActorMaterializer()(ActorSystem())

  val req = FakeRequest()

  val repo: DataMigrationRepository = new DataMigrationRepository() {

    var count: Int = 0

    override def countDataMigrations(): Future[Int] = Future.successful(count)

    override def insertDataMigration(migration: DataMigration): Future[Boolean] = {
      count = count + 1
      Future.successful(true)
    }
  }

  val healthController = new HealthCheckController(repo)

  "status" should {

    "return 500 initially" in {
      status(call(healthController.status, req)) must be(Status.INTERNAL_SERVER_ERROR)
    }

    "return Ok when insert is initially" in {
      whenReady(repo.insertDataMigration(DataMigration(1, System.currentTimeMillis()))) { _ =>
        status(call(healthController.status, req)) must be(Status.OK)
      }
    }

  }
}
