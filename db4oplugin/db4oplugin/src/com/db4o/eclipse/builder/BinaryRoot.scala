package com.db4o.eclipse.builder

import org.eclipse.jdt.core._

class BinaryRoot(classFile: IClassFile) {
  def path = PDEUtil.binaryRoots(javaProject).find(_.isPrefixOf(classFile.getPath)).get  
  def javaProject = classFile.getJavaProject  
}
