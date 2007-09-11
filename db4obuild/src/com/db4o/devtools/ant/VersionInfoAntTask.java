package com.db4o.devtools.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;

public class VersionInfoAntTask extends Task {
    
    static final int DB4O_ITERATION_OFFSET = 29;
    
    private Calendar calendar;
    private int major;
	private int minor;
    
    private String path;
    private String revision;
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setMajor(int major){
        this.major = major;
    }
    
    public void setMinor(int minor) {
    	this.minor = minor;
    }
    
    public void setRevision(String revision){
        this.revision = revision;
    }
    
    private int getIteration() {
        if (calendar == null) {
            calendar = Calendar.getInstance(Locale.UK);
        }
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        return (weekOfYear - DB4O_ITERATION_OFFSET);
    }
    
    private void outputJavaVersionInfo(PrintWriter pr) {
        int iteration = getIteration();
        String name = new StringBuffer().append(major).append(".")
                .append(minor).append(".").append(iteration).append(".")
                .append(revision).toString();
        
        pr.println("/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */");
        pr.println();
        pr.println("package com.db4o;");
        pr.println();
        pr.println("/**");
        pr.println("* @exclude");
        pr.println("*/");
        pr.println("public class Db4oVersion {");
        pr.println("    public static final String NAME = \"" + name + "\";");
        pr.println("    public static final int MAJOR = " + major + ";");
        pr.println("    public static final int MINOR = " + minor + ";");
        pr.println("    public static final int ITERATION = " + iteration + ";");
        pr.println("    public static final int REVISION = " + revision + ";");
        pr.println("}");
    }
    
    public void execute() throws BuildException {
        String fileName = "Db4oVersion.java";
        try{
	        File dir = new File(path);
	        File file = new File(dir, fileName);
	        file.delete();
	        FileOutputStream fos = new FileOutputStream(file);
	        PrintWriter pr = new PrintWriter(fos);
	        outputJavaVersionInfo(pr);
	        pr.close();
	        fos.close();
        }catch(Exception e){
            throw new BuildException(e);
        }
    }
}
