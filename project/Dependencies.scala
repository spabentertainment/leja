import sbt._

object Dependencies {
  lazy val classUtilVersion = "1.5.0"
  lazy val scalaParserCombinatorsVersion = "2.1.0"
  lazy val scalaTestVersion = "3.2.9"
  lazy val scallopVersion = "4.1.0"

  lazy val classUtil = "org.clapper" %% "classutil" % classUtilVersion
  lazy val scalaParserCombinators = "org.scala-lang.modules" %% "scala-parser-combinators" % scalaParserCombinatorsVersion
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion
  lazy val scallop = "org.rogach" %% "scallop" % scallopVersion
}
