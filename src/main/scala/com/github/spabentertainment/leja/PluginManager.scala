package com.github.spabentertainment.leja

import com.github.spabentertainment.leja.core.LejaPlugin
import java.io.File
import org.clapper.classutil.ClassFinder
import scala.io.Source

class PluginManager(plugginsPaths: List[String]) {
  private val LejaPluginCannonicalName =
    "com.github.spabentertainment.leja.core.LejaPlugin"

  def plugins = {
    val classpath = plugginsPaths.map { new File(_) }
    val finder = ClassFinder(classpath)
    val classes = finder.getClasses()

    val classMap = ClassFinder.classInfoMap(classes)
    val plugins =
      ClassFinder.concreteSubclasses(LejaPluginCannonicalName, classMap)

    val initial: Set[LejaPlugin] = Set()
    plugins.foldLeft(initial) { (plugins, pluginString) =>
      val plugin = Class
        .forName(pluginString.name)
        .newInstance()
        .asInstanceOf[LejaPlugin]

      plugins + plugin
    }
  }
}
