package com.db4o.eclipse.ui

import com.db4o.eclipse.nature._
import org.eclipse.core.resources._
import org.eclipse.core.runtime	._
import org.eclipse.ui._
import org.eclipse.jface.viewers._
import org.eclipse.jface.action._

class ToggleDb4oNatureAction extends IObjectActionDelegate {

  var selection: ISelection = null

  def run(action: IAction) {
    if (!selection.isInstanceOf[IStructuredSelection]) {
      return
	}
    val selections = selection.asInstanceOf[IStructuredSelection].toArray
    for(selected <- selections) {
      var project = asProject(selected)
      if (project != null) {
        Db4oNature.toggleNature(project)
      }
    }
  }
  
  def asProject(obj: Object): IProject = {
    if (obj.isInstanceOf[IProject]) {
      return obj.asInstanceOf[IProject]
    } 
    if (obj.isInstanceOf[IAdaptable]) {
      return (obj.asInstanceOf[IAdaptable]).getAdapter(classOf[IProject]).asInstanceOf[IProject]
    }
    null
  }
  
  def selectionChanged(action: IAction, selection: ISelection) {
    this.selection = selection
  }

  def setActivePart(action: IAction, targetPart: IWorkbenchPart) {
  }

}
