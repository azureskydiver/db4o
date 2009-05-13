package com.db4o.eclipse.test.functional

import com.db4o.eclipse.preferences._

import com.db4o.eclipse.test.util._

import org.junit._


class InstrumentSingleClassTestCase extends Db4oPluginTestCaseTrait {

  @Before
  override def setUp {
    super.setUp
    WorkspaceUtilities.setAutoBuilding(true)
  }
  
  @Test
  def classesAreInstrumentedByDefault {
    assertInstrumentSingleClass(true)
  }

  @Test
  def matchingClassIsInstrumented {
    Db4oPreferences.setFilterRegExp(project.getProject, java.util.regex.Pattern.compile("foo\\.Foo"))
    assertInstrumentSingleClass(true)
  }

  @Test
  def nonMatchingClassIsNotInstrumented {
    Db4oPreferences.setFilterRegExp(project.getProject, java.util.regex.Pattern.compile("fooX\\.Foo"))
    assertInstrumentSingleClass(false)
  }

  def assertInstrumentSingleClass(expectInstrumentation: Boolean) {
    project.createCompilationUnit(
      "foo",
      "Foo.java",
      "package foo; public class Foo { private int bar; }"
    )
    project.joinAutoBuild
    assertSingleClassInstrumented("foo.Foo", expectInstrumentation)
  }

}
