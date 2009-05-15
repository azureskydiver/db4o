package com.db4o.eclipse.ui

import com.db4o.eclipse.preferences._

import scala.collection._

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
import org.eclipse.swt.events._

import AndOrEnum.AndOr

object Db4oInstrumentationPropertyPage {
  val REGEXP_TEXT_ID = "regexp.text"
}

class Db4oInstrumentationPropertyPage extends PropertyPage {

  var model: Db4oInstrumentationPropertyPageModel = null
  
  override def createContents(parent: Composite) = {
    noDefaultAndApplyButton
    addControl(parent)
  }

  override def performOk: Boolean = {
    model.store(project) match {
      case StoreStatus(false, msg) => {
        setErrorMessage(msg)
        false
      }
      case _ => true
    }
  }
  
  private def addControl(parent: Composite) = {
    
    model = new Db4oInstrumentationPropertyPageModel(project)
    
    val composite = new Composite(parent, SWT.NULL)
    val layout = new GridLayout
    layout.numColumns = 2
    composite.setLayout(layout)
    composite.setLayoutData(fillGridData)
    composite.setFont(parent.getFont)
    
    createLabel("Regular expression for fully qualified class names to be instrumented", composite, (2,1))
    val filterRegExpText = new Text(composite, SWT.SINGLE | SWT.BORDER)
    filterRegExpText.setLayoutData(fillGridData((2,1), fillBothDimensions))
    filterRegExpText.setText(model.getFilterRegExp.map(_.toString).getOrElse(".*"))
    filterRegExpText.setData(Db4oInstrumentationPropertyPage.REGEXP_TEXT_ID)
    filterRegExpText.addModifyListener(new ModifyListener() {
      override def modifyText(event: ModifyEvent) {
        model.setFilterRegExp(filterRegExpText.getText)
      }
    })
    
    val booleanComposite = new Composite(composite, SWT.NONE)
    booleanComposite.setLayout(new RowLayout)
    val andButton = createRadio("AND", booleanComposite, Some(new RadioListener(AndOrEnum.And)))
    val orButton = createRadio("OR", booleanComposite, Some(new RadioListener(AndOrEnum.Or)))
    (model.getFilterCombinator match {
      case AndOrEnum.Or => orButton
      case _ => andButton
    }).setSelection(true)
    andButton.addListener(SWT.Selection, new RadioListener(AndOrEnum.And))
    andButton.addListener(SWT.Selection, new RadioListener(AndOrEnum.And))
    booleanComposite.setLayoutData(fillGridData((2,1), fillBothDimensions))
    
    createLabel("Packages to be instrumented", composite, (2,1))
    val filterPackageList = createTableViewer(composite, model.getPackages, (1,2))
    model.setSelectionProvider(filterPackageList)
    
    val addButton = createButton("Add", composite)    
    addButton.addListener(SWT.Selection, new Listener() {
      def handleEvent(event: Event) {
        val context = new ProgressMonitorDialog(getShell)
		val scope= SearchEngine.createWorkspaceScope();
		val flags= PackageSelectionDialog.F_SHOW_PARENTS | PackageSelectionDialog.F_HIDE_DEFAULT_PACKAGE | PackageSelectionDialog.F_REMOVE_DUPLICATES
        val dialog = new PackageSelectionDialog(getShell(), context, flags , scope, project, model.getPackages.toArray)
        dialog.setMultipleSelection(true)
        if(dialog.open != Window.OK) {
          return
        }
        val result = dialog.getResult
        val packageNames = result.map(_.asInstanceOf[IPackageFragment].getElementName)
        model.addPackages(immutable.ListSet(packageNames:_*))
      }
    })
    
    val removeButton = createButton("Remove", composite)
    removeButton.setEnabled(false)
    removeButton.addListener(SWT.Selection, new Listener() {
      def handleEvent(event: Event) {
        model.removePackages(selectedPackageNames(filterPackageList))
      }
    })
    model.addPackageSelectionChangeListener(new PackageSelectionChangeListener() {
      override def packagesSelected(state: Boolean) {
        removeButton.setEnabled(state)
      }
    })
    
    composite
  }

  private def allPackageNames: java.util.List[String] = {
    val list = new java.util.ArrayList[String]
    list
  }
  
  private def selectedPackageNames(filterPackageList: TableViewer): Set[String] = {
    val selection = filterPackageList.getSelection
    if(!selection.isInstanceOf[IStructuredSelection]) {
      return immutable.ListSet()
    }
    val structured = selection.asInstanceOf[IStructuredSelection]
    immutable.ListSet(structured.toArray.map(_.asInstanceOf[String]):_*)
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

  def createTableViewer(parent: Composite, packages: Set[String], span: (Int, Int)) = {
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

  private class PackageListContentProvider(view: TableViewer) extends IStructuredContentProvider with PackageListChangeListener {
        
    model.addPackageListChangeListener(this)

    override def getElements(inputElement: Object) = inputElement.asInstanceOf[Set[String]].toArray

    override def dispose() {
      model.removePackageListChangeListener(this)
    }

    override def inputChanged(viewer: Viewer, oldInput: Object, newInput: Object) {
    }

    override def packagesAdded(packageNames: Set[String]) {
      view.add(packageNames.toArray[Object])
    }
    
    override def packagesRemoved(packageNames: Set[String]) {
      view.remove(packageNames.toArray[Object])
    }
  }
  
  private class RadioListener(combinator: AndOr) extends Listener {
    override def handleEvent(event: Event) {
      model.setFilterCombinator(combinator)
    }
  }

}
