/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Threading;
using j4o.lang;
using j4o.lang.reflect;
using com.db4o;
using com.db4o.config;
using j4o.io;
using com.db4o.test.types;
namespace com.db4o.test {

    public class Regression: OpenContainer {
      
        public Regression() : base() {
        }
      
        /**
         * uses a small subset of classes see method testClasses at the end of this file
         */
        private static bool debug = false;
      
        /**
         * no comparisons, used to time performance only
         */
        private static bool profileOnly = false;
      
        /**
         * number of regression runs use higher values for threads or debug runs use 1 for the normal run, since it takes 10 minutes
         */
        private static int runs = 1;
      
        /**
         * run the normal JDK1 test on all classes
         */
        private static bool normal = true;

        /**
         * output the class name currently run
         */
        private static bool LOG_CLASS_NAMES = true;
      
        public static bool deactivate = false;
        public static String file = "regression.yap";
        private static String i_errors = "";
        private static int openedThreads = 0;
        private static int returnedThreads = 0;
        private bool closeFile;
      
        public static void addError(String err) {

            for (int i = 0; i < expectedErrors.Length; i++) {
                if (err.Equals(expectedErrors[i])) {
                    return;
                }
            }
            i_errors = i_errors + err + j4o.lang.JavaSystem.getProperty("line.separator");
        }
      
        public static void Main(String[] args) {
            new j4o.io.File(file).delete();
            if (normal) {
				Compat.threadSetName(System.Threading.Thread.CurrentThread, "Main Thread");
                Regression re = new Regression();
                re.run();
            }
        }
      
        public virtual void run() {
            openedThreads++;
            long time = j4o.lang.JavaSystem.currentTimeMillis();
            RTestable[] clazzes = testClasses();
            mainLoop(clazzes);
            time = j4o.lang.JavaSystem.currentTimeMillis() - time;
            Console.WriteLine(Compat.threadGetName(System.Threading.Thread.CurrentThread) + ": " + time + " ms.");
            returnedThreads++;
            if (returnedThreads >= openedThreads) {
                if (j4o.lang.JavaSystem.getLengthOf(i_errors) == 0) {
                    ObjectContainer con = open();
                    int objectCount = con.get(null).size();
                    con.close();
                    Console.WriteLine(profileOnly ? "Profile run completed." : "Regression Test Passed. " + objectCount + " objects.");
                } else {
                    Console.WriteLine("!!! Regression Test Failed. !!!");
                }
                Console.WriteLine(Db4o.version());
                Console.WriteLine(i_errors);
            }
        }
      
