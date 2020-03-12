import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "govuk-template"           % "5.52.0-play-26",
    "uk.gov.hmrc"             %% "play-ui"                  % "8.8.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.5.0",
    "com.typesafe.play"       %% "play-json-joda"           % "2.7.4",
    "uk.gov.hmrc"             %% "domain"                   % "5.6.0-play-26",
    "uk.gov.hmrc"             %% "play-whitelist-filter"    % "2.0.0",
    "org.reactivemongo"       %% "play2-reactivemongo"      % "0.18.8-play26"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.5.0" % Test classifier "tests",
    "org.scalatest"           %% "scalatest"                % "3.0.8"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.mockito"             %  "mockito-all"              % "1.10.19"               % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.2"                 % "test, it"
  )

}
