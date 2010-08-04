package com.db4o.util;
/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */


import java.io.*;

import com.db4o.util.IOServices.*;


/**
 * This file was taken from the db4oj.tests project from
 * the com.db4o.db4ounit.util package. 
 * TODO: move to own project and supply as separate Jar.
 */


public class JavaServices {

    public static ProcessResult java(String className) throws IOException, InterruptedException{
        return IOServices.exec(javaExecutable(), javaRunArguments(className));
    }
    
    public static ProcessResult java(String className, String[] args) throws IOException, InterruptedException{
        return IOServices.exec(javaExecutable(), javaRunArguments(className, args));
    }

    public static ProcessRunner startJava(String className, String[] args) throws IOException {
        return IOServices.start(javaExecutable(), javaRunArguments(className, args));
    }

    private static String javaExecutable() {
        for (int i = 0; i < vmTypes.length; i++) {
            if(vmTypes[i].identified()){
                return vmTypes[i].executable();
            }
        }
        throw new RuntimeException("VM " + vmName() + " not known. Please add as JavaVM class to end of JavaServices class.");
    }
    
    private static String[] javaRunArguments(String className) {
    	return javaRunArguments(className, new String[0]);
    }

    private static String[] javaRunArguments(String className, String[] args) {
    	String[] allArgs = new String[args.length + 3];
    	allArgs[0] = "-classpath";
    	allArgs[1] = IOServices.joinArgs(
						File.pathSeparator,
						new String[] {
						JavaServices.javaTempPath(), 
						currentClassPath(),
						
				}, DrsRuntime4.runningOnWindows());
        allArgs[2] = className;
        System.arraycopy(args, 0, allArgs, 3, args.length);
        return allArgs;
        
    }

    private static String currentClassPath(){
        return property("java.class.path");
    }
    
    static String javaHome(){
        return property("java.home");
    }
    
    static String vmName(){
        return property("java.vm.name");
    }
    
    static String property(String propertyName){
        return System.getProperty(propertyName);
    }
    
    private static final JavaVM[] vmTypes = new JavaVM[]{
        new SunWindows(),
    };
    
    static interface JavaVM {
        boolean identified();
        String executable();
    }
    
    static class SunWindows implements JavaVM{
        public String executable() {
            return  Path4.combine(Path4.combine(javaHome(), "bin"), "java");
        }
        public boolean identified() {
            return true;
        }
    }
    
	public static String javaTempPath()
	{
		return IOServices.buildTempPath("java");
	}
	
}
