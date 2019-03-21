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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json.toJson
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import models.ProfessionalBody
import repositories.ProfessionalBodiesMongoRepository

import scala.concurrent.ExecutionContext

@Singleton
class ProfessionalBodiesController @Inject()(val messagesApi: MessagesApi, repository : ProfessionalBodiesMongoRepository)
                                            (implicit val ec: ExecutionContext)
                                                extends BaseController with I18nSupport {

  def getProfessionalBodies: Action[AnyContent] = Action.async { implicit request =>
    repository.findAllProfessionalBodies().map { organisations =>
      Ok(toJson(organisations))
    }
  }

  def addProfessionalBody(): Action[ProfessionalBody] = Action.async (parse.json[ProfessionalBody]) { request =>
    repository.insertProfessionalBody(request.body).map {
      case false => InternalServerError
      case _ => Ok
    }
  }

  def removeProfessionalBody(): Action[ProfessionalBody] = Action.async(parse.json[ProfessionalBody]) { request =>
    repository.removeProfessionalBody(request.body).map {
      case false => InternalServerError
      case _ => Ok
    }
  }

//  def getAdminOrganisations: Action[AnyContent] = Action.async { implicit request =>
//    repository.fetchOrganisationsAdmin().map { organisations =>
//      Ok(toJson(organisations))
//    }
//  }
}
