import Dependencies._

ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "com.github.spabentertainment"
ThisBuild / organizationName := "spabentertainment"
ThisBuild / scalacOptions    := List("-deprecation")

lazy val root = (project in file("."))
  .aggregate(core, accountsPlugin, balancePlugin, registerPlugin)
  .dependsOn(core, accountsPlugin, balancePlugin, registerPlugin)
  .enablePlugins(BuildInfoPlugin, JavaAppPackaging)
  .settings(
    name := "leja",

    libraryDependencies ++= Seq(
      classUtil,
      scallop,
      scalaTest % Test
    ),

    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.github.spabentertainment.leja",
  )

lazy val core = (project in file("core"))
  .settings(
    name := "leja-core",
    libraryDependencies ++= Seq(
      scalaParserCombinators,
      scalaTest % Test
    ),
  )

lazy val accountsPlugin = (project in file("plugins/accounts"))
  .dependsOn(core)
  .settings(
    name := "leja-accounts-plugin",
    libraryDependencies += scalaTest % Test
  )

lazy val balancePlugin = (project in file("plugins/balance"))
  .dependsOn(core)
  .settings(
    name := "leja-balance-plugin",
    libraryDependencies += scalaTest % Test
  )

lazy val registerPlugin = (project in file("plugins/register"))
  .dependsOn(core)
  .settings(
    name := "leja-register-lugin",
    libraryDependencies += scalaTest % Test
  )
