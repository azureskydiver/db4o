/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using com.db4o.test.cs;

namespace com.db4o.test {

    public class AllTestsConfAll {

        /**
         * new testcases here
         */
        internal Type[] TESTS = new Type[]{
            typeof(ArrayNOrder),
            typeof(BindFileSize),
            typeof(Book),
            typeof(Callbacks),
            typeof(CascadeDeleteArray),
            typeof(CascadeOnActivate),
            typeof(CascadeOnDelete),
            typeof(CascadeOnUpdate),
            typeof(CascadeToArray),
            typeof(CascadeToArrayList),
            typeof(CascadeToExistingArrayListMember),
            typeof(CascadeToHashtable),
            typeof(CaseInsensitive),
            typeof(Circular1),
            typeof(Circular2),
            typeof(CollectionActivation),
            typeof(Cs),
            typeof(CsArrays),
            typeof(CsCascadeDeleteToStructs),
            typeof(CsCollections),
            typeof(CsCollections),
            typeof(CsCustomTransientAttribute),
            typeof(CsEvaluationDelegate),
            typeof(CsStructs),
            typeof(DerivedFromArrayList),
            typeof(Db4oHashMap),
            typeof(Db4oLinkedList),
            typeof(DifferentAccessPaths),
            typeof(DualDelete),
            typeof(ExtendsDate),
            typeof(ExtendsHashTable),
            typeof(ExternalBlobs),
            typeof(ExtMethods),
            typeof(IndexedByIdentity),
            typeof(Isolation),
            typeof(IsStored),
            typeof(KeepCollectionContent),
            typeof(MaxByEvaluation),
            typeof(Messaging),
            typeof(NoInternalClasses),
            typeof(ObjectSetIDs),
            typeof(OrClassConstraintInList),
            typeof(PersistStaticFieldValues),

            // disabled because it fails due to fix
            // See comments in YapClass.deleteEmbedded1() in the Java sources

            // typeof(PrimitiveArrayFileSize),

            typeof(PrimitivesInCollection),
            typeof(QueryDeleted),
            typeof(QueryNonExistant),
            typeof(Refresh),
            typeof(Rename),
            typeof(SelectDistinct),
            typeof(ServerRevokeAccess),
            typeof(SetDeactivated),
            typeof(SetSemaphore),
            typeof(SharedObjectContainer),
            typeof(Soda),
            typeof(SodaEvaluation),
            typeof(SodaNoDuplicates),
            typeof(StoredClassInformation),
            typeof(StoredFieldValue),
            typeof(StringInLists),
            typeof(SwitchingFilesFromClient),
            typeof(TestHashTable),
            typeof(TwoClients),
            typeof(TypedArrayInObject),
            typeof(TypedDerivedArray),
            typeof(UpdatingDb4oVersions)
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
        public static String BLOB_PATH = "test/TEMP/db4oTestBlobs";
        

    }
}