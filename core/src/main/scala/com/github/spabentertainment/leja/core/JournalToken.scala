package com.github.spabentertainment.leja.core

import scala.util.parsing.input.Positional

sealed trait JournalToken extends Positional

case class TIMESTAMP(value: JournalDate) extends JournalToken
case class COMMENT(value: String) extends JournalToken
case class LITERAL(value: String) extends JournalToken
case class NUMBER(value: Double) extends JournalToken
case class IDENTIFIER(value: String) extends JournalToken

case object ASSETS extends JournalToken
case object LIABILITIES extends JournalToken
case object EQUITY extends JournalToken
case object EXPENSES extends JournalToken
case object INCOME extends JournalToken

case object COLON extends JournalToken
case object NEWLINE extends JournalToken
case object WHITESPACE extends JournalToken
