/* Copyright (C) 2004 - 2006	db4objects Inc.	  http://www.db4o.com */

using System;
using System.IO;
using com.db4o.test.acid;
using com.db4o.test.inside.query;
using com.db4o.test.soda;
using com.db4o.test.cs;
using System.Collections;

namespace com.db4o.test 
{

	public class AllTestsConfAll 
	{

		/**
		 * Add new testcases here
		 */
		internal Type[] TESTS
		{
			// using property instead of array initializer
			// so the camelCase->PascalCase converter does
			// not get lost
			get
			{
				ArrayList tests = new ArrayList();

#if (NET || NET_2_0) && !MONO
				tests.Add(typeof(aliases.ClassAliasesTestCase));
#endif

				tests.Add(typeof(ArrayNOrder)); 
				tests.Add(typeof(BackReferences));
#if NET || NET_2_0 || MONO
				tests.Add(typeof(BasicClusterTest));
#endif
				tests.Add(typeof(BindFileSize));
				tests.Add(typeof(Book));
				tests.Add(typeof(Callbacks));
				tests.Add(typeof(CascadeDeleteArray));
				tests.Add(typeof(CascadeOnActivate));
				tests.Add(typeof(CascadeOnDelete));
				
				// FIXME: these tests are currently failing
				//tests.Add(typeof(CascadeOnUpdate));
				//tests.Add(typeof(CascadeToArray));
				//tests.Add(typeof(CascadeToArrayList));
				
				tests.Add(typeof(CascadeToExistingArrayListMember));
				tests.Add(typeof(CascadeToHashtable));
				tests.Add(typeof(CaseInsensitive));
				tests.Add(typeof(CrashSimulatingTest));
				tests.Add(typeof(Circular1));
				tests.Add(typeof(Circular2));

				tests.Add(typeof(CollectionActivation));

				tests.Add(typeof(config.attributes.IndexedWithAttributeByIdentity));
				
				tests.Add(typeof(Cs));
				tests.Add(typeof(CsArrays));
#if !MONO
#if NET || NET_2_0
				tests.Add(typeof(CsAppDomains));
				tests.Add(typeof(CsAssemblyVersionChange));
#endif
#endif
				tests.Add(typeof(CsCascadeDeleteToStructs));
				tests.Add(typeof(CsCollections));
				tests.Add(typeof(CsCustomTransientAttribute));
				tests.Add(typeof(CsDate));
	
				tests.Add(typeof(CsDelegate));

				tests.Add(typeof(CsDisposableTestCase));

				tests.Add(typeof(CsEnum));
												  
				tests.Add(typeof(CsEvaluationDelegate));
				
#if !MONO
				tests.Add(typeof(CsMarshalByRef));
#endif
				tests.Add(typeof(CsStructs));
				tests.Add(typeof(CsStructsRegression));

				tests.Add(typeof(CsType));

				tests.Add(typeof(DerivedFromArrayList));
#if !MONO
				tests.Add(typeof(Db4oHashMap));
#endif
				tests.Add(typeof(Db4oLinkedList));
				// FIXME: current failing
				// tests.Add(typeof(Db4oLinkedListUntyped));
				tests.Add(typeof(DifferentAccessPaths));
				tests.Add(typeof(DualDelete));
				tests.Add(typeof(ExtendsDate));
				tests.Add(typeof(ExtendsHashTable));
				tests.Add(typeof(ExternalBlobs));
				tests.Add(typeof(ExtMethods));

				tests.Add(typeof(events.EventRegistryTestCase));
				
				
				tests.Add(typeof(HashtableModifiedUpdateDepth));
#if NET || NET_2_0 || MONO
				tests.Add(typeof(HoldsAnArrayList));
#endif
				tests.Add(typeof(IndexedByIdentity));
				tests.Add(typeof(Isolation));
				tests.Add(typeof(IsStored));
				
				tests.Add(typeof(j4otest.TypeNameTest));
				
				tests.Add(typeof(KeepCollectionContent));
				tests.Add(typeof(MaxByEvaluation));
				tests.Add(typeof(Messaging));

				tests.Add(typeof(nativequeries.Cat));
				tests.Add(typeof(nativequeries.NativeQueriesTestCase));
				tests.Add(typeof(nativequeries.cats.TestCatConsistency));
#if !CF_1_0 && !CF_2_0 && !MONO
				tests.Add(typeof(nativequeries.MultipleAssemblySupportTestCase));
#endif
				tests.Add(typeof(nativequeries.OptimizationFailuresTestCase));
				tests.Add(typeof(nativequeries.StringComparisonTestCase));

#if NET_2_0 || CF_2_0
				tests.Add(typeof(net2.Net2GenericContainers));
				tests.Add(typeof(net2.Net2GenericList));
	#if !MONO
				tests.Add(typeof(net2.Net2GenericOtherCollections));
	#endif
				tests.Add(typeof(net2.Net2NullableTypes));
				tests.Add(typeof(net2.Net2QueryForClass));
				tests.Add(typeof(net2.Net2SimpleGenericType));
#endif		

				tests.Add(typeof(NoInstanceStored));
				tests.Add(typeof(NoInternalClasses));
				tests.Add(typeof(ObjectSetIDs));
				tests.Add(typeof(OrClassConstraintInList));
				tests.Add(typeof(PersistStaticFieldValues));

				// disabled because it fails due to fix
				// See comments in YapClass.DeleteEmbedded1() in the Java sources

				// tests.Add(typeof(PrimitiveArrayFileSize));

				tests.Add(typeof(PrimitivesInCollection));

#if !CF_1_0 && !CF_2_0
				tests.Add(typeof(QueryExpressionBuilderTestCase));
#endif
				tests.Add(typeof(QueryDeleted));
				tests.Add(typeof(QueryNonExistant));
				tests.Add(typeof(Refresh));
				tests.Add(typeof(Rename));
				tests.Add(typeof(SelectDistinct));
				tests.Add(typeof(ServerRevokeAccess));
				tests.Add(typeof(SetDeactivated));
				tests.Add(typeof(SetSemaphore));
				tests.Add(typeof(SharedObjectContainer));
				tests.Add(typeof(Soda));
				tests.Add(typeof(SodaEvaluation));
				tests.Add(typeof(SodaNoDuplicates));
				tests.Add(typeof(SodaNumberCoercion));
				//tests.Add(typeof(SortedSameOrder)); FIXME, where does this comes from?
				tests.Add(typeof(StoredClassInformation));
				tests.Add(typeof(StoredFieldValue));
#if !MONO
				tests.Add(typeof(StringInLists));
#endif
				tests.Add(typeof(SwitchingFilesFromClient));

				tests.Add(typeof(TestHashTable));
				tests.Add(typeof(TwoClients));
				tests.Add(typeof(TypedArrayInObject));
				tests.Add(typeof(TypedDerivedArray));
				tests.Add(typeof(UnknownClass));
#if !CF_1_0 && !CF_2_0
				tests.Add(typeof(UpdatingDb4oVersions));
#endif
				
				return (Type[])tests.ToArray(typeof(Type));
			}
		}
	   
		/**
		  * the number of test runs
		  */
		internal int RUNS = 1;

		/**
		* delete the database files
		*/
		internal bool DELETE_FILE = true;
	  
		/**
		  * run the tests stand-alone
		  */
		internal bool SOLO = true;
	  
		/**
		  * run the tests in client/server mode
		  */
		internal bool CLIENT_SERVER = true;

		/**
		 * use ObjectServer#OpenClient() instead of Db4o.OpenClient()
		 */
		public static bool EMBEDDED_CLIENT = false;
	  
		/**
		  * run the client/server test against a remote server.
		  * This requires AllTestsServer to be started on the other machine and
		  * SERVER_HOSTNAME to be set correctly.
		  */
		internal bool REMOTE_SERVER = false;

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
		public static String BLOB_PATH = Path.Combine(Path.GetTempPath(), "BLOBS");
		

	}
}
