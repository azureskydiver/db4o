/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.util;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.*;


/**
 * @sharpen.ignore
 */
public class JavaServices {

    public static String java(String className) throws IOException, InterruptedException{
        return IOServices.exec(javaExecutable(), javaRunArguments(className));
    }
    
	public static String javac(String srcFile) throws IOException, InterruptedException
	{
			String[] javacArgs =
				new String[]{
					"-classpath",
					IOServices.joinArgs(
	        				File.pathSeparator,
	        				new String[]{
	        						currentClassPath(),
	        						db4oCoreJarPath(), 
	        						db4oJarPath("-optional"),
	        						db4oJarPath("-cs"),
	        				}, runningOnWindows()),
	        		"-source", "1.5",
	        		"-target", "1.5",
	        		srcFile};

			return IOServices.exec(WorkspaceServices.javacPath(),javacArgs);
	}
	
	private static void assertJarExists(String path){
		Assert.isTrue(File4.exists(path), "'" + path + "' not found. Make sure the jar was built before running this test.");		
	}


    public static String startAndKillJavaProcess(String className, String expectedOutput, long timeout) throws IOException{
        return IOServices.execAndDestroy(javaExecutable(), javaRunArguments(className), expectedOutput, timeout);
    }

    private static String javaExecutable() {
        for (int i = 0; i < vmTypes.length; i++) {
            if(vmTypes[i].identified()){
                return vmTypes[i].executable();
            }
        }
        throw new NotImplementedException("VM " + vmName() + " not known. Please add as JavaVM class to end of JavaServices class.");
    }
    
    private static String[] javaRunArguments(String className) {
        return new String[] {"-cp",
                IOServices.joinArgs(
						File.pathSeparator,
						new String[] {
						JavaServices.javaTempPath(), 
						currentClassPath(),
						db4oCoreJarPath(), 
						db4oJarPath("-optional"),
						db4oJarPath("-cs")
				}, runningOnWindows())
        		, className};
        
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
        new J9(),
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
    
    static class J9 implements JavaVM{
        public String executable() {
            
            // The following does start J9, but it produces an error:
            // JVMJ9VM011W Unable to load jclfoun10_23: The specified module could not be found. 
            
            return property("com.ibm.oti.vm.exe");
        }
        public boolean identified() {
            return false;
            // return vmName().equals("J9");
        }
        
    }
    
    public static String db4oCoreJarPath()
    {
        return db4oJarPath("-core");
    }

	public static String db4oJarPath(String extension)
	{
		String db4oVersion =  
				Db4oVersion.MAJOR + "." + Db4oVersion.MINOR + "." + 
            Db4oVersion.ITERATION + "." + Db4oVersion.REVISION;
		String distDir = WorkspaceServices.readProperty(WorkspaceServices.machinePropertiesPath(), "dir.dist", true);
		if(distDir == null || distDir.length() == 0)
		{
			distDir = "db4obuild/dist";
		}
		return WorkspaceServices.workspacePath(distDir + "/java/lib/db4o-" + db4oVersion + extension + "-java1.2.jar");
	}
	
	public static String javaTempPath()
	{
		return IOServices.buildTempPath("java");
	}
	
	private static boolean runningOnWindows() {
		String osName = System.getProperty("os.name");
		if(osName == null) {
			return false;
		}
		return osName.indexOf("Win") >= 0;
	}
	
}
