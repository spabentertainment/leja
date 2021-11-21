package com.github.spabentertainment.leja.core

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JournalLexerSpec extends AnyFlatSpec with Matchers {
  "The JournalLexer object" should "lex a simple transaction" in {
    val journalContent = """
      |2020-11-01 "Opening Balances"
      |  Assets:Cash                       2,000.00 KES
      |  Equity:OpeningBalance            -2,000.00 KES
      """.stripMargin.trim

    val expectedResult = Right(
      List(
        TIMESTAMP(JournalDate(1, 11, 2020)),
        WHITESPACE,
        LITERAL("Opening Balances"),
        NEWLINE,
        WHITESPACE,
        ASSETS,
        COLON,
        IDENTIFIER("Cash"),
        WHITESPACE,
        NUMBER(2000.0),
        WHITESPACE,
        IDENTIFIER("KES"),
        NEWLINE,
        WHITESPACE,
        EQUITY,
        COLON,
        IDENTIFIER("OpeningBalance"),
        WHITESPACE,
        NUMBER(-2000.0),
        WHITESPACE,
        IDENTIFIER("KES")
      )
    )

    val actualOutput = JournalLexer(journalContent)

    actualOutput shouldEqual expectedResult
  }
}
