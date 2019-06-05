import play.core.PlayVersion.current
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "professional-bodies"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
  .settings(majorVersion := 0)
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)

libraryDependencies ++= Seq(
  "uk.gov.hmrc"             %% "bootstrap-play-25"        % "4.9.0",
  "uk.gov.hmrc"             %% "simple-reactivemongo"     % "7.12.0-play-25",
  "uk.gov.hmrc"             %% "mongo-lock"               % "6.12.0-play-25",

  "org.scalatest"           %% "scalatest"                % "3.0.4"                 % "test",
  "com.typesafe.play"       %% "play-test"                % current                 % "test",
  "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test",
  "uk.gov.hmrc"             %% "service-integration-test" % "0.2.0"                 % "test",
  "org.scalatestplus.play"  %% "scalatestplus-play"       % "2.0.0"                 % "test",
  "org.mockito"             %  "mockito-core"             % "2.23.4"                % "test",
  "uk.gov.hmrc"             %% "reactivemongo-test"       % "4.8.0-play-25"         % "test"
)
