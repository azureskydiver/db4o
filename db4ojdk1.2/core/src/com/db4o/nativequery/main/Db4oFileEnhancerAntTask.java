package com.db4o.nativequery.main;

import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class Db4oFileEnhancerAntTask extends Task {
	private String srcDir;
	private String targetDir;
	private List classPath=new ArrayList();
	private String packagePredicate;
	
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
		for (Iterator pathIter = paths.iterator(); pathIter.hasNext();) {
			Path path = (Path) pathIter.next();
			String[] curPaths=path.list();
			for (int curPathIdx = 0; curPathIdx < curPaths.length; curPathIdx++) {
				paths.add(curPaths[curPathIdx]);
			}
		}
		try {
			new Db4oFileEnhancer().enhance(srcDir,targetDir,(String[])paths.toArray(new String[paths.size()]),(packagePredicate==null ? "" : packagePredicate));
		} catch (Exception exc) {
			throw new BuildException(exc);
		}
	}
}
