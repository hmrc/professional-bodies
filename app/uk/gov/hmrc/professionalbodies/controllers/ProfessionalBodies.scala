/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.Future

@Singleton
class ProfessionalBodies @Inject()(val messagesApi: MessagesApi) extends BaseController with I18nSupport {

  def getOrganisations = Action.async { implicit request =>

    val organisations = Seq(
      "AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017",
      "Academic and Research Surgery Society of",
      "Academic Gaming and Simulation in Education and Training Society for",
      "Academic Primary Care Society for",
      "Access Consultants National Register of",
      "Accident and Emergency Medicine British Association for",
      "Accountancy Association of Lecturers in",
      "Accountants and Auditors British Association of",
      "Accountants Association of International",
      "Accountants Natal Society of",
      "Accountants South African Institute of Professional (SAIPA)",
      "Accountants Transvaal Society of",
      "Accounting Association American",
      "Accounting Association British (Standing Committee for British Accounting Association)",
      "Accounting Association European",
      "Accounting Conference of Professors of",
      "Accounting Technicians Association of",
      "Accounting Technicians in Ireland Institute of",
      "Acoustical Society British",
      "Acoustical Society of America",
      "Acoustics Institute of",
      "Acquisition of Latin American Library Materials Seminar on the (SALALM)",
      "Actuarial Profession The (name for the Institute and Faculty of Actuaries)",
      "Back Pain Research Society for",
      "Bacteriologists Society of American (J - Applied Microbiology)",
      "Bakers Institute of British",
      "Radiation Biology European Society for",
      "Zoological Society of London (J - Proceedings)",
      "Zoological Society of Scotland Royal"
    )

    Future.successful(Ok(Json.toJson(organisations)).withHeaders(CONTENT_TYPE -> JSON))


  }

}
