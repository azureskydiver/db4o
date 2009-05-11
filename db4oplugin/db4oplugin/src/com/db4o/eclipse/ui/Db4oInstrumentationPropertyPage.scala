package com.db4o.eclipse.ui

import com.db4o.eclipse.preferences._

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

class Db4oInstrumentationPropertyPage extends PropertyPage {

  private var filterRegExpText: Text = null
  private var filterPackageList: TableViewer = null

  override def createContents(parent: Composite) = {
    noDefaultAndApplyButton
    addControl(parent)
  }

  override def performOk = {
    Db4oPreferences.setFilterRegExp(project, filterRegExpText.getText)
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
    val packageLabel = createLabel("Packages to be instrumented", composite, (2,1))
    filterPackageList = createTableViewer(composite, (1,2))
    val addButton = createButton("Add", composite)
    val removeButton = createButton("Remove", composite)
    
    addButton.addListener(SWT.Selection, new Listener() {
      def handleEvent(event: Event) {
        val context = new ProgressMonitorDialog(getShell)
		val scope= SearchEngine.createWorkspaceScope();
		val flags= PackageSelectionDialog.F_SHOW_PARENTS | PackageSelectionDialog.F_HIDE_DEFAULT_PACKAGE | PackageSelectionDialog.F_REMOVE_DUPLICATES;
        val dialog = new PackageSelectionDialog(getShell(), context, flags , scope)
        //val dialog = new ElementListSelectionDialog(getShell, null)
        dialog.open
      }
    })
    
    composite
  }

  private def createLabel(text: String, parent: Composite, span: (Int, Int)) = {
    val label = new Label(parent, SWT.NONE)
    label.setText(text)
    label.setFont(parent.getFont)
    label.setLayoutData(fillGridData(span, fillBothDimensions))
    label
  }

  private def createButton(text: String, parent: Composite) = {
    val button = new Button(parent, SWT.NONE)
    button.setText(text)
    button.setFont(parent.getFont)
    button.setLayoutData(fillGridData((1,1), (GridData.FILL, GridData.BEGINNING)))
    button
  }

  def createTableViewer(parent: Composite, span: (Int, Int)) = {
    val table= new Table(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL)
    table.setFont(parent.getFont())
    table.setLayout(new TableLayout)
    val gridData = fillGridData(span, fillBothDimensions)
    gridData.widthHint = 200
    gridData.heightHint = 100
    table.setLayoutData(gridData)
    val viewer = new TableViewer(table)
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

}
