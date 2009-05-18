/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */

package com.db4o.eclipse.builder

import org.eclipse.jdt.core._
import org.eclipse.core.resources._
import org.eclipse.core.runtime._
import java.io._

object PDEUtil {
  def binaryRoots(javaProject: IJavaProject) = {
    javaProject.getPackageFragmentRoots.map(outputLocation(_))
  }
  
  def packageFragmentRoot(javaElement: IJavaElement): IPackageFragmentRoot =
    if(javaElement.isInstanceOf[IPackageFragmentRoot])
      javaElement.asInstanceOf[IPackageFragmentRoot]
    else
      packageFragmentRoot(javaElement.getParent)
      
  def outputLocation(root: IPackageFragmentRoot) =
    if(root.getKind != IPackageFragmentRoot.K_SOURCE)
      root.getPath
    else {
      val path = root.getRawClasspathEntry.getOutputLocation
      if (path != null) 
        path
      else
        root.getJavaProject.getOutputLocation
    }
  
  def getWorkspaceRoot = ResourcesPlugin.getWorkspace.getRoot

  def workspacePath(path: IPath) =
    getWorkspaceRoot.getLocation.append(path)

  def workspaceFile(path: IPath) =
    new File(PDEUtil.workspacePath(path).toOSString)

}
