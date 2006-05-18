package com.db4o.devtools.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public final class UpdateAssemblyKeyTask extends Task {

	private String _assemblyInfo;
	private String _keyFile;
	
	private void printAttribute(PrintWriter w, String attribute, String val) {
		w.println("[assembly: " + attribute + "(" + val + ")]");
	}
	
	public void execute() throws BuildException {
		try {
			// open the AssemblyInfo.cs file,
			// and append the key related attributes
			PrintWriter writer = new PrintWriter(
					new FileOutputStream (
							new File(_assemblyInfo), true));
			
			writer.println();
			printAttribute(writer, "AssemblyDelaySign", "false");
			printAttribute(writer, "AssemblyKeyFile", "\"" + _keyFile +"\"");
			printAttribute(writer, "AssemblyKeyName", "\"\"");
			writer.println();
			
			writer.close();
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
	
	public String getAssemblyInfo() {
		return _assemblyInfo;
	}
	
	public void setAssemblyInfo(String assemblyInfo) {
		this._assemblyInfo = assemblyInfo;
	}
	
	public String getKeyFile() {
		return _keyFile;
	}
	
	public void setKeyFile(String keyFile) {
		this._keyFile = keyFile;
	}
	
	
}
