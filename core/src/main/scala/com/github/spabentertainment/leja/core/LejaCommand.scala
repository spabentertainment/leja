package com.github.spabentertainment.leja.core

trait LejaCommand {
  def name: String
  def description: String
  def execute(context: LejaContext): Boolean
}
