/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.ibs.tests;

import java.io.*;
import java.util.*;

import com.db4o.foundation.io.*;
import com.db4o.ibs.*;
import com.db4o.ibs.engine.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

public class SlotBasedChangeSetProcessorTestCase extends SlotBasedChangeSetTestCaseBase implements OptOutCS, OptOutDefragSolo{
    
    private static final String FILE = "ibsProcessorBackup.db4o";

    public void testUpdateOnlyIntField() throws Exception {
        runChangeSetTest(new ChangeSetTest() {
            public void applyChanges() {
                Item item = (Item) retrieveOnlyInstance(Item.class);
                item.intValue = -1;
                commitItem(item);
            }
            public void runAssertions() {
                Item item = (Item) retrieveOnlyInstance(Item.class);
                Assert.areEqual(-1, item.intValue);
            }
        });
    }
    
    public void testDeleteObject() throws Exception {
//        runChangeSetTest(new ChangeSetTest() {
//            public void applyChanges() {
//                Item item = (Item) retrieveOnlyInstance(Item.class);
//                db().delete(item);
//            }
//            public void runAssertions() {
//                Item item = (Item) retrieveOnlyInstance(Item.class);
//                Assert.areEqual(-1, item.intValue);
//            }
//        });
        
    }
    
    static interface ChangeSetTest {
        public void applyChanges();
        public void runAssertions();
    }
    
    private void runChangeSetTest(ChangeSetTest test) throws Exception{
        applyChangeSets(generateChangeSets(test));
        test.runAssertions();
    }

    private List<ChangeSet> generateChangeSets(ChangeSetTest test) throws Exception{
        deleteBackupFile();
        reopenFixtureAndCopyFiles(fixtureDatabasePath(), backupDatabasePath());
        setUpChangeSetPublisher();
        test.applyChanges();
        return changeSets();
    }
    
    private void applyChangeSets(List<ChangeSet> changeSets) throws Exception, IOException {
        reopenFixtureAndCopyFiles(backupDatabasePath(), fixtureDatabasePath());
        ChangeSetProcessor processor = new SlotBasedChangeSetEngine().newProcessorFor(db());
        for(ChangeSet changeSet : changeSets){
            processor.apply(changeSet);
        }
        deleteBackupFile();
    }
    
    public void reopenFixtureAndCopyFiles(String sourcePath, String targetPath) throws Exception{
        fixture().close();
        File4.copy(sourcePath, targetPath);
        fixture().open(getClass());
    }

    private String fixtureDatabasePath() {
        Db4oSolo fixture =  (Db4oSolo) fixture();
        return fixture.getAbsolutePath();
    }

    private void deleteBackupFile() {
        File4.delete(backupDatabasePath());
    }
    
    private static String backupDatabasePath() {
        return CrossPlatformServices.databasePath(FILE);
    }

}
