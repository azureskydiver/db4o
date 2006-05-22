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
      
        public static void AddError(String err) {

            for (int i = 0; i < expectedErrors.Length; i++) {
                if (err.Equals(expectedErrors[i])) {
                    return;
                }
            }
            i_errors = i_errors + err + j4o.lang.JavaSystem.GetProperty("line.separator");
        }
      
        public static void Main(String[] args) {
            new j4o.io.File(file).Delete();
            if (normal) {
				j4o.lang.Thread.CurrentThread().SetName("Main Thread");
                Regression re = new Regression();
                re.Run();
            }
        }
      
        public virtual void Run() {
            openedThreads++;
            long time = j4o.lang.JavaSystem.CurrentTimeMillis();
            RTestable[] clazzes = TestClasses();
            MainLoop(clazzes);
            time = j4o.lang.JavaSystem.CurrentTimeMillis() - time;
            Console.WriteLine(CurrentThreadName() + ": " + time + " ms.");
            returnedThreads++;
            if (returnedThreads >= openedThreads) {
                if (i_errors.Length == 0) {
                    ObjectContainer con = Open();
                    int objectCount = con.Get(null).Size();
                    con.Close();
                    Console.WriteLine(profileOnly ? "Profile run completed." : "Regression Tester Passed. " + objectCount + " objects.");
                } else {
                    Console.WriteLine("!!! Regression Tester Failed. !!!");
                }
                Console.WriteLine(Db4o.Version());
                Console.WriteLine(i_errors);
            }
        }
      
        public void MainLoop(RTestable[] clazzes) {
            int run = 0;
            for (int k = 0; k < runs; k++) {
                run++;
                Console.WriteLine(CurrentThreadName() + " regression run:" + run);
                closeFile = false;
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < clazzes.Length; j++) {
                        Cycle(clazzes[j], run);
                    }
                }
                closeFile = true;
            }
            ObjectContainer con = Open();
            con.Close();
        }

    	private static string CurrentThreadName()
    	{
    		return j4o.lang.Thread.CurrentThread().GetName();
    	}

    	internal void Cycle(RTestable clazz, int a_run) {
            if (LOG_CLASS_NAMES) {
                Console.WriteLine("Testing class: " + clazz.GetType().Name);
            }
            Object obj = clazz.NewInstance();
            ObjectContainer con = Open();
            Object get = clazz.NewInstance();
            ObjectSet set = con.Get(get);
            while (set.HasNext()) {
                con.Delete(set.Next());
            }
            clazz.Set(obj, 1);
            con.Set(obj);
            con = Close(con);
            Compare(con, get, clazz, 1, 1);
            clazz.Set(get, 1);
            Compare(con, get, clazz, 1, 1);
            con = Close(con);
            for (int i = 0; i < 4; i++) {
                obj = clazz.NewInstance();
                clazz.Set(obj, 1);
                con.Set(obj);
            }
            con = Close(con);
            Compare(con, get, clazz, 1, 5);
            con = Close(con);
            set = con.Get(get);
            obj = set.Next();
            con.Delete(obj);
            con = Close(con);
            Compare(con, get, clazz, 1, 4);
            con = Close(con);
            set = con.Get(get);
            obj = set.Next();
            clazz.Set(obj, 2);
            con.Set(obj);
            con = Close(con);
            Compare(con, get, clazz, 1, 3);
            con = Close(con);
            clazz.Set(get, 2);
            Compare(con, get, clazz, 2, 1);
            con = Close(con);
            if (clazz.Ver3()) {
                set = con.Get(get);
                obj = set.Next();
                clazz.Set(obj, 3);
                con.Set(obj);
                con = Close(con);
                clazz.Set(get, 1);
                Compare(con, get, clazz, 1, 3);
                con = Close(con);
                clazz.Set(get, 3);
                Compare(con, get, clazz, 3, 1);
                con.Close();
            }
            con.Close();
        }
      
        public void Compare(ObjectContainer con, Object get, RTestable clazz, int ver, int count) {
            ObjectSet set = con.Get(get);
            if (!profileOnly) {
                set.Reset();
                if (set.Size() == count) {
                    while (set.HasNext()) {
                        Object res = set.Next();
                        clazz.Compare(con, res, ver);
                        if (deactivate) {
                            con.Deactivate(res, 1);
                            con.Activate(res, Int32.MaxValue);
                            clazz.Compare(con, res, ver);
                        }
                    }
                } else {
                    Regression.AddError(j4o.lang.Class.GetClassForObject(clazz).GetName() + ":offcount:expected" + count + ":actual:" + set.Size());
                }
            }
        }
      
        public ObjectContainer Open() {
            return OpenContainer();
        }
      
        public void Configure() {
            Configuration config = Db4o.Configure();
            config.ActivationDepth(12);
            ObjectClass oc = config.ObjectClass("com.db4o.test.DeepUpdate");
            oc.UpdateDepth(2);
            oc = config.ObjectClass("com.db4o.test.Debug");
            oc.UpdateDepth(5);
        }
      
        public ObjectContainer OpenContainer() {
            Configure();
            ObjectContainer con = Db4o.OpenFile(file);
            return con;
        }
      
        public ObjectContainer Close(ObjectContainer con) {
            if (closeFile) {
                con.Close();
                return Open();
            }
            return con;
        }
      
        internal Object NewInstance(Class a_class) {
            try {
                return a_class.NewInstance();
            }
            catch (Exception t) {}
            Console.WriteLine("NewInstance failed:" + a_class.GetName());
            return null;
        }
        static internal String[] expectedErrors = {"1e3==null:com.db4o.test.types.ArrayTypedPrivate, db4otest:oByte:", "1e0==null:com.db4o.test.types.ArrayTypedPrivate, db4otest:nByte:", "1e3==null:com.db4o.test.types.ArrayTypedPublic, db4otest:oByte:", "1e0==null:com.db4o.test.types.ArrayTypedPublic, db4otest:nByte:", "com.db4o.test.types.Empty, db4otest:offcount:expected3:actual:4", "com.db4o.test.types.Empty, db4otest:offcount:expected1:actual:4", "f1==null:com.db4o.test.types.MasterMonster, db4otest:ooo:nByte:", "1e3==null:com.db4o.test.types.MasterMonster, db4otest:ooo:oByte:", "1e0==null:com.db4o.test.types.MasterMonster, db4otest:ooo:nByte:", "com.db4o.test.types.RecursiveTypedPrivate, db4otest:offcount:expected1:actual:11", "com.db4o.test.types.RecursiveUnTypedPrivate, db4otest:offcount:expected1:actual:11", "com.db4o.test.types.RecursiveTypedPublic, db4otest:offcount:expected1:actual:11", "com.db4o.test.types.RecursiveUnTypedPublic, db4otest:offcount:expected1:actual:11", "f1==null:com.db4o.test.types.TypedPrivate, db4otest:nByte:", "f1==null:com.db4o.test.types.TypedPublic, db4otest:nByte:"};
        static internal Object[] simpleNullWrappers = {System.Convert.ToInt32(0), System.Convert.ToInt64(0), System.Convert.ToChar((char)0), System.Convert.ToDouble((double)0), System.Convert.ToSingle((float)0), System.Convert.ToBoolean(false), System.Convert.ToInt16((short)0), System.Convert.ToByte((byte)0)};
        public static int ONE = 1;
        public static int FIVE = 5;
        public static int DELETED = 4;
        public static int SAME = 3;
        public static int UPDATED = 0;
      
        public static RTestable[] AllClasses() {
            return new RTestable[]{new ArrayInObjectPrivate(), new ArrayMixedInObjectPublic(), new ArrayTypedPublic(), new BiParentTypedPrivate(), new BiParentTypedPublic(), new BiParentUnTypedPrivate(), new BiParentUnTypedPublic(), new CSharpTypes(), new DeepUpdate(), new Empty(), new InterfacePrivate(), new InterfacePublic(), new ObjectSimplePrivate(), new ObjectSimplePublic(), new RecursiveTypedPrivate(), new RecursiveTypedPublic(), new RecursiveUnTypedPrivate(), new RecursiveUnTypedPublic(), new SelfReference(), new UntypedPrivate(), new UntypedPublic()/*, new TypedPrivate()*/  };
        }
      
        public virtual RTestable[] TestClasses() {
            if (!debug) {
                return AllClasses();
            }
            return new RTestable[]{new CSharpTypes()};
        }
    }
}