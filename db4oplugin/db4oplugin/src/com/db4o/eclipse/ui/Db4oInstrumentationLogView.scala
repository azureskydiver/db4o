package com.db4o.eclipse.ui

import com.db4o.instrumentation.core._
import com.db4o.instrumentation.main._
import com.db4o.instrumentation.file._

import org.eclipse.swt._
import org.eclipse.ui.part._
import org.eclipse.swt.widgets._
import org.eclipse.jface.viewers._

class Db4oInstrumentationLogView extends ViewPart {

  private val MAX_ENTRIES = 100
  private var view: TableViewer = null
  
  private object LogViewListener extends Db4oInstrumentationListener {
    override def notifyProcessed(source: InstrumentationClassSource, status: InstrumentationStatus) {
      Display.getDefault.asyncExec(new Runnable() {
        override def run() {
	      if(view.getTable.getItemCount > MAX_ENTRIES) {
	        view.remove(view.getElementAt(0))
	      }
	      view.add(source + ": " + status)
        }
      })
    }
  }
  
  override def createPartControl(parent: Composite) = {
    val table= new Table(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL)
    table.setFont(parent.getFont())
    table.setLayout(new TableLayout)
    view = new TableViewer(table)
    Db4oPluginActivator.getDefault.addInstrumentationListener(LogViewListener)
  }

  override def setFocus() {
  }

  override def dispose() {
    Db4oPluginActivator.getDefault.removeInstrumentationListener(LogViewListener)
    super.dispose
  }
  
}
