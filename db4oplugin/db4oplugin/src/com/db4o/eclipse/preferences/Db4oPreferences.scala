package com.db4o.eclipse.preferences

import org.eclipse.core.resources._

object Db4oPreferences {

  val FILTER_REGEXP_PROPERTY_ID = "filter.regexp" 
  val DEFAULT_REGEXP = ".*"

  def projectPreferences(project: IProject) =
    new ProjectScope(project).getNode(Db4oPluginActivator.PLUGIN_ID) match {
      case null => None
      case x => Some(x)
    }
  
  def setFilterRegExp(project: IProject, filterRegExp: String) {
    projectPreferences(project).map((node) => {
      node.put(FILTER_REGEXP_PROPERTY_ID, filterRegExp);
      node.flush();
    })
  }

  def getFilterRegExp(project: IProject) = {
    projectPreferences(project).map(_.get(FILTER_REGEXP_PROPERTY_ID, DEFAULT_REGEXP))
        .getOrElse(DEFAULT_REGEXP)
  }  
}
