package com.db4o.devtools.ant;

import java.io.*;

import org.apache.tools.ant.*;

public class VersionInfoAntTask extends Task {
    
    private int major;
    
    private String path;
    private String version;
    private String keyfile;
    
    static final int NET = 0;
    static final int COMPACT = 1;
    static final int JAVA = 2;
    
    private int distribution = NET;
	
    private String[] distributionNames = {
        ".NET",
        ".NET CompactFramework"
    };
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setMajor(int major){
        this.major = major;
    }
    
    public void setVersion(String version){
        this.version = version;
    }
    
    public void setKeyfile(String keyfile){
        this.keyfile = keyfile;
    }
	
    public void setNet(boolean net) {
        if(net){
            distribution = NET;
        }
    }
    
    public void setJava(boolean java){
        if(java){
            distribution = JAVA;
        }
    }
	
	public boolean getJava() {
		return JAVA == distribution;
	}
    
    public void setCompact(boolean compact) {
        if(compact){
            distribution = COMPACT;
        }
    }
    
    public void execute() throws BuildException {
        String fileName = getJava() ? "Db4oVersion.java": "AssemblyInfo.cs";
        try{
	        File dir = new File(path);
	        File file = new File(dir, fileName);
	        file.delete();
	        FileOutputStream fos = new FileOutputStream(file);
	        PrintWriter pr = new PrintWriter(fos);
	        pr.println("/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */");
	        pr.println();
	        if(getJava()){
	            pr.println("package com.db4o;");
                pr.println();
                pr.println("/**");
                pr.println("* @exclude");
                pr.println("*/");
	            pr.println("public class Db4oVersion {");
                pr.println("    public static final String NAME = \"" + version + "\";");
                pr.println("    public static final int MAJOR = " + major + ";");
	            pr.println("}");
	        }else{
		        pr.println("using System.Reflection;");
		        pr.println("using System.Runtime.CompilerServices;");
		        pr.println("[assembly: AssemblyTitle(\"db4o - database for objects\")]");
		        pr.println("[assembly: AssemblyDescription(\"db4o " + version + " " +  distributionNames[distribution] + "\")]");
		        pr.println("[assembly: AssemblyConfiguration(\"" + distributionNames[distribution] + "\")]");
		        pr.println("[assembly: AssemblyCompany(\"db4objects Inc., San Mateo, CA, USA\")]");
		        pr.println("[assembly: AssemblyProduct(\"db4o - database for objects\")]");
		        pr.println("[assembly: AssemblyCopyright(\"db4o 2005\")]");
		        pr.println("[assembly: AssemblyTrademark(\"\")]");
		        pr.println("[assembly: AssemblyCulture(\"\")]	");
		        pr.println("[assembly: AssemblyVersion(\"" + version +  "\")]");
		        pr.println("[assembly: AssemblyDelaySign(false)]");
		        pr.println("[assembly: AssemblyKeyFile(\"" + keyfile   + "\")]");
		        pr.println("[assembly: AssemblyKeyName(\"\")]");
	        }
	        pr.close();
	        fos.close();
        }catch(Exception e){
            throw new BuildException(e);
        }
    }
}
