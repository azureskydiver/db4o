package com.db4o.eclipse.builder

import com.db4o.eclipse.preferences._

import com.db4o.instrumentation.main._
import com.db4o.ta.instrumentation._
import com.db4o.instrumentation.core._
import com.db4o.instrumentation.classfilter._
import com.db4o.instrumentation.file._
import EDU.purdue.cs.bloat.file._
import com.db4o.instrumentation.util._

import org.eclipse.core.resources._
import org.eclipse.core.runtime._
import org.eclipse.jdt.core._

import scala.collection._
import scala.collection.immutable._

import java.io._
import java.util.regex._

object Db4oInstrumentationBuilder {
  val BUILDER_ID = "db4oplugin.db4oBuilder"
}

class Db4oInstrumentationBuilder extends IncrementalProjectBuilder {

  override def build(kind: Int, args: java.util.Map[_,_], monitor: IProgressMonitor): Array[IProject] = {
	if (kind == IncrementalProjectBuilder.FULL_BUILD) {
	  fullBuild(monitor)
      return null
	} 
    val delta = getDelta(getProject)
	if (delta == null) {
	  fullBuild(monitor)
      return null
	}
    incrementalBuild(delta, monitor)
	null
  }

  private def fullBuild(monitor: IProgressMonitor) {
    getProject.accept(new InstrumentationFullBuildVisitor)
  }

  private def incrementalBuild(delta: IResourceDelta, monitor: IProgressMonitor) {
    val visitor = new ModifiedClassFileCollectorVisitor
    delta.accept(visitor)
    val classSources = visitor.getClassSources
    val classPathRoots = collectClassPathRoots(classSources).map(_.toOSString)

    val partitioned = partitionBy(classSources, (source: SelectionClassSource) => source.binaryRoot)
    val classPathRootsArr = classPathRoots.toList.toArray
    partitioned.keys.foreach((binaryRoot) => {
      val instrumentor = new Db4oFileInstrumentor(new InjectTransparentActivationEdit(new RegExpFilter(binaryRoot.javaProject.getProject)))
      val instrumentationRoot = new SelectionFilePathRoot(classPathRoots, partitioned.get(binaryRoot).get.toList.removeDuplicates)
      try {
        instrumentor.enhance(new BundleClassSource, instrumentationRoot, PDEUtil.workspacePath(binaryRoot.path).toOSString, classPathRootsArr, getClass.getClassLoader)
      }
      catch {
        case e => e.printStackTrace
      }
    })
  }

  private def partitionBy[T,K](iterable: Iterable[T], selector: ((T) => K)) = {
    val agg = HashMap[K, Iterable[T]]().withDefaultValue(HashSet[T]())
    iterable.foldLeft(agg)((map, t) => {
      val key = selector(t)
      map + ((key, map.get(key).getOrElse(HashSet[T]()) ++ List(t)))
    })
  }
  
  private def collectClassPathRoots(classFiles: Iterable[SelectionClassSource]) = {
    classFiles.foldLeft(scala.collection.mutable.HashSet[IPath]())((roots, classSource) => {
        val javaProject = classSource.binaryRoot.javaProject
        roots ++ (PDEUtil.binaryRoots(javaProject).map(PDEUtil.workspacePath(_)))
    })
  }
 
  private class BundleClassSource extends ClassSource {
    def loadClass(name: String) = Db4oPluginActivator.getDefault.getBundle.loadClass(name)
  }
  
  private class InstrumentationFullBuildVisitor extends IResourceVisitor {
    override def visit(resource: IResource) = true
  }
    
  private class SelectionFilePathRoot(roots: Iterable[String], sources: Iterable[_ <: InstrumentationClassSource]) extends FilePathRoot {
	override def rootDirs = roots.toList.toArray
	override def iterator = java.util.Arrays.asList(sources.toList.toArray: _*).iterator
    override def toString = roots.toString + ": " + sources.toString
  }
  
  private class RegExpFilter(project: IProject) extends ClassFilter {

    override def accept(clazz: Class[_]) = {
      !BloatUtil.isPlatformClassName(clazz.getName()) && {
        val regExp = Db4oPreferences.getFilterRegExp(project)
        Pattern.compile(regExp).matcher(clazz.getName).matches
      }
    }
  }
}
