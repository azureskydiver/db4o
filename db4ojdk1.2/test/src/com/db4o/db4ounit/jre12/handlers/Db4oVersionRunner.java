/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.handlers;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import com.db4o.db4ounit.common.handlers.*;
import com.db4o.db4ounit.util.*;

import db4ounit.*;

public class Db4oVersionRunner implements TestCase {

    public static void main(String[] args) throws Exception {
        runDatabaseCreator(IntHandlerUpdateTestCase.class.getName());
    }

    private static String DB4OTESTS_BIN_PATH = System.getProperty(
            "db4oj.tests.bin", "../db4oj.tests/bin");

    private static String DB4OARCHIVES_PATH = System.getProperty(
            "db4o.archives.path", "../db4o.archives/java1.2/");

    private static String[] DB4OLIBS = { "db4o-3.0.jar",
            "db4o-4.0-java1.1.jar", "db4o-4.6-java1.2.jar",
            "db4o-5.0-java1.2.jar", "db4o-5.3-java1.2.jar",
            "db4o-5.4-java1.2.jar", "db4o-5.5-java1.2.jar",
            "db4o-5.6-java1.2.jar", "db4o-5.7-java1.2.jar",
            "db4o-6.0-java1.2.jar", "db4o-6.1-java1.2.jar",
            "db4o-6.3-java1.2.jar", };

    private static String[] prefixes = { "com.db4o" };

    public static void runDatabaseCreator(String className) throws Exception {
        //Should use canonical file. Maybe a defect of JDK 1.2 URLClassLoader
        File testBin = new File(DB4OTESTS_BIN_PATH).getCanonicalFile();
        
        for (int i = 0; i < DB4OLIBS.length; i++) {
            URL[] urls = new URL[] { testBin.toURL(),
                    new File(DB4OARCHIVES_PATH + DB4OLIBS[i]).toURL() };
            ClassLoader loader = new VersionClassLoader(urls, prefixes);
            Class clazz = loader.loadClass(className);
            Object obj = clazz.newInstance();
            Method method = clazz.getMethod("createDatabase", new Class[] {});
            method.invoke(obj, new Object[] {});
        }
    }
}
