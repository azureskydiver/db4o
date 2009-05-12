package com.db4o.eclipse.ui

import com.db4o.eclipse.preferences._

import scala.collection.immutable._

import org.eclipse.swt._
import org.eclipse.swt.graphics._
import org.eclipse.swt.widgets._
import org.eclipse.swt.layout._
import org.eclipse.ui.dialogs._
import org.eclipse.core.runtime._
import org.eclipse.core.resources._
import org.eclipse.core.runtime.preferences._
import org.eclipse.jdt.core._
import org.eclipse.jdt.core.search._
import org.eclipse.jface.viewers._
import org.eclipse.jface.dialogs._
import org.eclipse.jface.window._

import AndOrEnum._

class Db4oInstrumentationPropertyPage extends PropertyPage {

  private var filterRegExpText: Text = null
  private var filterPackages: PackageList = null
  private var filterCombinator: AndOrEnum = null

  override def createContents(parent: Composite) = {
    noDefaultAndApplyButton
    addControl(parent)
  }

  override def performOk = {
    Db4oPreferences.setFilterRegExp(project, filterRegExpText.getText)
    Db4oPreferences.setPackageList(project, filterPackages.getPackages)
    Db4oPreferences.setFilterCombinator(project, filterCombinator)
    true
  }
  
  private def addControl(parent: Composite) = {
    val composite = new Composite(parent, SWT.NULL)
    val layout = new GridLayout
    layout.numColumns = 2
    composite.setLayout(layout)
    composite.setLayoutData(fillGridData)
    composite.setFont(parent.getFont)
    
    val regExpLabel = createLabel("Regular expression for fully qualified class names to be instrumented", composite, (2,1))
    filterRegExpText = new Text(composite, SWT.SINGLE | SWT.BORDER)
    filterRegExpText.setLayoutData(fillGridData((2,1), fillBothDimensions))
    filterRegExpText.setText(Db4oPreferences.getFilterRegExp(project))
    
    filterCombinator = Db4oPreferences.getFilterCombinator(project)
    val booleanComposite = new Composite(composite, SWT.NONE)
    booleanComposite.setLayout(new RowLayout)
    val andButton = createRadio("AND", booleanComposite, Some(new RadioListener(AndOrEnum.And)))
    val orButton = createRadio("OR", booleanComposite, Some(new RadioListener(AndOrEnum.Or)))
    (filterCombinator match {
      case AndOrEnum.Or => orButton
      case _ => andButton
    }).setSelection(true)
    andButton.addListener(SWT.Selection, new RadioListener(AndOrEnum.And))
    andButton.addListener(SWT.Selection, new RadioListener(AndOrEnum.And))
    booleanComposite.setLayoutData(fillGridData((2,1), fillBothDimensions))
    
    val packageLabel = createLabel("Packages to be instrumented", composite, (2,1))
    filterPackages = new PackageList(Db4oPreferences.getPackageList(project))
    val filterPackageList = createTableViewer(composite, filterPackages, (1,2))
    val addButton = createButton("Add", composite)
    val removeButton = createButton("Remove", composite)
    
    addButton.addListener(SWT.Selection, new Listener() {
      def handleEvent(event: Event) {
        val context = new ProgressMonitorDialog(getShell)
		val scope= SearchEngine.createWorkspaceScope();
		val flags= PackageSelectionDialog.F_SHOW_PARENTS | PackageSelectionDialog.F_HIDE_DEFAULT_PACKAGE | PackageSelectionDialog.F_REMOVE_DUPLICATES
        val dialog = new PackageSelectionDialog(getShell(), context, flags , scope, filterPackages.getPackages.toArray)
        dialog.setMultipleSelection(true)
        if(dialog.open != Window.OK) {
          return
        }
        val result = dialog.getResult
        val packageNames = result.map(_.asInstanceOf[IPackageFragment].getElementName)
        packageNames.foreach(filterPackages.addPackage(_))
      }
    })
    
    removeButton.addListener(SWT.Selection, new Listener() {
      def handleEvent(event: Event) {
        selectedPackageNames(filterPackageList).foreach((packageName) => filterPackages.removePackage(packageName))
      }
    })
    
    composite
  }