        public void mainLoop(RTestable[] clazzes) {
            int run = 0;
            for (int k = 0; k < runs; k++) {
                run++;
                Console.WriteLine(Compat.threadGetName(System.Threading.Thread.CurrentThread) + " regression run:" + run);
                closeFile = false;
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < clazzes.Length; j++) {
                        cycle(clazzes[j], run);
                    }
                }
                closeFile = true;
            }
            ObjectContainer con = open();
            con.close();
        }
      
        internal void cycle(RTestable clazz, int a_run) {
            if (LOG_CLASS_NAMES) {
                Console.WriteLine("Testing class: " + clazz.GetType().Name);
            }
            Object obj = clazz.newInstance();
            ObjectContainer con = open();
            Object get = clazz.newInstance();
            ObjectSet set = con.get(get);
            while (set.hasNext()) {
                con.delete(set.next());
            }
            clazz.set(obj, 1);
            con.set(obj);
            con = close(con);
            compare(con, get, clazz, 1, 1);
            clazz.set(get, 1);
            compare(con, get, clazz, 1, 1);
            con = close(con);
            for (int i = 0; i < 4; i++) {
                obj = clazz.newInstance();
                clazz.set(obj, 1);
                con.set(obj);
            }
            con = close(con);
            compare(con, get, clazz, 1, 5);
            con = close(con);
            set = con.get(get);
            obj = set.next();
            con.delete(obj);
            con = close(con);
            compare(con, get, clazz, 1, 4);
            con = close(con);
            set = con.get(get);
            obj = set.next();
            clazz.set(obj, 2);
            con.set(obj);
            con = close(con);
            compare(con, get, clazz, 1, 3);
            con = close(con);
            clazz.set(get, 2);
            compare(con, get, clazz, 2, 1);
            con = close(con);
            if (clazz.ver3()) {
                set = con.get(get);
                obj = set.next();
                clazz.set(obj, 3);
                con.set(obj);
                con = close(con);
                clazz.set(get, 1);
                compare(con, get, clazz, 1, 3);
                con = close(con);
                clazz.set(get, 3);
                compare(con, get, clazz, 3, 1);
                con.close();
            }
            con.close();
        }
      
        public void compare(ObjectContainer con, Object get, RTestable clazz, int ver, int count) {
            ObjectSet set = con.get(get);
            if (!profileOnly) {
                set.reset();
                if (set.size() == count) {
                    while (set.hasNext()) {
                        Object res = set.next();
                        clazz.compare(con, res, ver);
                        if (deactivate) {
                            con.deactivate(res, 1);
                            con.activate(res, Int32.MaxValue);
                            clazz.compare(con, res, ver);
                        }
                    }
                } else {
                    Regression.addError(j4o.lang.Class.getClassForObject(clazz).getName() + ":offcount:expected" + count + ":actual:" + set.size());
                }
            }
        }
      
        public ObjectContainer open() {
            return openContainer();
        }
      
        public void configure() {
            Configuration config = Db4o.configure();
            config.activationDepth(12);
            ObjectClass oc = config.objectClass("com.db4o.test.DeepUpdate");
            oc.updateDepth(2);
            oc = config.objectClass("com.db4o.test.Debug");
            oc.updateDepth(5);
        }
      
        public ObjectContainer openContainer() {
            configure();
            ObjectContainer con = Db4o.openFile(file);
            return con;
        }
      
        public ObjectContainer close(ObjectContainer con) {
            if (closeFile) {
                con.close();
                return open();
            }
            return con;
        }
      
        internal Object newInstance(Class a_class) {
            try {
                return a_class.newInstance();
            }
            catch (Exception t) {}
            Console.WriteLine("NewInstance failed:" + a_class.getName());
            return null;
        }
        static internal String[] expectedErrors = {"1e3==null:com.db4o.test.types.ArrayTypedPrivate, db4otest:oByte:", "1e0==null:com.db4o.test.types.ArrayTypedPrivate, db4otest:nByte:", "1e3==null:com.db4o.test.types.ArrayTypedPublic, db4otest:oByte:", "1e0==null:com.db4o.test.types.ArrayTypedPublic, db4otest:nByte:", "com.db4o.test.types.Empty, db4otest:offcount:expected3:actual:4", "com.db4o.test.types.Empty, db4otest:offcount:expected1:actual:4", "f1==null:com.db4o.test.types.MasterMonster, db4otest:ooo:nByte:", "1e3==null:com.db4o.test.types.MasterMonster, db4otest:ooo:oByte:", "1e0==null:com.db4o.test.types.MasterMonster, db4otest:ooo:nByte:", "com.db4o.test.types.RecursiveTypedPrivate, db4otest:offcount:expected1:actual:11", "com.db4o.test.types.RecursiveUnTypedPrivate, db4otest:offcount:expected1:actual:11", "com.db4o.test.types.RecursiveTypedPublic, db4otest:offcount:expected1:actual:11", "com.db4o.test.types.RecursiveUnTypedPublic, db4otest:offcount:expected1:actual:11", "f1==null:com.db4o.test.types.TypedPrivate, db4otest:nByte:", "f1==null:com.db4o.test.types.TypedPublic, db4otest:nByte:"};
        static internal Object[] simpleNullWrappers = {System.Convert.ToInt32(0), System.Convert.ToInt64(0), System.Convert.ToChar((char)0), System.Convert.ToDouble((double)0), System.Convert.ToSingle((float)0), System.Convert.ToBoolean(false), System.Convert.ToInt16((short)0), System.Convert.ToByte((byte)0)};
        public static int ONE = 1;
        public static int FIVE = 5;
        public static int DELETED = 4;
        public static int SAME = 3;
        public static int UPDATED = 0;
      
        public static RTestable[] allClasses() {
            return new RTestable[]{new ArrayInObjectPrivate(), new ArrayMixedInObjectPublic(), new ArrayTypedPublic(), new BiParentTypedPrivate(), new BiParentTypedPublic(), new BiParentUnTypedPrivate(), new BiParentUnTypedPublic(), new CSharpTypes(), new DeepUpdate(), new Empty(), new InterfacePrivate(), new InterfacePublic(), new ObjectSimplePrivate(), new ObjectSimplePublic(), new RecursiveTypedPrivate(), new RecursiveTypedPublic(), new RecursiveUnTypedPrivate(), new RecursiveUnTypedPublic(), new SelfReference(), new UntypedPrivate(), new UntypedPublic()/*, new TypedPrivate()*/  };
        }
      
        public virtual RTestable[] testClasses() {
            if (!debug) {
                return allClasses();
            }
            return new RTestable[]{new CSharpTypes()};
        }
    }
}