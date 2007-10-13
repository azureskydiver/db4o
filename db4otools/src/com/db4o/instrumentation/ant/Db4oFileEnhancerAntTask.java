/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.ant;

import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;

public class Db4oFileEnhancerAntTask extends Task {
	private String srcDir;
	private String targetDir;
	private final List classPath = new ArrayList();
	private String packagePredicate;
	private final List editFactories = new ArrayList();

	public void add(ClassEditFactory editFactory) {
		editFactories.add(editFactory);
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
			String[] curPaths=path.list();
			for (int curPathIdx = 0; curPathIdx < curPaths.length; curPathIdx++) {
				paths.add(curPaths[curPathIdx]);
			}
		}
		BloatClassEdit clazzEdit = null;
		switch(editFactories.size()) {
			case 0:
				clazzEdit = new NullClassEdit();
				break;
			case 1:
				clazzEdit = ((ClassEditFactory)editFactories.get(0)).createEdit();
				break;
			default:
				List classEdits = new ArrayList(editFactories.size());
				for (Iterator factoryIter = editFactories.iterator(); factoryIter.hasNext(); ) {
					ClassEditFactory curFactory = (ClassEditFactory) factoryIter.next();
					classEdits.add(curFactory.createEdit());
				}
				clazzEdit = new CompositeBloatClassEdit((BloatClassEdit[])classEdits.toArray(new BloatClassEdit[classEdits.size()]), true);
				
		}
		try {
			new Db4oFileEnhancer(clazzEdit).enhance(srcDir,targetDir,(String[])paths.toArray(new String[paths.size()]),(packagePredicate==null ? "" : packagePredicate));
		} catch (Exception exc) {
			throw new BuildException(exc);
		}
	}
}
