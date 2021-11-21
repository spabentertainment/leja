package com.github.spabentertainment.leja.core

import scala.util.parsing.combinator._

object JournalLexer extends RegexParsers {
  override def skipWhitespace = false
  override val whiteSpace = "[ \t]+".r

  def timestamp: Parser[TIMESTAMP] = positioned {
    val hyphen = "-"
    val year = """\d{4}""".r ^^ { _.toInt }
    val monthAndDay = (
      (("(0(1|3|5|7|8))|(1(0|2))".r <~ hyphen) ~ "(0[1-9])|((1|2)[0-9])|(3[0-1])".r)
        | (("(0(4|6|9))|(11)".r <~ hyphen) ~ "(0[1-9])|((1|2)[0-9])|(30)".r)
        | (("02" <~ hyphen) ~ "(0[1-9])|((1|2)[0-9])".r)
    ) ^^ { case month ~ day => Map("month" -> month.toInt, "day" -> day.toInt) }

    (year <~ hyphen) ~ monthAndDay ^^ {
      case year ~ monthAndDay => {
        val date = JournalDate(
          monthAndDay("day"),
          monthAndDay("month"),
          year
        )

        TIMESTAMP(date)
      }
    }
  }

  def comment: Parser[COMMENT] = positioned {
    ";" ~> "[^\n]+".r ^^ { str => COMMENT(str.trim) }
  }

  def literal: Parser[LITERAL] = positioned {
    """"[^"\n]*"""".r ^^ { str =>
      val content = str.substring(1, str.length - 1)

      LITERAL(content)
    }
  }

  def identifier: Parser[IDENTIFIER] = positioned {
    "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ { str => IDENTIFIER(str) }
  }

  def number: Parser[NUMBER] = positioned {
    """-?(([1-9],)?[0-9])+(\.[0-9]+)?""".r ^^ { str =>
      val strWithoutCommas = str.replace(",", "")

      NUMBER(strWithoutCommas.toDouble)
    }
  }

  def assets = positioned { "Assets" ^^ { _ => ASSETS } }
  def liabilities = positioned { "Liabilities" ^^ { _ => LIABILITIES } }
  def equity = positioned { "Equity" ^^ { _ => EQUITY } }
  def expenses = positioned { "Expenses" ^^ { _ => EXPENSES } }
  def income = positioned { "Income" ^^ { _ => INCOME } }

  def colon = positioned { ":" ^^ { _ => COLON } }
  def newline = positioned { "\n".r ^^ { _ => NEWLINE } }
  def ws = positioned { whiteSpace ^^ { _ => WHITESPACE } }

  def tokens: Parser[List[JournalToken]] = {
    phrase(
      rep(
        timestamp
          | comment
          | literal
          | assets
          | liabilities
          | equity
          | expenses
          | income
          | identifier
          | colon
          | number
          | newline
          | ws
      )
    )
  }

  def apply(str: String): Either[JournalLexerError, List[JournalToken]] = {
    parse(tokens, str) match {
      case Success(result, _) => Right(result)
      case Failure(message, next) => {
        val location = Location(next.pos.line, next.pos.column)
        val error = JournalLexerError(location, message)

        Left(error)
      }
      case Error(message, next) => {
        val location = Location(next.pos.line, next.pos.column)
        val error = JournalLexerError(location, message)

        Left(error)
      }
    }
  }
}
