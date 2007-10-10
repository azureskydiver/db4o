/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.ant;

import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import com.db4o.instrumentation.*;

public class Db4oFileEnhancerAntTask extends Task {
	private String srcDir;
	private String targetDir;
	private List classPath=new ArrayList();
	private String packagePredicate;
	private String editClassName;

	public void setEditClass(String editClassName) {
		this.editClassName = editClassName;
	}

	public void setSrcdir(String srcDir) {
		this.srcDir=srcDir;
	}
	
	public void setTargetdir(String targetDir) {
		this.targetDir=targetDir;
	}

	public void addClasspath(Path path) {
		classPath.add(path);
	}

	public void setPackagefilter(String packagePredicate) {
		this.packagePredicate=packagePredicate;
	}

	public void execute() {
		List paths=new ArrayList();
		for (Iterator pathIter = classPath.iterator(); pathIter.hasNext();) {
			Path path = (Path) pathIter.next();
			System.err.println(path.size());
			String[] curPaths=path.list();
			for (int curPathIdx = 0; curPathIdx < curPaths.length; curPathIdx++) {
				paths.add(curPaths[curPathIdx]);
			}
		}
		try {
			BloatClassEdit clazzEdit = (BloatClassEdit) Class.forName(editClassName).newInstance();
			new Db4oFileEnhancer(clazzEdit).enhance(srcDir,targetDir,(String[])paths.toArray(new String[paths.size()]),(packagePredicate==null ? "" : packagePredicate));
		} catch (Exception exc) {
			throw new BuildException(exc);
		}
	}
}
