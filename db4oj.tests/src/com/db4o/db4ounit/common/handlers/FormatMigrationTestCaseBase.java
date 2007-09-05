/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;


public abstract class FormatMigrationTestCaseBase implements TestLifeCycle{
    
    private Configuration _config;

    public void configure(){
        _config = Db4o.newConfiguration();
        _config.allowVersionUpdates(true);
        configure(_config);
    }
    
    protected static final String PATH = "./test/db4oVersions/";

    protected String fileName(){
        return fileName(Db4o.version());
    }
    
    protected String fileName(String versionName){
        return oldVersionFileName(versionName) + ".yap";
    }
    
    protected String oldVersionFileName(String versionName){
        return PATH + fileNamePrefix() + versionName.replace(' ', '_') ;
    }
    
    public void setUp() throws Exception {
        configure();
        String file = fileName();
        File4.mkdirs(PATH);
        if(File4.exists(file)){
            File4.delete(file);
        }
        ExtObjectContainer objectContainer = Db4o.openFile(_config, file).ext();
        try {
            store(objectContainer);
        } finally {
            objectContainer.close();
        }
    }
    
    public void test() throws IOException{
        for(int i = 0; i < versionNames().length; i ++){
            String fileName = oldVersionFileName(versionNames()[i]);
            if(File4.exists(fileName)){
                String testFileName = fileName(versionNames()[i]); 
                File4.delete(testFileName);
                File4.copy(fileName, testFileName);
                checkDatabaseFile(testFileName);
                // Twice, to ensure everything is fine after opening, converting and closing.
                checkDatabaseFile(testFileName);
            }else{
                
                // FIXME: Change back to Assert.fail as soon as all new handler
                //        test files are available for all Java and .NET versions
                
                // Assert.fail("Version upgrade check failed. File not found:" + fileName);
                
                System.err.println("Version upgrade check failed. File not found:");
                System.err.println(fileName);

            }
        }
    }
    
    public void tearDown() throws Exception {
        // do nothing
    }
    
    private void checkDatabaseFile(String testFile) {
        ExtObjectContainer objectContainer = Db4o.openFile(_config, testFile).ext();
        try {
            assertObjectsAreReadable(objectContainer);
        } finally {
            objectContainer.close();
        }
    }
    
    protected abstract String[] versionNames();
    
    protected abstract String fileNamePrefix();

    protected abstract void configure(Configuration config);
    
    protected abstract void store(ExtObjectContainer objectContainer);
    
    protected abstract void assertObjectsAreReadable(ExtObjectContainer objectContainer);
    
}
