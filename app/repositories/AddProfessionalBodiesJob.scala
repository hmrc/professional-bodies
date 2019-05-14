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

import javax.inject.{Inject, Named}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

class AddProfessionalBodiesJob @Inject()(@Named("professionalBodies") professionalBodies: Seq[MongoProfessionalBody],
                                         @Named("runAutomatically") runAutomatically: Boolean,
                                         professionalBodiesRepository: ProfessionalBodiesRepository,
                                         dataMigrationRepository: DataMigrationRepository)(implicit val ec: ExecutionContext) {

  def run(): Future[Boolean] = {
    Logger.debug("Running add professional bodies job")
    dataMigrationRepository.countDataMigrations().flatMap { count =>
      if (count < 1) {
        Logger.debug("Data migration not completed. Adding professional bodies to Mongo.")
        professionalBodiesRepository.insertProfessionalBodies(professionalBodies).flatMap { bool =>
          if (bool) {
            Logger.debug("Professional bodies successfully added to Mongo. Inserting data migration.")
            dataMigrationRepository.insertDataMigration(DataMigration(1, System.currentTimeMillis()))
          }
          else {
            Logger.warn("Bulk insert of professional bodies failed.")
            Future.successful(false)
          }
        }
      } else {
        Logger.debug("Data migration already completed. Professional bodies present in Mongo.")
        Future.successful(false)
      }
    }
  }

  if (runAutomatically) run()
}