  private def allPackageNames: java.util.List[String] = {
    val list = new java.util.ArrayList[String]
    list
  }
  
  private def selectedPackageNames(filterPackageList: TableViewer): Array[String] = {
    val selection = filterPackageList.getSelection
    if(!selection.isInstanceOf[IStructuredSelection]) {
      return Array()
    }
    val structured = selection.asInstanceOf[IStructuredSelection]
    structured.toArray.map(_.toString)
  }
  
  private def createLabel(text: String, parent: Composite, span: (Int, Int)) = {
    val label = new Label(parent, SWT.NONE)
    label.setText(text)
    label.setFont(parent.getFont)
    label.setLayoutData(fillGridData(span, fillBothDimensions))
    label
  }

  private def createRadio(text: String, parent: Composite, listener: Option[Listener]): Button = {
    val radio = createSWTButton(text, parent, SWT.RADIO)
    listener.map(radio.addListener(SWT.Selection, _))
    radio
  }

  private def createButton(text: String, parent: Composite): Button = {
    val button = createSWTButton(text, parent, SWT.NONE)
    button.setLayoutData(fillGridData((1,1), (GridData.FILL, GridData.BEGINNING)))
    button
  }
  
  private def createSWTButton(text: String, parent: Composite, flags: Int) = {
    val button = new Button(parent, flags)
    button.setText(text)
    button.setFont(parent.getFont)
    button
  }

  def createTableViewer(parent: Composite, packages: PackageList, span: (Int, Int)) = {
    val table= new Table(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL)
    table.setFont(parent.getFont())
    table.setLayout(new TableLayout)
    val gridData = fillGridData(span, fillBothDimensions)
    gridData.widthHint = 200
    gridData.heightHint = 100
    table.setLayoutData(gridData)
    val viewer = new TableViewer(table)
    viewer.setContentProvider(new PackageListContentProvider(viewer))
    viewer.setInput(packages)
    viewer
  }	

  private def fillGridData(): GridData = fillGridData((1,1), fillBothDimensions)

  private def fillGridData(span: (Int, Int), fill: (Int, Int)) = {
    val data = new GridData
    data.horizontalAlignment = fill._1
    data.verticalAlignment = fill._2
    data.horizontalSpan = span._1
    data.verticalSpan = span._2
    data
  }
  
  private def fillBothDimensions = (GridData.FILL, GridData.FILL)
  
  private def project = getElement.asInstanceOf[IJavaProject].getProject

  private trait PackageListChangeListener {
    def packageAdded(name: String)
    def packageRemoved(name: String)
  }
  
  private class PackageList(var packages: Set[String]) {
    
    private var listeners: ListSet[PackageListChangeListener] = ListSet.empty
    
    def addListener(listener: PackageListChangeListener) = listeners += listener

    def removeListener(listener: PackageListChangeListener) = listeners -= listener

    def addPackage(name: String) {
      packages += name
      listeners.foreach(_.packageAdded(name))
    }

    def removePackage(name: String) {
      packages -= name
      listeners.foreach(_.packageRemoved(name))
    }
    
    def getPackages = packages

  }
  
  private class PackageListContentProvider(view: TableViewer) extends IStructuredContentProvider with PackageListChangeListener {
    
    private var packages: Option[PackageList] = None
    
    override def getElements(inputElement: Object) = inputElement.asInstanceOf[PackageList].getPackages.toArray

    override def dispose() {
      packages.map(_.removeListener(this))
    }

    override def inputChanged(viewer: Viewer, oldInput: Object, newInput: Object) {
      packagesOption(oldInput).map(_.removeListener(this))
      packages = packagesOption(newInput)
      packages.map(_.addListener(this))
    }

    override def packageAdded(name: String) {
      view.add(name)
    }
    
    override def packageRemoved(name: String) {
      view.remove(name)
    }
    
    private def packagesOption(input: Object) =
      input match {
        case null => None
        case p => Some(p.asInstanceOf[PackageList])
      }
  }
  
  private class RadioListener(combinator: AndOrEnum) extends Listener {
    override def handleEvent(event: Event) {
      filterCombinator = combinator
    }
  }

}
