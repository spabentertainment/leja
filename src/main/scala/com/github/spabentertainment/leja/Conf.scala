package com.github.spabentertainment.leja

import com.github.spabentertainment.leja.core._
import org.rogach.scallop._
import org.rogach.scallop.exceptions._

class Conf(
    arguments: Seq[String],
    commands: Map[Subcommand, LejaCommand]
) extends ScallopConf(arguments) {
  version(s"""
    |${BuildInfo.name} ${BuildInfo.version}, the command-line accounting tool
    |""".trim.stripMargin)

  val filename = trailArg[String]()

  commands.keys.foreach { command => addSubcommand(command) }

  requireSubcommand()

  verify()

  private def customHelp: String = {
    val tabWidth = 4
    val tab = "".padTo(tabWidth, ' ')

    val commandNameAndDescription = commands.values
      .map { command =>
        f"${tab}${command.name}%-10s${tab}${command.description}"
      }
      .mkString("\n")

    val helpText = f"""
      |Usage: ${BuildInfo.name} yourfile.${BuildInfo.name} [command] [args...]
      |
      |where commands include:
      |${commandNameAndDescription}
      |
      |See '${BuildInfo.name} <command> --help' to read about a specific subcommand.
      |""".stripMargin.trim

    helpText
  }

  override def onError(e: Throwable): Unit = {
    e match {
      case Help("") => {
        // main help was called
        println(customHelp)

        Compat.exit(0)
      }
      case Help(command) => {
        // help for subcommand was called
        println(customHelp)
        
        Compat.exit(0)
      }
      case Version => {
        builder.vers.foreach(println)
        Compat.exit(0)
      }
      case ScallopException(message) => {
        // catches all excepitons
        println(message)
        println(customHelp)

        Compat.exit(0)
      }
      case other => throw other
    }
  }
}
