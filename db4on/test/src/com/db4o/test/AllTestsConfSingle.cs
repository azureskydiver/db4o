/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;

namespace com.db4o.test {

    public class AllTestsConfSingle {

        /**
         * new testcases here
         */
        internal Type[] TESTS = new Type[]{
            typeof(com.db4o.test.SimplestPossible),
            //typeof(com.db4o.test.soda.SodaNumberCoercion),
            typeof(com.db4o.test.net2.Net2GenericContainers),
            //typeof(com.db4o.test.nativequeries.NativeQueriesTestCase),
            //typeof(com.db4o.test.nativequeries.NQRegressionTests),
            typeof(com.db4o.test.j4otest.TypeNameTest),
            //typeof(com.db4o.test.net2.Net2GenericContainers),
         };
       
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
        internal bool CLIENT_SERVER = false;
      
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
