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

class Db4oInstrumentationPropertyPage extends PropertyPage {

  private var filterRegExpText: Text = null

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
    layout.numColumns = 1
    composite.setLayout(layout)
    composite.setLayoutData(fillGridData)
    val font = parent.getFont
    val label = new Label(composite, SWT.NONE)
    label.setText("Regular expression for fully qualified class names to be instrumented")
    label.setFont(font)
    label.setLayoutData(fillGridData)
    filterRegExpText = new Text(composite, SWT.SINGLE | SWT.BORDER)
    filterRegExpText.setLayoutData(fillGridData)
    filterRegExpText.setText(Db4oPreferences.getFilterRegExp(project))
    composite
  }
  
  private def fillGridData = {
    val data = new GridData
    data.verticalAlignment = GridData.FILL
    data.horizontalAlignment = GridData.FILL
    data
  }
  
  private def project = getElement.asInstanceOf[IJavaProject].getProject

}
