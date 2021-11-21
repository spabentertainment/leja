package com.github.spabentertainment.leja.plugins

import com.github.spabentertainment.leja.core.{
  Account,
  Journal,
  LejaContext,
  LejaCommand,
  LejaPlugin
}

class BalancePlugin extends LejaPlugin {
  object BalanceCommand extends LejaCommand {
    def name: String = "balance"
    def description: String = "gets balances of all accounts"

    def getAccounts(journal: Journal): Set[Account] = {
      val initial: Set[Account] = Set()

      journal.transactions
        .foldLeft(initial) { (accounts, transaction) =>
          transaction.posts.foldLeft(accounts) { (accounts, post) =>
            if (accounts(post.account)) accounts else accounts + post.account
          }
        }
    }

    def getAccountsWithBalances(journal: Journal): Map[Account, Double] = {
      val initial: Map[Account, Double] = Map()

      getAccounts(journal).foldLeft(initial) { (map, account) =>
        val initialBalance = 0.0

        val currentBalance = journal.transactions
          .foldLeft(initialBalance) { (balance, transaction) =>
            transaction.posts
              .filter { post => post.account == account }
              .foldLeft(balance) { (balance, post) => balance + post.amount }
          }

        map + (account -> currentBalance)
      }
    }

    def execute(context: LejaContext): Boolean = {
      val accountsWithBalances = getAccountsWithBalances(context.journal)

      accountsWithBalances
        .map { case account -> balance => f"${balance}%,15.2f ${account}" }
        .foreach { println }

      println("".padTo(15, '-'))

      val totalBalance = accountsWithBalances.values.foldLeft(0.0) {
        (balance, accountBalance) =>
          balance + accountBalance
      }
      println(f"${totalBalance}%,15.2f")

      true
    }
  }

  def commands: Set[LejaCommand] = Set(BalanceCommand)
}
