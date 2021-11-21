package com.github.spabentertainment.leja.core

import scala.util.parsing.input.Positional

sealed trait JournalAST extends Positional

case class Journal(
    transactions: List[Transaction]
) extends JournalAST

case class Transaction(
    timestamp: JournalDate,
    payee: String,
    narration: Option[String],
    posts: List[Post]
) extends JournalAST

case class Post(
    account: Account,
    amount: Double,
    commodity: Option[Commodity]
) extends JournalAST

case class Account(
    name: String,
    parent: Option[Account]
) extends JournalAST {
  override def toString(): String = {
    parent match {
      case Some(account) => s"${account.toString}:${name}"
      case None          => name
    }
  }
}

case class Commodity(id: String) extends JournalAST {
  override def toString(): String = id
}
