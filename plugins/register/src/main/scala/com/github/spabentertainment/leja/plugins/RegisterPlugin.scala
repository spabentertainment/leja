package com.github.spabentertainment.leja.plugins

import com.github.spabentertainment.leja.core.{
  LejaContext,
  LejaCommand,
  LejaPlugin,
  Transaction
}

class RegisterPlugin extends LejaPlugin {
  object RegisterCommand extends LejaCommand {
    def name: String = "register"
    def description: String = "gets a history of transactions"

    def execute(context: LejaContext): Boolean = {
      // TODO: Get this from the context
      val accountFilterTerm = ""

      context.journal.transactions
        .map { transaction =>
          val filteredPosts = transaction.posts.filter { post =>
            post.account.toString.contains(accountFilterTerm)
          }

          Transaction(
            transaction.timestamp,
            transaction.payee,
            transaction.narration,
            filteredPosts
          )
        }
        .filter { transaction =>
          !transaction.posts.isEmpty
        }
        .map { transaction =>
          val firstSegment =
            f"${transaction.timestamp} ${transaction.payee}%-25s"
          val indentation = "".padTo(
            10 /* for timestamp */ + 2 /* for space between */ + 25 /* for payee */,
            ' '
          )

          val lastSegment = transaction.posts
            .map { post =>
              val commoditySegment = post.commodity match {
                case Some(commodity) => commodity
                case None            => ""
              }

              f"${indentation}${post.account}%-40s ${post.amount}%,10.2f ${commoditySegment}"
            }
            .mkString("\n")
            .trim

          f"${firstSegment} ${lastSegment}".trim
        }
        .foreach(println)

      true
    }
  }

  def commands: Set[LejaCommand] = {
    Set(RegisterCommand)
  }
}
