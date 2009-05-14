package com.db4o.eclipse.test.functional

import com.db4o.eclipse.ui._

import org.eclipse.swt._
import org.eclipse.swt.widgets._

import org.junit._

import Assert._

class PropertyPageTestCase extends Db4oPluginTestCaseTrait {

  var shell: Shell = null
  
  @Before
  override def setUp {
    super.setUp
    shell = new Shell
  }
  
  @After
  override def tearDown {
    shell.dispose
    super.tearDown
  }
  
  @Test
  def testPropertyPage {
    val page = new Db4oInstrumentationPropertyPage()
    page.setElement(project.getJavaProject)
    val parent = new Composite(shell, SWT.NULL)
    assertFalse(findWidget(page.getControl, Db4oInstrumentationPropertyPage.REGEXP_TEXT_ID).isDefined)
    page.createControl(parent)
    assertTrue(findWidget(page.getControl, Db4oInstrumentationPropertyPage.REGEXP_TEXT_ID).isDefined)
    val regExpText = findWidget(page.getControl, Db4oInstrumentationPropertyPage.REGEXP_TEXT_ID).get.asInstanceOf[Text]
    regExpText.setText("\\")
    assertFalse(page.performOk)
    regExpText.setText("foo")
    assertTrue(page.performOk)
  }
  
  private def findWidget(root: Widget, id: String): Option[Widget] = {
    if(root == null) {
      return None
    }
    if(root.isInstanceOf[Composite]) {
      root.asInstanceOf[Composite].getChildren.foreach((child) => {
        val curResult = findWidget(child, id)
        if(curResult.isDefined) {
          return curResult
        }
      })
      return None
    }
    if(id.equals(root.getData)) Some(root) else None
  }
  
}
