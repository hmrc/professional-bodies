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

package uk.gov.hmrc.professionalbodies.service

import javax.inject.{Inject, Singleton}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.professionalbodies.models.{MongoOrganisation, Organisation}
import uk.gov.hmrc.professionalbodies.repositories.ProfessionalBodiesRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProfessionalBodiesService @Inject()(repository: ProfessionalBodiesRepository)(implicit ec : ExecutionContext){

  def fetchOrganisations(): Future[Seq[String]] = {
    val organisations: Future[Seq[MongoOrganisation]] = repository.fetchOrganisations()
    organisations.
      map(orgs => orgs.
        map(org => org.name))
  }

  def fetchOrganisationsAdmin: Future[Seq[MongoOrganisation]] = repository fetchOrganisations()

  def addOrganisations(organisation: Organisation): Future[Boolean] = repository addOrganisations organisation

  def removeOrganisations(organisationBSONObjectIDOption: Option[BSONObjectID]): Future[Boolean] = {
    val organisationBSONObjectID = organisationBSONObjectIDOption.orNull
    if (organisationBSONObjectID != null) {
      repository removeOrganisations organisationBSONObjectID
    } else Future.successful(false)
  }

}
