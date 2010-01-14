package com.db4o.db4ounit.common.freespace;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class FreespaceManagerTypeChangeSlotCountTestCase extends TestWithTempFile{

    private static final int SIZE = 10000;
    private LocalObjectContainer _container;
    private Closure4<Configuration> _currentConfig;
    
    public static void main(String[] args) {
        new ConsoleTestRunner(FreespaceManagerTypeChangeSlotCountTestCase.class).run();
    }
    
    public void testMigrateFromRamToBTree() throws Exception {
        createDatabaseUsingRamManager();
        migrateToBTree();
        reopen();
        createFreeSpace();
        List initialSlots = getSlots(_container.freespaceManager());
        reopen();
        List currentSlots = getSlots(_container.freespaceManager());
        Assert.areEqual(initialSlots, currentSlots);
        _container.close();
    }
    
    public void testMigrateFromBTreeToRam() throws Exception {
        createDatabaseUsingBTreeManager();
        migrateToRam();
        createFreeSpace();
        List initialSlots = getSlots(_container.freespaceManager());
        reopen();
        Assert.areEqual(initialSlots, getSlots(_container.freespaceManager()));
        _container.close();
        
    }
    
    
    private void reopen() {
        _container.close();
        open();
    }

    private void createDatabaseUsingRamManager() {
        configureRamManager();
        open();
    }
    
    private void createDatabaseUsingBTreeManager() {
        configureBTreeManager();
        open();
    }

    private void open() {
        _container = (LocalObjectContainer)Db4o.openFile(_currentConfig.run(), tempFile());
    }

    private void createFreeSpace() {
        Slot slot = _container.allocateSlot(SIZE);
        _container.free(slot);
    }

    private void migrateToBTree() throws Exception {
        _container.close();
        configureBTreeManager();
        open();
    }


    private void configureBTreeManager() {
    	_currentConfig = new Closure4<Configuration>() { public Configuration run() {
         	final Configuration config = Db4o.newConfiguration();
         	config.freespace().useBTreeSystem();
         	return config;
         }};
    }
    
    private void migrateToRam() throws Exception {
        _container.close();
        configureRamManager();
        open();
    }


    private void configureRamManager() {
        _currentConfig = new Closure4<Configuration>() { public Configuration run() {
        	final Configuration config = Db4o.newConfiguration();
        	config.freespace().useRamSystem();
        	return config;
        }};
    }
    
    private List getSlots(FreespaceManager freespaceManager) {
        final List retVal = new ArrayList();
        freespaceManager.traverse(new Visitor4(){
            public void visit(Object obj) {
                retVal.add(obj);
            }
        });
        return retVal;
    }

}
