import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "bootstrap-play-25"        % "3.14.0",
    "uk.gov.hmrc"             %% "simple-reactivemongo"     % "7.12.0-play-26"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.4"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"             %% "service-integration-test" % "0.2.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "2.0.0"                 % "test, it",
    "uk.gov.hmrc"             %% "hmrctest"                 % "3.2.0"                 % "test, it",
    "org.mockito"             %  "mockito-core"             % "2.23.4"                % "test, it",
    "uk.gov.hmrc"             %% "reactivemongo-test"       % "4.8.0-play-25"         % "test, it"

  )

}
