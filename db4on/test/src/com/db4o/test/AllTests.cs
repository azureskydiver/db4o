/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using System.IO;
using j4o.io;
using j4o.lang;
using j4o.lang.reflect;
using com.db4o;
using com.db4o.query;
using com.db4o.config;
using com.db4o.test.cs;
using com.db4o.tools;
using com.db4o.test.soda;

namespace com.db4o.test {

    /// <summary>
    ///
    /// This is the main db4o regression test.
    ///
    /// The parameters of the testing environment and all registered test
    /// cases can be found in AllTestsConfAll.cs.
    ///
    /// Derive this class from AllTestsConfSingle if you only want to run
    /// single test cases and enter the test case that you want to run in
    /// the AllTestsConfSingle#TESTS[] array.
    /// </summary>
    ///
    public class AllTests : AllTestsConfAll, Runnable  {

        public static void Main(String[] args) {

//            Configuration conf = Db4o.configure();
//            conf.messageLevel(-1);
//
//            //        conf.automaticShutDown(false);
//            //        conf.lockDatabaseFile(false);
//            //        conf.singleThreadedClient(true);
//            //        conf.automaticShutDown(false);
//            //        conf.lockDatabaseFile(false);
//            //        conf.weakReferences(false);
//            //        conf.callbacks(false);
//            //        conf.detectSchemaChanges(false);
//            //        conf.testConstructors(false);
//            //        conf.discardFreeSpace(Integer.MAX_VALUE);
//            //        conf.encrypt(true);
//
//            // BenchMark.Main(null);
//
            new AllTests().run();
        }

        public AllTests() : base() {
            Test.currentRunner = this;
            if(Compat.compact()){
                CLIENT_SERVER = false;
            }
        }

        public void run1(){
            Thread.sleep(4000);
        }
      
        public virtual void run() {
            Db4o.configure().messageLevel(-1);
            logConfiguration();
            long time1 = j4o.lang.JavaSystem.currentTimeMillis();
            if(DELETE_FILE){
                Test.delete();
            }
            configure();
            
            for (Test.run = 1; Test.run <= RUNS; Test.run++) {
                Console.WriteLine("com.db4o.AllTests run " + Test.run + " from " + RUNS);
                if (SOLO) {
                    Test.runServer = false;
                    Test.clientServer = false;
                    runTests();
                }
                if (CLIENT_SERVER) {
                    Test.runServer = !REMOTE_SERVER;
                    Test.clientServer = true;
                    runTests();
                }
                Test.end();
            }
            time1 = j4o.lang.JavaSystem.currentTimeMillis() - time1;
            Console.WriteLine("\n\nAllTests completed.\nAssertions: " + Test.assertionCount + "\nTime: " + time1 + "ms");
            if(Test.errorCount == 0){
                Console.WriteLine("No errors detected.\n");
            }else{
                Console.WriteLine("" + Test.errorCount + " ERRORS DETECTED !!!.\n");
            }
        }
      
        protected void configure() {
            for (int i1 = 0; i1 < TESTS.Length; i1++) {
                Object toTest1 = newInstance(TESTS[i1]);
                runMethod(toTest1, "configure");
            }
        }
      
        private void runTests() {
            String cs = Test.clientServer ? "C/S" : "SOLO";
            for (int i = 0; i < TESTS.Length; i++) {
                Console.WriteLine(cs + " testing " +  TESTS[i].Name);
                Object toTest1 = newInstance(TESTS[i]);
                Test.open();
                if(! runStoreOne(toTest1)){
                    runMethod(toTest1, "store");
                }
                Test.commit();
                Test.close();
                // connection needs some commit time
                Thread.sleep(100);
                Test.open();
                toTest1 = newInstance(TESTS[i]);
                runTestOne(toTest1);
                toTest1 = newInstance(TESTS[i]);
                Method[] methods1 = Class.getClassForType(TESTS[i]).getDeclaredMethods();
                for (int j1 = 0; j1 < methods1.Length; j1++) {
                    Method method1 = methods1[j1];
                    String methodName = method1.getName();
                    if (!methodName.Equals("testOne")) {
                        if (method1.getName().IndexOf("test") == 0) {
                            try {
                                method1.invoke(toTest1, null);
                            }  catch (Exception e) {
                                j4o.lang.JavaSystem.printStackTrace(e);
                                                     
                            }
                        }
                    }
                }
                Test.close();
            }
        }
      
        private Object newInstance(Type type) {
            Class clazz = Class.getClassForType(type);

            try { {
                      return clazz.newInstance();
                  }
            }  catch (Exception e) { {
                                     }
            }
            Console.WriteLine("Instantiation failed. Class:" + clazz.getName());
            return null;
        }
      
        private void runMethod(Object onObject, String methodName) {
            try {
                Method method1 = j4o.lang.Class.getClassForObject(onObject).getDeclaredMethod(methodName, null);
                if (method1 != null) {
                    method1.invoke(onObject, null);
                }
                  
            }  catch (Exception e) {
                Console.WriteLine(e);
            }
        }

        private bool runStoreOne(Object onObject) {
            try {
                Method method = j4o.lang.Class.getClassForObject(onObject).getDeclaredMethod("storeOne", null);
                if (method != null) {
                    Test.deleteAllInstances(onObject);
                    method.invoke(onObject, null);
                    Test.store(onObject);
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }

        private bool runTestOne(Object onObject) {
            try {
                Method method = j4o.lang.Class.getClassForObject(onObject).getDeclaredMethod("testOne", null);
                if (method != null) {
                    onObject = Test.getOne(onObject);
                    method.invoke(onObject, null);
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }


      
        protected void logConfiguration() {
            Console.WriteLine("\nRunning " + typeof(AllTests).FullName + " against\n" + Db4o.version() + "\n");
            Console.WriteLine("Using " + typeof(AllTests).BaseType.FullName + ".\n");
            Console.WriteLine("SERVER_HOSTNAME: " + SERVER_HOSTNAME);
            Console.WriteLine("SERVER_PORT: " + SERVER_PORT);
            Console.WriteLine("FILE_SERVER: " + FILE_SERVER);
            Console.WriteLine("FILE_SOLO: " + FILE_SOLO);
            Console.WriteLine("DELETE_FILE: " + DELETE_FILE);
            Console.WriteLine("BLOB_PATH: " + BLOB_PATH + "\n\n");
        }

      
    }
}
