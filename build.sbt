import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "maintain-beneficiaries-frontend"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin, SbtSassify)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    DefaultBuildSettings.scalaSettings,
    DefaultBuildSettings.defaultSettings(),
    scalaVersion := "2.13.10",
    SilencerSettings(),
    SbtDistributablesPlugin.publishingSettings,
    inConfig(Test)(testSettings),
    majorVersion := 0,
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "models.{Mode, NormalMode, CheckMode}",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 9793,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController",
    ScoverageKeys.coverageMinimumStmtTotal := 75,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq("-feature"),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    // concatenate js
    Concat.groups := Seq(
      "javascripts/maintainbeneficiariesfrontend-app.js" ->
        group(Seq(
          "javascripts/iebacklink.js",
          "javascripts/print.js",
          "javascripts/maintainbeneficiariesfrontend.js",
          "javascripts/autocomplete.js",
          "javascripts/libraries/location-autocomplete.min.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat,uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("maintainbeneficiariesfrontend-*.js")
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork        := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)

addCommandAlias("scalastyleAll", "all scalastyle test:scalastyle it:scalastyle")
