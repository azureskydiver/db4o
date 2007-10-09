/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.util;

import java.io.*;

import com.db4o.foundation.io.*;


/**
 * @sharpen.ignore
 */
public class JavaServices {

    public static String java(String className) throws IOException, InterruptedException{
        return IOServices.exec(javaExecutable(), javaRunArguments(className));
    }

    public static void startAndKillJavaProcess(String className, String expectedOutput, long timeout) throws IOException{
        IOServices.execAndDestroy(javaExecutable(), javaRunArguments(className), expectedOutput, timeout);
    }

    private static String javaExecutable() {
        return  Path4.combine(Path4.combine(javaHome(), "bin"), "java"); 
    }
    
    private static String javaHome(){
        return System.getProperty("java.home");
    }
    
    private static String[] javaRunArguments(String className) {
        return new String[] {"-cp", currentClassPath(), className};
    }
    
    private static String currentClassPath(){
        return System.getProperty("java.class.path");
    }

}
