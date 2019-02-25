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

package uk.gov.hmrc.professionalbodies

import play.api.libs.json.Json
import uk.gov.hmrc.professionalbodies.models.{MongoOrganisation, Organisation}

// dirty hack to support Guice app routing tests
object DefaultProfessionalBodies {

  def load: Seq[MongoOrganisation] = {
    val organisations: Seq[Organisation] = Json.parse(getClass.
                                 getResourceAsStream("/json/ApprovedOrganisations.json")).as[Seq[Organisation]]
    organisations.map(organisation => MongoOrganisation(organisation.name))
  }
}
