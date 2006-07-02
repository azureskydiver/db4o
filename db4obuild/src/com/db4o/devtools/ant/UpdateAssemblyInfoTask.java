package com.db4o.devtools.ant;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.types.FileSet;

public class UpdateAssemblyInfoTask extends AbstractMultiFileSetTask {

	String _version;
	
	public void setVersion(String version) {
		_version = version;
	}
	
	public FileSet createFileSet() {
		return newFileSet();
	}

	@Override
	protected void workOn(File file) throws Exception {
		String contents = IO.readAll(file);		
		contents = updateAttribute(contents, "AssemblyVersion", _version);
		contents = updateAttribute(contents, "AssemblyProduct", AssemblyInfo.PRODUCT);
		contents = updateAttribute(contents, "AssemblyCompany", AssemblyInfo.COMPANY);
		contents = updateAttribute(contents, "AssemblyCopyright", AssemblyInfo.COPYRIGHT); 
		IO.writeAll(file, contents);
	}

	private String updateAttribute(String contents, String attributeName, String value) {
		Pattern pattern = Pattern.compile(attributeName + "\\((.+)\\)");
		Matcher matcher = pattern.matcher(contents);
		return matcher.replaceFirst(attributeName + "(\"" + value + "\")");
	}	
}
