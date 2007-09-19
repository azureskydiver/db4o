/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.handlers;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.db4o.db4ounit.common.handlers.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class Db4oVersionRunner extends Db4oTestSuite {

    private static String DB4OTESTS_BIN_PATH = System.getProperty(
            "db4oj.tests.bin", "../db4oj.tests/bin");

    private static String DB4OARCHIVES_PATH = System.getProperty(
            "db4o.archives.path", "../db4o.archives/java1.2/");

    private static String[] prefixes = { "com.db4o" };

    private File testBinPath = new File(DB4OTESTS_BIN_PATH);

    private File archivePath = new File(DB4OARCHIVES_PATH);

    private Class testCase;

    private String db4oJarFile;
    
    private Map cache = new HashMap();

    public static void main(String[] args) throws Exception {
        new Db4oVersionRunner().runSolo();
    }

    protected Class[] testCases() {
        return new Class[] { 
			DoubleHandlerUpdateTestCase.class,
			FloatHandlerUpdateTestCase.class,
            IntHandlerUpdateTestCase.class, 
            LongHandlerUpdateTestCase.class,
            ShortHandlerUpdateTestCase.class,
            StringHandlerUpdateTestCase.class,
            };
    }

    public Db4oVersionRunner() {
        this(null, null);
    }

    public Db4oVersionRunner(String db4oJar, Class test) {
        this.db4oJarFile = db4oJar;
        this.testCase = test;

        try {
            // if possible, use the canonical file.
            testBinPath = new File(DB4OTESTS_BIN_PATH).getCanonicalFile();
            archivePath = new File(DB4OARCHIVES_PATH).getCanonicalFile();
        } catch (IOException e) {
            //
        }
    }

    public int runSolo() {
        if (testCase != null) {
            return runSingleUpdateTest(testCase);
        } 
        Class[] testCases = testCases();
        for (int i = 0; i < testCases.length; i++) {
            if (FormatMigrationTestCaseBase.class
                    .isAssignableFrom(testCases[i])) {
                int failures = runSingleUpdateTest(testCases[i]);
                if (failures != 0) {
                    return failures;
                }
            }
        }
        return 0;
    }

    private int runSingleUpdateTest(Class test) {
        try {
            if (db4oJarFile != null) {
                File file = new File(archivePath, db4oJarFile);
                if (!file.exists()) {
                    throw new Db4oException("File not found: " + db4oJarFile);
                }
                file = file.getCanonicalFile();
                createDatabase(file, test);
                String version = getDb4oVersion(file.toURL());
                return run(new String[] { version }, test);
            } 
            File[] files = getDb4oJarFiles();
            for (int i = 0; i < files.length; i++) {
                createDatabase(files[i], test);
            }
            return run(getDb4oVersions(), test);
            
        } catch (Exception e) {
            throw new Db4oException(e);
        }
    }

    private void createDatabase(File file, Class test) throws Exception {
        ClassLoader loader = getVersionClassLoader(file.toURL());
        Class clazz = loader.loadClass(test.getName());
        Object obj = clazz.newInstance();
        Method method = clazz.getMethod("createDatabase", new Class[] {});
        method.invoke(obj, new Object[] {});
    }

    private int run(String[] versions, Class test) throws Exception {
        Field field = test.getField("db4oVersions");
        field.set(null, versions);
        return new TestRunner(test).run();
    }

    private File[] getDb4oJarFiles() {
        File[] files = archivePath.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        });
        return files;
    }

    private String[] getDb4oVersions() throws Exception {
        ArrayList lists = new ArrayList();

        File[] files = archivePath.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        });
        for (int i = 0; i < files.length; i++) {
            URL db4oEngineURL = files[i].toURL();
            String version = getDb4oVersion(db4oEngineURL);
            lists.add(version);
        }
        String[] db4oVersions = new String[lists.size()];
        lists.toArray(db4oVersions);
        return db4oVersions;
    }

    private String getDb4oVersion(URL db4oEngineURL) throws Exception {
        ClassLoader loader = getVersionClassLoader(db4oEngineURL);
        Class clazz = loader.loadClass("com.db4o.Db4o");
        Method method = clazz.getMethod("version", new Class[] {});
        String version = (String) method.invoke(null, new Object[] {});
        return version.replace(' ', '_');
    }
    
    private ClassLoader getVersionClassLoader(URL url)
            throws MalformedURLException {
        ClassLoader loader = (ClassLoader) cache.get(url);
        if (loader == null) {
            URL[] urls = new URL[] { testBinPath.toURL(), url, };
            loader = new VersionClassLoader(urls, prefixes);
            cache.put(url, loader);
        }
        return loader;
    }
}
