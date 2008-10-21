/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;

import db4ounit.extensions.*;

/**
 * @decaf.ignore
 * @sharpen.ignore
 */
public class CsSchemaUpdateTestCase extends AbstractDb4oTestCase{
	
	public static void main(String[] arguments) {
		new CsSchemaUpdateTestCase().runSolo();
	}
	
	public void test() throws IOException, InterruptedException{
		runForLabel("store");
		runForLabel("update");
		runForLabel("assert");
	}

	private void runForLabel(String label) throws IOException, InterruptedException {
		prepareSource(label);
		System.out.println(JavaServices.javac(targetFileName()));
		System.out.println(JavaServices.java(packageName() + "." + className()));
	}
	
	private void prepareSource(String label) throws IOException{
		FileReader reader = new FileReader(sourceFileName());
		BufferedReader bufferedReader = new BufferedReader(reader);
		String tempBuffer;
		StringBuffer stringBuffer = new StringBuffer();
		while ((tempBuffer = bufferedReader.readLine()) != null) {
			stringBuffer.append(tempBuffer);
			stringBuffer.append("\r\n");
		}
		reader.close();
		processLabel(label, stringBuffer);
		FileWriter writer = new FileWriter(targetFileName());
		writer.write(stringBuffer.toString());
		writer.flush();
		writer.close();
	}

	private void processLabel(String label, StringBuffer stringBuffer) {
		int pos = 0;
		while(pos >= 0){
			int labelPos = stringBuffer.indexOf("//" + label, pos);
			if(! (labelPos >= 0)){
				return;
			}
			replaceStringWithBlank(stringBuffer, labelPos, "/*");
			replaceStringWithBlank(stringBuffer, labelPos, "*/");
			pos = labelPos + 1;
		}
		
	}

	private void replaceStringWithBlank(StringBuffer stringBuffer,
			int labelPos, String commentString) {
		int startCommentPos = stringBuffer.indexOf(commentString, labelPos);
		stringBuffer.replace(startCommentPos, startCommentPos + 2, "\r\n");
	}
	
	private String tempPath(){
		return JavaServices.javaTempPath();
	}
	
	private String fileName(){
		return className() + ".java";
	}
	
	private String className(){
		return "CsSchemaMigrationSourceCode";
	}
	
	private String sourceFileName(){
		return testSourcePath() + packagePath() + fileName();
	}
	
	private String targetFilePath(){
		return tempPath() + packagePath();
	}
	
	private String targetFileName(){
		return targetFilePath() + fileName();
	}

	private String testSourcePath() {
		return WorkspaceServices.workspaceRoot() + "/" + "db4oj.tests/src/";
	}
	
	private String packageName(){
		String className = this.getClass().getName();
		return className.substring(0, className.lastIndexOf("."));
	}
	
	private String packagePath(){
		String dotsToSlashes = packageName().replaceAll("\\.", "/");
		return "/" + dotsToSlashes + "/";
	}
	


}
