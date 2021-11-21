package com.github.spabentertainment.leja.plugins

import com.github.spabentertainment.leja.core.{
  Account,
  Journal,
  LejaContext,
  LejaCommand,
  LejaPlugin
}

class AccountsPlugin extends LejaPlugin {
  object AccountsCommand extends LejaCommand {
    def name: String = "accounts"
    def description: String = "gets all accounts"

    def getAccounts(journal: Journal): Set[Account] = {
      val initial: Set[Account] = Set()

      journal.transactions
        .foldLeft(initial) { (accounts, transaction) =>
          transaction.posts.foldLeft(accounts) { (accounts, post) =>
            if (accounts(post.account)) accounts else accounts + post.account
          }
        }
    }

    def execute(context: LejaContext): Boolean = {
      getAccounts(context.journal).foreach { println }

      true
    }
  }

  def commands: Set[LejaCommand] = {
    Set(AccountsCommand)
  }
}
