package com.db4o.eclipse.test.functional

import com.db4o.eclipse.nature._
import com.db4o.eclipse.preferences._
import com.db4o.eclipse.builder._

import com.db4o.eclipse.test.util._

import org.junit._
import org.junit.Assert._

import org.eclipse.core.resources._
import org.eclipse.jdt.core._

import java.net._


class BuilderTestCase {

  val ACTIVATABLE_CLASS = classOf[com.db4o.ta.Activatable]

  val project = new JavaProject("simple_project")
  
  @Before
  def setUp {
    WorkspaceUtilities.setAutoBuilding(true)
    Db4oNature.toggleNature(project.getProject)
    project.buildProject(null)
  }
  
  @After
  def tearDown {
    project.dispose
  }
  
  @Test
  def classesAreInstrumentedByDefault {
    assertInstrumentSingleClass(true)
  }

  @Test
  def matchingClassIsInstrumented {
    Db4oPreferences.setFilterRegExp(project.getProject, "foo.Foo")
    assertInstrumentSingleClass(true)
  }

  @Test
  def nonMatchingClassIsNotInstrumented {
    Db4oPreferences.setFilterRegExp(project.getProject, "fooX.Foo")
    assertInstrumentSingleClass(false)
  }

  def assertInstrumentSingleClass(expectInstrumentation: Boolean) {
    project.createCompilationUnit(
      "foo",
      "Foo.java",
      "package foo; public class Foo { private int bar; }"
    )
    assertSingleClassInstrumented("foo.Foo", expectInstrumentation)
  }

  def assertSingleClassInstrumented(className: String, expectInstrumentation: Boolean) {
    project.joinAutoBuild
    val loaderURLs = Array[URL](PDEUtil.workspaceFile(project.getBinFolder.getFullPath).toURI.toURL)
    val loader = new URLClassLoader(loaderURLs, ACTIVATABLE_CLASS.getClassLoader)
    val clazz = loader.loadClass(className)
    assertEquals(expectInstrumentation, ACTIVATABLE_CLASS.isAssignableFrom(clazz))
  }

}
