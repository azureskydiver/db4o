package com.db4o.eclipse.ui

import com.db4o.eclipse.preferences._
import org.eclipse.core.resources._
import org.eclipse.jface.viewers._
import java.util.regex._
import scala.collection._
import AndOrEnum.AndOr


class Db4oInstrumentationPropertyPageModel(project: IProject) {
  private var filterRegExp: String = Db4oPreferences.getFilterRegExp(project).toString
  private var filterPackages: immutable.Set[String] = Db4oPreferences.getPackageList(project)
  private var filterCombinator: AndOr = Db4oPreferences.getFilterCombinator(project)
  private var selectedPackages: List[String] = List()
  
  private var listChangeListeners: immutable.ListSet[PackageListChangeListener] = immutable.ListSet.empty
  private var selectionChangeListeners: immutable.ListSet[PackageSelectionChangeListener] = immutable.ListSet.empty

  def addPackageListChangeListener(listener: PackageListChangeListener) = listChangeListeners += listener
  def removePackageListChangeListener(listener: PackageListChangeListener) = listChangeListeners -= listener
  def addPackageSelectionChangeListener(listener: PackageSelectionChangeListener) = selectionChangeListeners += listener
  def removePackageSelectionChangeListener(listener: PackageSelectionChangeListener) = selectionChangeListeners -= listener

  private object PackageSelectionListener extends ISelectionChangedListener {
	  def selectionChanged(event: SelectionChangedEvent) {
	    val selection = event.getSelection
	    if(!selection.isInstanceOf[IStructuredSelection]) {
	      selectedPackages = List()
	    }
	    val structured = selection.asInstanceOf[IStructuredSelection]
	    selectedPackages = structured.toArray.toList.map(_.toString)
	    selectionChangeListeners.foreach(_.packagesSelected(selectedPackages.isEmpty))
	  }
  }

  def setSelectionProvider(provider: IPostSelectionProvider) {
    provider.addSelectionChangedListener(PackageSelectionListener)
  }
  
  def setFilterRegExp(regExp: String) {
    filterRegExp = regExp
  }
  
  def getFilterRegExp = 
    try {
      Some(Pattern.compile(filterRegExp))
    }
    catch {
      case exc => None
    }

  def addPackages(packageNames: Set[String]) {
    filterPackages ++= packageNames
    listChangeListeners.foreach(_.packagesAdded(packageNames))
  }
  
  def removePackages(packageNames: Set[String]) {
    filterPackages --= packageNames
    listChangeListeners.foreach(_.packagesRemoved(packageNames))
  }

  def getPackages = filterPackages

  def setFilterCombinator(combinator: AndOr) {
    filterCombinator = combinator
  }
  
  def getFilterCombinator = filterCombinator

  def getSelectedPackages = selectedPackages
  
  def store(project: IProject): StoreStatus = {
    val pattern = getFilterRegExp
    if(!pattern.isDefined) {
      return StoreStatus(false, "Invalid regular expression")
    }
    pattern.map(Db4oPreferences.setFilterRegExp(project, _))
    Db4oPreferences.setPackageList(project, filterPackages)
    Db4oPreferences.setFilterCombinator(project, filterCombinator)
    StoreStatus(true, "OK")
  }
  
}

case class StoreStatus(success: Boolean, message: String)

trait PackageListChangeListener {
  def packagesAdded(names: Set[String])
  def packagesRemoved(names: Set[String])
}

trait PackageSelectionChangeListener {
  def packagesSelected(state: Boolean)
}
