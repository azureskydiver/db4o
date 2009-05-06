/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import com.db4o.db4ounit.common.assorted.*;
import com.db4o.db4ounit.common.freespace.*;
import com.db4o.db4ounit.common.handlers.*;
import com.db4o.db4ounit.util.*;
import com.db4o.foundation.*;

import db4ounit.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Db4oMigrationTestSuite implements TestSuiteBuilder {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(Db4oMigrationTestSuite.class).run();
	}

	public Iterator4 iterator() {
		return new Db4oMigrationSuiteBuilder(testCases(), libraries()).iterator();
	}

	private String[] libraries() {
		
		if (true) {
			return Db4oMigrationSuiteBuilder.ALL;
		}
		
		if (true) {
			// run against specific libraries + the current one
			String javaPath = "db4o.archives/java1.2/db4o-5.7-java1.2.jar";
			String netPath = "db4o.archives/net-2.0/7.4/Db4objects.Db4o.dll";
			return new String[] {
				WorkspaceServices.workspacePath(javaPath),
			};
		} 
		return Db4oMigrationSuiteBuilder.CURRENT;
	}

	protected Class[] testCases() {
		
	    final Class[] classes = new Class[] {
            BooleanHandlerUpdateTestCase.class,
            ByteHandlerUpdateTestCase.class,
            CascadedDeleteFileFormatUpdateTestCase.class,
            CharHandlerUpdateTestCase.class,
            DateHandlerUpdateTestCase.class,
            DoubleHandlerUpdateTestCase.class,
            EncryptedFileMigrationTestCase.class,
            FloatHandlerUpdateTestCase.class,
            IntHandlerUpdateTestCase.class,
            InterfaceHandlerUpdateTestCase.class,
            LongHandlerUpdateTestCase.class,
            MultiDimensionalArrayHandlerUpdateTestCase.class,
            NestedArrayUpdateTestCase.class,
            ObjectArrayUpdateTestCase.class,
            PlainObjectUpdateTestCase.class,
            QueryingMigrationTestCase.class,
            ShortHandlerUpdateTestCase.class,
            StringHandlerUpdateTestCase.class,
            
            // Order to run freespace/Encrypted tests last is
            // deliberate. Global configuration Db4o.configure()
            // is changed in the #setUp call and reused.
            
            IxFreespaceMigrationTestCase.class,
            FreespaceManagerMigrationTestCase.class,

		};
	    return addJavaTestCases(classes);
	}
	
    /**
     * @sharpen.remove null
     */
	protected Class[] javaOnlyTestCases(){
	    return new Class[] {
            ArrayListUpdateTestCase.class,
            HashtableUpdateTestCase.class,
            KnownClassesMigrationTestCase.class,
            TreeSetHandlerUpdateTestCase.class,
            VectorUpdateTestCase.class,
	    };
	}
	
	protected Class[] addJavaTestCases(Class[] classes){
        Class[] javaTestCases = javaOnlyTestCases(); 
        if(javaTestCases == null){
            return classes;
        }
        int len = javaTestCases.length;
        Class[] allClasses = new Class[classes.length + len];
        System.arraycopy(javaTestCases, 0, allClasses, 0,len );
        System.arraycopy(classes, 0, allClasses, len,classes.length);
        return allClasses;
    }

}
