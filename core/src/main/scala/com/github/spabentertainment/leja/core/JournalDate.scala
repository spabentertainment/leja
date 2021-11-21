package com.github.spabentertainment.leja.core

case class JournalDate(
    day: Int,
    month: Int,
    year: Int
) {
  private val separator = '-'

  override def toString(): String = {
    f"${year}%04d${separator}${month}%02d${separator}${day}%02d"
  }
}
