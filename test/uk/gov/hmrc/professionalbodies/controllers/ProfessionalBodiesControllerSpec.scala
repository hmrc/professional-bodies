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

import akka.stream.Materializer
import akka.util.ByteString
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.Matchers
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.libs.json.Json
import play.api.libs.streams.Accumulator
import play.api.test.FakeRequest
import play.api.{Configuration, Environment, mvc}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.professionalbodies.models.Organisation
import uk.gov.hmrc.professionalbodies.service.ProfessionalBodiesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ProfessionalBodiesControllerSpec extends UnitSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar {

  val mockService: ProfessionalBodiesService = mock[ProfessionalBodiesService]
  val fakeRequest = FakeRequest("GET", "/")
  val env: Environment = Environment.simple()
  val configuration: Configuration = Configuration.load(env)
  val messageApi = new DefaultMessagesApi(env, configuration, new DefaultLangs(configuration))
  val controller = new ProfessionalBodiesController(messageApi, mockService)
  implicit val mat: Materializer = app.materializer

  val organisations = Seq("AABC Register Ltd (Architects accredited in building conservation),from year 2016 to 2017",
    "Academic and Research Surgery Society of",
    "Academic Gaming and Simulation in Education and Training Society for",
    "Academic Primary Care Society for",
    "Access Consultants National Register of")


  def theServiceWillReturnSomeOrganisations(): OngoingStubbing[Future[Seq[String]]] = {
    when(mockService.fetchOrganisations()).thenReturn(Future.successful(organisations))
  }

  def theServiceWillReturnBoolean(organisation: Organisation, boolean: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockService.addOrganisations(organisation)).thenReturn(Future.successful(boolean))
  }

  "GET /" should {
    "return 200" in {
      theServiceWillReturnSomeOrganisations()
      val result = controller.getOrganisations()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the list of organisations" in {
      theServiceWillReturnSomeOrganisations()
      val result = await(controller.getOrganisations()(fakeRequest))
      jsonBodyOf(result) shouldBe Json.toJson(organisations)
    }
  }

  "POST /addOrganisation" should {

    "return bad request given well-formed JSON in unexpected format" in {
      val organisation = Organisation("bar")
      theServiceWillReturnBoolean(organisation, boolean = false)
      val result: Accumulator[ByteString, mvc.Result] = controller.addOrganisation()(FakeRequest().withJsonBody(Json.parse(
        """
          |{"foo":"bar"}
        """.stripMargin)))
      val runResult: Int = extractAwait(result.run.flatMap(res => status(res)))
      runResult shouldBe Status.UNSUPPORTED_MEDIA_TYPE
    }
  }
}
