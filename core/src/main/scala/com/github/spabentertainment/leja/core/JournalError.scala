package com.github.spabentertainment.leja.core

sealed trait JournalError

case class JournalLexerError(location: Location, msg: String)
    extends JournalError
case class JournalParserError(location: Location, msg: String)
    extends JournalError

case class Location(line: Int, column: Int) {
  override def toString = s"$line:$column"
}
