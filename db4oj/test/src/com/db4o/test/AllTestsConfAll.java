/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.test.interfaces.*;
import com.db4o.test.reflect.*;
import com.db4o.test.replication.*;

public class AllTestsConfAll extends TestSuite{

	static protected final String TEST_CONFIGURATION = "AllTestsConfAll";
	
    protected TestSuite[] TEST_SUITES = new TestSuite[]{
        
        // remove the com.db4o.test.jdk5 package 
        // to run on JDKs prior to 1.5
        
        suite("com.db4o.test.jdk5.Jdk5Tests"),
        this,
        new ReplicationTestSuite(),
        new InterfacesTestSuite()
        
    };
    
    public Class[] tests(){
        return new Class[] {
	    	ArrayNOrder.class,
	    	ArrayListInHashMap.class,
	    	Backup.class,
	    	BindFileSize.class,
	    	Callbacks.class,
	    	CallConstructors.class,
	    	CascadedDeleteUpdate.class,
			CascadeDeleteArray.class,
            CascadeDeleteDeleted.class,
			CascadeDeleteFalse.class,
	    	CascadeOnActivate.class,
	        CascadeOnDelete.class,
	        CascadeOnUpdate.class,
	        CascadeToArray.class,
	        CascadeToExistingVectorMember.class,
	        CascadeToHashMap.class,
	        CascadeToHashtable.class,
	        CascadeToVector.class,
	        CaseInsensitive.class,
	        Circular1.class,
	        Circular2.class,
	        CollectionActivation.class,
	        CreateIndex.class,
	        CreateIndexInherited.class,
			CustomActivationDepth.class,
			GenericObjects.class,
            Db4oLinkedList.class,
			Db4oHashMap.class,
			DeleteRemovedMapElements.class,
			DeepSet.class,
	        DifferentAccessPaths.class,
	        DiscreteArrayInMap.class,
	        DualDelete.class,
			ExtMethods.class,
			ExtendsDate.class,
			ExtendsHashMap.class,
			ExternalBlobs.class,
			FileSizeOnReopen.class,
			FulltextIndex.class,
	        GetAll.class,
			GreaterOrEqual.class,
			HashMapClearUnsaved.class,
			IndexedByIdentity.class,
			IndexCreateDrop.class,
			IndexedUpdatesWithNull.class,
	        IsStored.class,
	        Isolation.class,
	        KeepCollectionContent.class,
			MassUpdates.class,
			Messaging.class,
			MultipleEvaluationGetObjectCalls.class,
            NeverAnObjectStored.class,
			NoInstanceStored.class,
	        NoInternalClasses.class,
	        NullWrapperQueries.class,
	        ObjectContainerIsTransient.class,
			ObjectSetIDs.class,
			OrClassConstraintInList.class,
			ParameterizedEvaluation.class,
	        PersistStaticFieldValues.class,
            
            // disabled because it fails due to fix
            // See comments in: YapClass.deleteEmbedded1()
            
	        // PrimitiveArrayFileSize.class,
            
			PrimitivesInCollection.class,
			QueryDeleted.class,
            QueryForList.class,
	        QueryNonExistant.class,
            ReferenceThis.class,
            Reflection.class,
	        Refresh.class,
            RefreshList.class,
	        Rename.class,
			SameSizeOnReopen.class,
	        SelectDistinct.class,
	        SerializableTranslator.class,
			ServerRevokeAccess.class,
	    	SetDeactivated.class,
	    	SetSemaphore.class,
	    	SharedObjectContainer.class,
            SimpleTypeArrayInUntypedVariable.class,
			SmallerOrEqual.class,
	    	Soda.class,
	    	SodaEvaluation.class,
	    	SodaNoDuplicates.class,
	    	StoredClassInformation.class,
	    	StoredFieldValue.class,
	    	StringCaseInsensitive.class,
	    	StringInLists.class,
			SwitchingFilesFromClient.class,
	        TestHashTable.class,
	        TestHashMap.class,
	        TestStringBuffer.class,
	        TestTreeMap.class,
			TransientClone.class,
			TreeSetCustomComparable.class,
	        // TwoClients.class,
	        TypedArrayInObject.class,
	        TypedDerivedArray.class,
	        UpdatingDb4oVersions.class
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
