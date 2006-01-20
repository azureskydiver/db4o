/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.test.acid.*;
import com.db4o.test.cluster.*;
import com.db4o.test.conjunctions.*;
import com.db4o.test.constraints.*;
import com.db4o.test.foundation.Collection4TestCase;
import com.db4o.test.interfaces.*;
import com.db4o.test.nativequery.*;
import com.db4o.test.soda.*;

public class AllTestsConfAll extends TestSuite{

	static protected final String TEST_CONFIGURATION = "AllTestsConfAll";
	
    protected void addTestSuites(TestSuite suites) {
        suites.add(this);
        suites.add(new ConstraintsTestSuite());
        suites.add(new ConjunctionsTestSuite());
        suites.add(new InterfacesTestSuite());
        if(Db4oVersion.MAJOR >= 5){
            suites.add(new NativeQueryTestSuite());
        }
	}
    
    public Class[] tests(){
        return new Class[] {
	    	ArrayNOrder.class,
	    	Backup.class,
            BasicClusterTest.class,
	    	BindFileSize.class,
            CallbackCanDelete.class,
	    	Callbacks.class,
	    	CascadedDeleteUpdate.class,
			CascadeDeleteArray.class,
            CascadeDeleteDeleted.class,
			CascadeDeleteFalse.class,
	    	CascadeOnActivate.class,
	        CascadeOnDelete.class,
	        CascadeOnUpdate.class,
	        CascadeToArray.class,
	        CascadeToExistingVectorMember.class,
	        CascadeToHashtable.class,
	        CascadeToVector.class,
	        CaseInsensitive.class,
            ChangeIdentity.class,
	        Circular1.class,
	        Circular2.class,
	        Collection4TestCase.class,
	        // takes too long in JDK1.1 setup due to locking timeout
            // CrashSimulatingTest.class,
	        CreateIndex.class,
	        CreateIndexInherited.class,
			CustomActivationDepth.class,
            DeleteDeep.class,
			DeepSet.class,
	        DifferentAccessPaths.class,
	        DualDelete.class,
            EncryptionWrongPassword.class,
			ExtMethods.class,
			ExtendsDate.class,
			FileSizeOnReopen.class,
	        GetAll.class,
            GetAllSoda.class,
			GreaterOrEqual.class,
			IndexedByIdentity.class,
			IndexCreateDrop.class,
			IndexedUpdatesWithNull.class,
			InternStrings.class,
	        IsStored.class,
	        Isolation.class,
			Messaging.class,
            MultiDelete.class,
            MultiLevelIndex.class,
            NeverAnObjectStored.class,
			NoInstanceStored.class,
	        NoInternalClasses.class,
	        NullWrapperQueries.class,
	        ObjectContainerIsTransient.class,
			ObjectSetIDs.class,
			ParameterizedEvaluation.class,
	        PersistStaticFieldValues.class,
            
            // disabled because it fails due to fix
            // See comments in: YapClass.deleteEmbedded1()
            
	        // PrimitiveArrayFileSize.class,
            
			QueryDeleted.class,
            QueryForUnknownField.class,
	        QueryNonExistant.class,
            ReadAs.class,
            ReferenceThis.class,
	        Refresh.class,
	        Rename.class,
			SameSizeOnReopen.class,
	        SerializableTranslator.class,
			ServerRevokeAccess.class,
	    	SetDeactivated.class,
	    	SetSemaphore.class,
	    	SharedObjectContainer.class,
            SimpleTypeArrayInUntypedVariable.class,
			SmallerOrEqual.class,
	    	Soda.class,
	    	SodaNoDuplicates.class,
	    	SodaNumberCoercion.class,
	    	//SortResult.class,
	    	StoredClassInformation.class,
	    	StoredFieldValue.class,
	    	//StoreObject.class,
			SwitchingFilesFromClient.class,
	        TestHashTable.class,
	        TwoClients.class,
	        TypedArrayInObject.class,
	        TypedDerivedArray.class,
            UuidAware.class,
	        XTEAEncryption.class
        };
    }
    

    /**
      * the number of test runs 
      */
    public int RUNS = 1;

	/**
	 * delete the database files
	 */
	public boolean DELETE_FILE = true;

    /**
      * run the tests stand-alone 
      */
    public boolean SOLO = true;

    /**
      * run the tests in client/server mode 
      */
    public boolean CLIENT_SERVER = true;
    
    /**
     * run the test against a memory file instead of disc file
     */
    public static boolean MEMORY_FILE = false;

    /**
      * run the client/server test against a remote server. 
      * This requires AllTestsServer to be started on the other machine and 
      * SERVER_HOSTNAME to be set correctly.
      */
    final boolean REMOTE_SERVER = false;

    /**
     * the database file to be used for the server.
     */
    public static String FILE_SERVER = "xt_serv.yap";

    /**
     * the database file to be used stand-alone.
     */
    public static String FILE_SOLO = "xt_solo.yap";
    
    /**
     * the server host name.
     */
    public static String SERVER_HOSTNAME = "localhost";

    /**
     * the server port.
     */
    public static int SERVER_PORT = 4448;

    /**
     * the db4o user.
     */
    public static String DB4O_USER = "db4o";

    /**
     * the db4o password.
     */
    public static String DB4O_PASSWORD = "db4o";
    
    /**
     * path to blobs held externally
     */
	public static String BLOB_PATH = "test/TEMP/db4oTestBlobs";

}
