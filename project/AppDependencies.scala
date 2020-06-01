import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% "govuk-template"           % "5.55.0-play-26",
    "uk.gov.hmrc"             %% "play-ui"                  % "8.10.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.8.0",
    "com.typesafe.play"       %% "play-json-joda"           % "2.7.4",
    "uk.gov.hmrc"             %% "domain"                   % "5.9.0-play-26",
    "uk.gov.hmrc"             %% "play-whitelist-filter"    % "3.4.0-play-26",
    "org.reactivemongo"       %% "play2-reactivemongo"      % "0.18.8-play26"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.8.0" % Test classifier "tests",
    "org.scalatest"           %% "scalatest"                % "3.0.8"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.12.1"                % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.mockito"             %  "mockito-all"              % "1.10.19"               % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.3"                 % "test",
    "org.scalacheck"          %% "scalacheck"               % "1.14.3"                % "test",
    "wolfendale"              %% "scalacheck-gen-regexp"    % "0.1.2"                 % "test",
    "com.github.tomakehurst"  %  "wiremock-standalone"      % "2.25.1"                % "test"
  )

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.5.23"
  val akkaHttpVersion = "10.0.15"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  )
}
