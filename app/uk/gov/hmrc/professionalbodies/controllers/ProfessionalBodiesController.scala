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

package uk.gov.hmrc.professionalbodies.controllers

import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json.toJson
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.professionalbodies.models.Organisation
import uk.gov.hmrc.professionalbodies.repositories.ProfessionalBodiesRepository

import scala.concurrent.ExecutionContext

@Singleton
class ProfessionalBodiesController @Inject()(val messagesApi: MessagesApi, repository : ProfessionalBodiesRepository)
                                            (implicit val ec: ExecutionContext)
                                                extends BaseController with I18nSupport {

  def getOrganisations: Action[AnyContent] = Action.async { implicit request =>
    repository.fetchOrganisations().map { organisations =>
      Ok(toJson(organisations))
    }
  }

  def addOrganisation(): Action[Organisation] = Action.async (parse.json[Organisation]) { request =>
    repository.addOrganisation(request.body).map {
      case false => InternalServerError
      case _ => Ok
    }
  }

  def removeOrganisation(): Action[Organisation] = Action.async(parse.json[Organisation]) { request =>
    repository.removeOrganisation(request.body).map {
      case false => InternalServerError
      case _ => Ok
    }
  }

  def getAdminOrganisations: Action[AnyContent] = Action.async { implicit request =>
    repository.fetchOrganisationsAdmin().map { organisations =>
      Ok(toJson(organisations))
    }
  }
}
