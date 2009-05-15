package com.db4o.eclipse.test.functional

import com.db4o.eclipse.ui._

import org.junit._
import Assert._

import scala.collection._

class Db4oInstrumentationPropertyPageModelTestCase extends Db4oPluginTestCaseTrait {
  
  private var model: Db4oInstrumentationPropertyPageModel = null
  
  @Before
  override def setUp {
    super.setUp
    model = new Db4oInstrumentationPropertyPageModel(project.getProject)
  }

  abstract case class PackageChange(val packageNames: Set[String])    
  case class PackageAdd(override val packageNames: Set[String]) extends PackageChange(packageNames)
  case class PackageRemove(override val packageNames: Set[String]) extends PackageChange(packageNames)
  case class PackageExpectation(change: PackageChange, result: Set[String])

  @Test
  def testPackageListChanges {

    def add(packageNames: String*) = PackageAdd(immutable.ListSet(packageNames:_*))
    def remove(packageNames: String*) = PackageRemove(immutable.ListSet(packageNames:_*))
    def expect(change: PackageChange, result: String*) = PackageExpectation(change, immutable.ListSet(result:_*))
    
    val expectations = Array(
      expect(add("foo", "bar"), "foo", "bar"),
      expect(remove("foo"), "bar"),
      expect(remove("baz"), "bar"),
      expect(add("foo", "baz"), "foo", "bar", "baz"),
      expect(remove("bar", "baz"), "foo")
    )
    
    object MockChangeListener extends PackageListChangeListener {
      private var expIdx = 0
      
      override def packagesAdded(packageNames: Set[String]) {
        expectations(expIdx).change match {
          case PackageAdd(packageNames) => 
          case _ => fail
        }
        expIdx += 1
      }

      override def packagesRemoved(packageNames: Set[String]) {
        expectations(expIdx).change match {
          case PackageRemove(packageNames) => 
          case _ => fail
        }
        expIdx += 1
      }
      
      def validate {
        assertEquals(expectations.length, expIdx)
      }
    }

    model.addPackageListChangeListener(MockChangeListener)
    
    expectations.foreach((expectation) => {
      expectation.change match {
        case PackageAdd(packageNames) => model.addPackages(packageNames)
        case PackageRemove(packageNames) => model.removePackages(packageNames)
      }
      assertEquals(expectation.result, model.getPackages)
    })
    
    MockChangeListener.validate
  }
  
}
