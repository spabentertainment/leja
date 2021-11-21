package com.github.spabentertainment.leja.core

trait LejaPlugin {
  def commands: Set[LejaCommand]
}
