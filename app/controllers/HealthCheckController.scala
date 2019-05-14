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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent}
import repositories.{DataMigration, DataMigrationRepository}
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext

@Singleton
class HealthCheckController @Inject()(dataMigrationRepository: DataMigrationRepository)
                                     (implicit val ec: ExecutionContext) extends BaseController {
  def status: Action[AnyContent] = Action.async { implicit req =>
    dataMigrationRepository.countDataMigrations().map { count =>
      if (count > 0) {
        Ok
      } else {
        InternalServerError
      }
    }
  }
}
