package com.db4o.test.aliases;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.test.Test;
import com.db4o.test.lib.File4;

public class ClassAliasesTestCase {

	public void testTypeAlias() {
		ObjectContainer container = Test.objectContainer();

		container.set(new Person1("Homer Simpson"));
		container.set(new Person1("John Cleese"));
		
		container = Test.reOpen();
		container.ext().configure().alias(
				// Person1 instances should be read as Person2 objects
				new TypeAlias("com.db4o.test.aliases.Person1",
						"com.db4o.test.aliases.Person2"));
		
		assertData(container);
	}
	
	public void testAliasedTypeIsStoredCorrectly() {
		ObjectContainer container = Test.objectContainer();
		container.ext().configure().alias(
				// Person1 instances should be read as Person2 objects
				new TypeAlias("com.db4o.test.aliases.Person1",
						"com.db4o.test.aliases.Person2"));
		
		container.set(new Person2("Homer Simpson"));
		container.set(new Person2("John Cleese"));
		
		container = Test.reOpen();
		ObjectSet os = container.query(Person1.class);
		//Test.ensureEquals(0, os.size());
		while (os.hasNext()) {
			Object next = os.next();
			System.out.println(next);
			System.out.println(next.getClass());
		}
		assertData(container);

	}
	
	public void testAccessingDotnetFromJava() throws Exception {
		generateDotnetData();
		ObjectContainer container = openDotnetDataFile();
		container.ext().configure().alias(
				new GlobAlias(
						"com.db4o.test.aliases.*, MyAssembly",
						"com.db4o.test.aliases.*"));
//				new TypeAlias(
//						Person2.class.getName() + ", MyAssembly",
//						Person2.class.getName()));
		try {
			assertData(container);
		} finally {
			container.close();
		}
	}
	
	private void assertData(ObjectContainer container) {
		ObjectSet os = container.query(Person2.class);
		
		Test.ensureEquals(2, os.size());
		ensureContains(os, new Person2("Homer Simpson"));
		ensureContains(os, new Person2("John Cleese"));
	}	

	private ObjectContainer openDotnetDataFile() {
		return Db4o.openFile(getDotnetDataFilePath());
	}

	private String getDotnetDataFilePath() {
		return buildTempPath("dotnet.yap");
	}

	private String buildTempPath(String fname) {
		return new File(getTempPath(), fname).getAbsolutePath();
	}

	private String getTempPath() {
		return System.getProperty("java.io.tmpdir");
	}

	private void generateDotnetData() throws IOException {
		new File(getDotnetDataFilePath()).delete();
		String assembly = generateAssembly();
		// XXX: use mono on linux to execute the assembly
		exec(assembly + " " + getDotnetDataFilePath());
	}

	private String generateAssembly() throws IOException {
		String code = "namespace com.db4o.test.aliases {" +
			"class Person2 { string _name; public Person2(string name) { _name = name; }}" +
			"class Program {" +
				"static void Main(string[] args) {" +
					"string fname = args[0];" +
					"using (ObjectContainer container = Db4o.openFile(fname)) {" +
						"container.set(new Person2(\"Homer Simpson\"));" +
						"container.set(new Person2(\"John Cleese\"));" +
					"}" +
					"System.Console.WriteLine(\"success\");" + 
				"}" +
			"}" +
		"}";
		
		String srcFile = buildTempPath("MyAssembly.cs");
		writeFile(srcFile, code);
		String exePath = buildTempPath("MyAssembly.exe");

		new File4(db4odllPath()).copy(buildTempPath("db4o.dll"));
		String cmdLine = "csc /target:exe /r:" + db4odllPath() + " /out:" + exePath + " " + srcFile;
		exec(cmdLine);
		return exePath;
	}

	private String exec(String cmdLine) throws IOException {
		Process p = Runtime.getRuntime().exec(cmdLine);
		return readStdOut(p);
	}

	private String db4odllPath() throws IOException {
		return new File("../db4obuild/dist/dll/net/db4o.dll").getCanonicalPath();
	}

	private void writeFile(String fname, String contents) throws IOException {
		FileWriter writer = new FileWriter(fname);
		try {
			writer.write(contents);
		} finally {
			writer.close();
		}
	}

	private String readStdOut(Process p) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringWriter writer = new StringWriter();
		String line = null;
		while (null != (line = reader.readLine())) {
			writer.write(line);
			writer.write("\n");
		}
		return writer.toString();
	}

	private void ensureContains(ObjectSet actual, Object expected) {
		actual.reset();
		while (actual.hasNext()) {
			Object next = actual.next();
			if (next.equals(expected))
				return;
		}
		Test.ensure(false);
	}
	
	public static void main(String[] args) {
		Test.runSolo(ClassAliasesTestCase.class);
	}

}
