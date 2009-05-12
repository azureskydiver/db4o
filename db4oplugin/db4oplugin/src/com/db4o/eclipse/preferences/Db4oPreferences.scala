package com.db4o.eclipse.preferences

import org.eclipse.core.resources._

import scala.collection._

object Db4oPreferences {

  val FILTER_REGEXP_PROPERTY_ID = "filter.regexp" 
  val PACKAGE_LIST_PROPERTY_ID = "filter.packages" 
  val DEFAULT_REGEXP = ".*"
  val PACKAGE_SEP = ","
  val DEFAULT_PACKAGE_LIST = ""
  
  def projectPreferences(project: IProject) =
    new ProjectScope(project).getNode(Db4oPluginActivator.PLUGIN_ID) match {
      case null => None
      case x => Some(x)
    }
  
  def setFilterRegExp(project: IProject, filterRegExp: String) {
    setPreference(project, FILTER_REGEXP_PROPERTY_ID, filterRegExp)
  }

  def getFilterRegExp(project: IProject) =
    getPreference(project, FILTER_REGEXP_PROPERTY_ID, DEFAULT_REGEXP)
  
  def setPackageList(project: IProject, packages: Set[String]) {
    setPreference(project, PACKAGE_LIST_PROPERTY_ID, packages.mkString(PACKAGE_SEP))
  }

  def getPackageList(project: IProject) = {
    var packages = immutable.ListSet.empty[String]
    val packageStr = getPreference(project, PACKAGE_LIST_PROPERTY_ID, DEFAULT_PACKAGE_LIST)
    if(!packageStr.isEmpty) {
      packageStr.split(PACKAGE_SEP).foreach(packages += _)
    }
    packages
  }
  
  def getPreference(project: IProject, key: String, defaultValue: String) = {
    projectPreferences(project).map(_.get(key, defaultValue))
        .getOrElse(defaultValue)
  }  

  private def setPreference(project: IProject, key: String, value: String) {
    projectPreferences(project).map((node) => {
      node.put(key, value);
      node.flush();
    })
  }

}
