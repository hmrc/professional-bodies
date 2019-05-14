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
import models.ProfessionalBody
import play.api.libs.json.Json.toJson
import play.api.mvc._
import repositories.ProfessionalBodiesRepository
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext

@Singleton
class ProfessionalBodiesController @Inject()(repository: ProfessionalBodiesRepository)
                                            (implicit val ec: ExecutionContext) extends BaseController {

  def getProfessionalBodies: Action[AnyContent] = Action.async { implicit request =>
    repository.findAllProfessionalBodies().map { organisations =>
      Ok(toJson(organisations))
    }
  }

  def addProfessionalBody(): Action[ProfessionalBody] = Action.async(parse.json[ProfessionalBody]) { request =>
    repository.insertProfessionalBody(request.body).map {
      case false => InternalServerError
      case _ => Created
    }
  }

  def removeProfessionalBody(): Action[ProfessionalBody] = Action.async(parse.json[ProfessionalBody]) { request =>
    repository.removeProfessionalBody(request.body).map {
      case false => InternalServerError
      case _ => Accepted
    }
  }

}
