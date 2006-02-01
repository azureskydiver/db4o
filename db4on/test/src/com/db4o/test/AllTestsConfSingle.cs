/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using System.IO;
using com.db4o.test.cs;
using com.db4o.test.j4otest;
using com.db4o.test.nativequeries;
using com.db4o.test.soda;

namespace com.db4o.test
{
	public class AllTestsConfSingle
	{
		/**
         * new testcases here
         */

		internal Type[] TESTS
		{
			// using property instead of array initializer
			// so the camelCase->PascalCase converter does
			// not get lost
			get
			{
				ArrayList tests = new ArrayList();

				tests.Add(typeof (SimplestPossible));
                tests.Add(typeof(CsDelegate));
				tests.Add(typeof(OptimizationFailuresTestCase));
				//tests.Add(typeof (CsAssemblyVersionChange));
                tests.Add(typeof(com.db4o.test.nativequeries.Cat));
#if !CF_1_0 && !CF_2_0
				tests.Add(typeof (MultipleAssemblySupportTestCase));
#endif
				//tests.Add(typeof(com.db4o.test.ExternalBlobs));
				//tests.Add(typeof(com.db4o.test.soda.SodaNumberCoercion));
#if NET_2_0
				tests.Add(typeof(net2.Net2GenericContainers));
				tests.Add(typeof(net2.Net2GenericDictionary));
				tests.Add(typeof(net2.Net2GenericList));
				tests.Add(typeof(net2.Net2GenericOtherCollections));
#endif
				tests.Add(typeof(com.db4o.test.nativequeries.NativeQueriesTestCase));
				tests.Add(typeof(com.db4o.test.nativequeries.NQRegressionTests));
				tests.Add(typeof(TypeNameTest));				
				return (Type[]) tests.ToArray(typeof (Type));
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