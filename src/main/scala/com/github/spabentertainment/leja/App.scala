package com.github.spabentertainment.leja

import com.github.spabentertainment.leja.core.{
  LejaContext,
  LejaCommand,
  LejaPlugin,
  Journal,
  JournalError,
  JournalLexer,
  JournalParser
}
import java.io.FileNotFoundException
import org.rogach.scallop.Subcommand
import scala.io.Source

object App {
  def parse(str: String): Either[JournalError, Journal] = {
    JournalLexer(str) match {
      case Left(ex) => Left(ex)
      case Right(tokens) => {
        // Examine the tokens and log warnings for example non-uniform indentation

        JournalParser(tokens) match {
          case Left(ex)       => Left(ex)
          case Right(journal) => Right(journal)
        }
      }
    }
  }

  def lejaCommandToScallopCommand(command: LejaCommand): Subcommand = {
    val scallopCommand = new Subcommand(command.name)

    scallopCommand
  }

  def aggregateCommands(
      plugins: Set[LejaPlugin]
  ): Map[Subcommand, LejaCommand] = {
    val initial: Map[Subcommand, LejaCommand] = Map()

    plugins.foldLeft(initial) { (commandsMap, plugin) =>
      plugin.commands.foldLeft(commandsMap) { (commandsMap, command) =>
        val scallopCommand = lejaCommandToScallopCommand(command)

        commandsMap ++ Map(scallopCommand -> command)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    // TODO: Allow configuration of paths where plugins are placed
    val classPath = System.getProperty("java.class.path").split(":").toList
    val plugginsPaths = classPath
    val plugginManager = new PluginManager(plugginsPaths)
    val commands = aggregateCommands(plugginManager.plugins)

    val conf = new Conf(args.toIndexedSeq, commands)
    val filename = conf.filename()

    try {
      val source = Source.fromFile(filename)

      // Read the file contents
      val fileContent = source.getLines().mkString("\n").trim
      source.close()

      // Parse the file content
      parse(fileContent) match {
        case Left(ex) => println(s"Error: ${ex}")
        case Right(journal) => {
          // Create a context
          val context = LejaContext(journal)

          // Get the command the user wants to execute
          val scallopCommand = conf.subcommands.head.asInstanceOf[Subcommand]

          // Get the LejaCommand mapped to the scallopCommand
          val command = commands(scallopCommand)

          // Execute the command passing our created context
          command.execute(context)
        }
      }
    } catch {
      case ex: FileNotFoundException =>
        println(s"${filename} doesn't exist in the filesystem")
    }
  }
}
