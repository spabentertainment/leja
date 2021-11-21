package com.github.spabentertainment.leja.core

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{NoPosition, Position, Reader}

object JournalParser extends Parsers {
  override type Elem = JournalToken

  class JournalTokenReader(tokens: Seq[JournalToken])
      extends Reader[JournalToken] {
    override def first: JournalToken = tokens.head
    override def atEnd: Boolean = tokens.isEmpty
    override def pos: Position = NoPosition
    override def rest: Reader[JournalToken] = new JournalTokenReader(
      tokens.tail
    )
  }

  def apply(tokens: Seq[JournalToken]): Either[JournalParserError, Journal] = {
    val reader = new JournalTokenReader(tokens)

    journal(reader) match {
      case Success(result, next) => Right(result)
      case Failure(msg, next) =>
        Left(JournalParserError(Location(next.pos.line, next.pos.column), msg))
      case Error(msg, next) =>
        Left(JournalParserError(Location(next.pos.line, next.pos.column), msg))
    }
  }

  def journal: Parser[Journal] = positioned {
    repsep(transaction, NEWLINE) ^^ { transactions => Journal(transactions) }
  }

  def transaction: Parser[Transaction] = positioned {
    (opt(comment <~ NEWLINE)) ~>
      (timestamp <~ WHITESPACE) ~
      (literal <~ NEWLINE) ~
      opt(WHITESPACE ~> comment <~ NEWLINE) ~
      repsep(post, NEWLINE) ^^ {
        case TIMESTAMP(date) ~ LITERAL(payee) ~ Some(
              COMMENT(narration)
            ) ~ posts => {
          Transaction(date, payee, Some(narration), posts)
        }
        case TIMESTAMP(date) ~ LITERAL(payee) ~ None ~ posts => {
          Transaction(date, payee, None, posts)
        }
      }
  }

  def post: Parser[Post] = positioned {
    (WHITESPACE ~> account <~ WHITESPACE) ~
      number ~
      opt(WHITESPACE ~> identifier) ^^ {
        case account ~ NUMBER(amount) ~ Some(
              IDENTIFIER(commodityIdentifier)
            ) => {
          val commodity = Some(Commodity(commodityIdentifier))

          Post(account, amount, commodity)
        }
        case account ~ NUMBER(amount) ~ None => {
          Post(account, amount, None)
        }
      }
  }

  def account: Parser[Account] = positioned {
    (ASSETS | LIABILITIES | EQUITY | EXPENSES | INCOME) ~ rep1(
      COLON ~> identifier
    ) ^^ {
      case rootAccount ~ identifiers => {
        val parentAccount = rootAccount match {
          case ASSETS      => Account("Assets", None)
          case LIABILITIES => Account("Liabilities", None)
          case EQUITY      => Account("Equity", None)
          case EXPENSES    => Account("Expenses", None)
          case INCOME      => Account("Income", None)
          case _           => Account("Unknown", None)
        }

        identifiers.foldLeft(parentAccount) { (parentAccount, identifier) =>
          Account(identifier.value, Some(parentAccount))
        }
      }
    }
  }

  private def timestamp: Parser[TIMESTAMP] = positioned {
    accept("timestamp", { case timestamp @ TIMESTAMP(date) => timestamp })
  }

  private def number: Parser[NUMBER] = positioned {
    accept("number", { case number @ NUMBER(value) => number })
  }

  private def identifier: Parser[IDENTIFIER] = positioned {
    accept("identifier", { case id @ IDENTIFIER(name) => id })
  }

  private def literal: Parser[LITERAL] = positioned {
    accept("string literal", { case lit @ LITERAL(name) => lit })
  }

  private def comment: Parser[COMMENT] = positioned {
    accept("comment", { case comment @ COMMENT(str) => comment })
  }
}
