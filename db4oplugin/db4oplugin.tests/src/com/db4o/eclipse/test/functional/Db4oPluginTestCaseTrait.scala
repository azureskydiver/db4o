package com.db4o.eclipse.test.functional

import com.db4o.eclipse.nature._
import com.db4o.eclipse.builder._

import com.db4o.eclipse.test.util._

import org.junit._
import org.junit.Assert._

import org.eclipse.jdt.core._

import java.net._

trait Db4oPluginTestCaseTrait {

    val ACTIVATABLE_CLASS = classOf[com.db4o.ta.Activatable]

  val project = new JavaProject("simple_project")
  
  @Before
  def setUp {
    Db4oNature.toggleNature(project.getProject)
    project.buildProject(null)
  }
  
  @After
  def tearDown {
    project.dispose
  }

  protected def assertSingleClassInstrumented(className: String, expectInstrumentation: Boolean) {
    assertSingleClassInstrumented(project.getMainSourceFolder, className, expectInstrumentation)
  }

  protected def assertSingleClassInstrumented(root: IPackageFragmentRoot, className: String, expectInstrumentation: Boolean) {
    val classPathEntry = root.getRawClasspathEntry
    val outputLocation = 
      if(classPathEntry.getOutputLocation != null)
        classPathEntry.getOutputLocation
      else
        project.getBinFolder.getFullPath
    val loaderURLs = Array[URL](PDEUtil.workspaceFile(outputLocation).toURI.toURL)
    val loader = new URLClassLoader(loaderURLs, ACTIVATABLE_CLASS.getClassLoader)
    val clazz = loader.loadClass(className)
    assertEquals(expectInstrumentation, ACTIVATABLE_CLASS.isAssignableFrom(clazz))
  }

}
