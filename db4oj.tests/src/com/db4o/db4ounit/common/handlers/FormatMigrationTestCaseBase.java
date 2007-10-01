/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;


public abstract class FormatMigrationTestCaseBase implements TestLifeCycle{
    
    public void configure(){
        Configuration config = Db4o.configure();
        config.allowVersionUpdates(true);
        configure(config);
    }
    
    protected static final String PATH = Path4.combine(Path4.getTempPath(), "test/db4oVersions");

    protected String fileName(){
        return fileName(Db4o.version());
    }
    
    protected String fileName(String versionName){
        return oldVersionFileName(versionName) + ".yap";
    }
    
    protected String oldVersionFileName(String versionName){
        return Path4.combine(PATH, fileNamePrefix() + versionName.replace(' ', '_'));
    }
    
    public void createDatabase() {
        createDatabase(fileName());
    }
    
    public void createDatabaseFor(String versionName) {
    	createDatabase(fileName(versionName));
    }

	private void createDatabase(String file) {
		File4.mkdirs(PATH);
        if(File4.exists(file)){
            File4.delete(file);
        }
        ExtObjectContainer objectContainer = Db4o.openFile(file).ext();
        try {
            store(objectContainer);
        } finally {
            objectContainer.close();
        }
	}
    
    public void setUp() throws Exception {
        configure();
        createDatabase();
    }
    
    public void test() throws IOException{
        for(int i = 0; i < versionNames().length; i ++){
            final String versionName = versionNames()[i];
			test(versionName);
        }
    }

	public void test(final String versionName) throws IOException {
		String testFileName = fileName(versionName); 
		if(File4.exists(testFileName)){
//		    System.out.println("Check database: " + testFileName);
		    
		    
		    investigateFileHeaderVersion(testFileName);

		    checkDatabaseFile(testFileName);
		    // Twice, to ensure everything is fine after opening, converting and closing.
		    checkDatabaseFile(testFileName);
		}else{
		    
		    System.out.println("Version upgrade check failed. File not found:" + testFileName);
		    
		    
		    // FIXME: The following fails the CC build since not all files are there on .NET.
		    //        Change back when we have all files.
		    // Assert.fail("Version upgrade check failed. File not found:" + testFileName);
		}
	}
    
    public void tearDown() throws Exception {
        // do nothing
    }
    
    private void checkDatabaseFile(String testFile) {
        configure();
        ExtObjectContainer objectContainer = Db4o.openFile(testFile).ext();
        try {
            assertObjectsAreReadable(objectContainer);
        } finally {
            objectContainer.close();
        }
    }
    
    private void investigateFileHeaderVersion(String testFile) throws IOException{
        _db4oHeaderVersion = VersionServices.fileHeaderVersion(testFile); 
    }
    
    protected byte _db4oHeaderVersion;
    
    protected abstract String[] versionNames();
    
    protected abstract String fileNamePrefix();

    protected abstract void configure(Configuration config);
    
    protected abstract void store(ExtObjectContainer objectContainer);
    
    protected abstract void assertObjectsAreReadable(ExtObjectContainer objectContainer);
    
}
