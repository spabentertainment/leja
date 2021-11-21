# Leja

Leja is a plugin-based, command-line, double-entry accounting system. It uses text files for input. It reads the files, parses it into an AST that is then passed to a plugin associated with the command-line subcommand to get some output i.e. account balances.

Leja is inspired by John Wiegley's [ledger-cli](https://www.ledger-cli.org/).

## Getting Started
To get started with Leja, ensure you have a Java Runtime Environment installed in your computer.

```
$ java -version
openjdk version "1.8.0_312"
OpenJDK Runtime Environment (build 1.8.0_312-bre_2021_10_21_00_49-b00)
OpenJDK 64-Bit Server VM (build 25.312-b00, mixed mode)
```

You would then download [leja-0.1.0.zip](https://github.com/spabentertainment/leja/releases/download/v0.1.0/leja-0.1.0.zip) and extract it to your home directory or any other preffered path. You can optionally add the bin directory of your extracted directory to the PATH environment variable.

You are now set to use leja, but first you would add transactions to a text file in Leja's own textual format. Here's what a sample leja file might look like:

```txt
$ cat mypersonal.leja
2021-11-01 "Opening Balances"
  Assets:MPESA                        3,500.00 KES
  Liabilities:AccountsPayable:Linda  -2,500.00 KES
  Equity:OpeningBalances             -1,000.00 KES
2021-11-01 "SAFARICOM LTD"
  Expenses:Internet                   3,000.00 KES
  Assets:MPESA                       -3,000.00 KES
```

You use the leja command line to see the balance of your accounts.

```sh
$ ./leja-0.1.0/bin/leja mypersonal.leja balance
         500.00 Assets:MPESA
      -2,500.00 Liabilities:AccountsPayable:Linda
      -1,000.00 Equity:OpeningBalances
       3,000.00 Expenses:Internet
---------------
           0.00
```

or the history of transactions:

```sh
$ ./leja-0.1.0/bin/leja mypersonal.leja register
2021-11-01 Opening Balances   Assets:MPESA                 3,500.00 KES
                              Equity:OpeningBalances      -3,500.00 KES
2021-11-01 SAFARICOM LTD      Expenses:Internet            3,000.00 KES
                              Assets:MPESA                -3,000.00 KES
```
