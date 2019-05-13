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

import com.google.inject.{AbstractModule, Provides}
import javax.inject.{Named, Singleton}
import repositories.{AddProfessionalBodiesJob, MongoProfessionalBody}

class Module extends AbstractModule {

  @Provides @Singleton @Named("professionalBodies")
  def professionalBodies(): Seq[MongoProfessionalBody] = {
    DefaultProfessionalBodies.load.map(organisation => MongoProfessionalBody.apply(organisation.name))
  }

  @Provides @Singleton @Named("runAutomatically")
  def runAutomatically(): Boolean = true

  override def configure(): Unit = bind(classOf[AddProfessionalBodiesJob]).asEagerSingleton()
}