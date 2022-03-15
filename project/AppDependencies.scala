import play.core.PlayVersion
import sbt._

object AppDependencies {

  private val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % "5.20.0",
    "com.typesafe.play"       %% "play-json-joda"             % "2.9.2",
    "uk.gov.hmrc"             %% "domain"                     % "7.0.0-play-28",
    "org.reactivemongo"       %% "play2-reactivemongo"        % "0.20.13-play28",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "3.5.0-play-28"
  )

  private val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.9",
    "org.jsoup"               %  "jsoup"                    % "1.12.1",
    "com.typesafe.play"       %% "play-test"                % PlayVersion.current,
    "org.mockito"             %  "mockito-all"              % "1.10.19",
    "org.pegdown"             %  "pegdown"                  % "1.6.0",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.0.0",
    "org.scalacheck"          %% "scalacheck"               % "1.14.3",
    "wolfendale"              %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.github.tomakehurst"  %  "wiremock-standalone"      % "2.27.2"
  ).map(_ % "it, test")

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.12"
  val akkaHttpVersion = "10.2.3"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion,
    "commons-codec" % "commons-codec" % "1.12"
  )
}
