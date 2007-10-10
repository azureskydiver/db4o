/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.util;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;


/**
 * @sharpen.ignore
 */
public class JavaServices {

    public static String java(String className) throws IOException, InterruptedException{
        return IOServices.exec(javaExecutable(), javaRunArguments(className));
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
        return new String[] {"-cp", currentClassPath(), className};
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

}
