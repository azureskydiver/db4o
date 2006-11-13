/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.devtools.ant;

import java.io.File;
import java.util.regex.*;

import org.apache.tools.ant.types.FileSet;


public abstract class AbstractAssemblyInfoTask extends AbstractMultiFileSetTask {
	
	@Override
	protected void workOn(File file) throws Exception {
		IO.writeAll(file, updateAttributes(IO.readAll(file)));
	}

	protected abstract String updateAttributes(String contents);
	
	public FileSet createFileSet() {
		return newFileSet();
	}

	protected String updateAttribute(String contents, String attributeName, String value) {
		Pattern pattern = Pattern.compile(attributeName + "\\((.+)\\)");
		Matcher matcher = pattern.matcher(contents);
		return matcher.replaceFirst(attributeName + "(\"" + value + "\")");
	}	

}
