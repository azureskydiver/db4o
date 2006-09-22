/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda.classes.simple;
using com.db4o.test.soda.classes.wrapper.untyped;
using com.db4o.test.soda.collections;
using com.db4o.test.soda.engines.db4o;
namespace com.db4o.test.soda {

    public class SodaTestThreadedRegression : SodaTest, Runnable {
        private static Object Lock = new Object();
        private static int RUNS = Platform4.IsCompact() ? 10 : 100;
        private STClass[] classes;
        private static volatile int runningThreads;
      
        public SodaTestThreadedRegression(STClass[] classes) : base() {
            this.classes = classes;
            SetSodaTestOn(classes);
        }
      
        public static void Main(String[] args) {
            testCases = 0;
            CascadeOnDelete(new STArrayListT());
            time = j4o.lang.JavaSystem.CurrentTimeMillis();
            engine = new STDb4o();
            engine.Reset();
            engine.Open();
			try {
				StartThread(new STClass[]{new STString()});
				StartThread(new STClass[]{new STInteger()});
				StartThread(new STClass[]{new STByte()});
				StartThread(new STClass[]{new STShort()});
				StartThread(new STClass[]{new STBooleanWU()});
				StartThread(new STClass[]{new STArrayListT()});
				do {
					try {
						Thread.Sleep(300);
					}  catch (Exception e) {
					}
				}while (runningThreads > 0);
        	} finally {
				engine.Close();
			}
		}
      
        private static void StartThread(STClass[] classes) {
            for (int i1 = 0; i1 < classes.Length; i1++) {
                if (!JdkOK(classes[i1])) {
					System.Console.WriteLine("Tester case can\'t run on this JDK: " + j4o.lang.Class.GetClassForObject(classes[i1]).GetName());
                    return;
                }
            }
            new Thread(new SodaTestThreadedRegression(classes)).Start();
        }
      
        protected override String Name() {
            return "S.O.D.A. threaded test";
        }
      
        public void Run() {
            String name1;
            lock (Lock) {
                runningThreads++;
                name1 = "R " + runningThreads + " ";
            }
            Thread.CurrentThread().SetName(name1);
            for (int i1 = 0; i1 < RUNS; i1++) {
                if (!QUIET) {
					System.Console.WriteLine(name1 + i1);
                }
                Store(classes);
                engine.Commit();
                Test(classes);
                for (int j1 = 0; j1 < classes.Length; j1++) {
                    Query q1 = engine.Query();
                    q1.Constrain(j4o.lang.Class.GetClassForObject(classes[j1]));
                    ObjectSet os = q1.Execute();
                    while (os.HasNext()) {
                        engine.Delete(os.Next());
                    }
                }
            }
            lock (Lock) {
                runningThreads--;
                if (runningThreads < 1) {
                    engine.Close();
                    Completed();
                }
            }
        }
      
        public static void CascadeOnDelete(Object obj) {
            Db4o.Configure().ObjectClass(j4o.lang.Class.GetClassForObject(obj).GetName()).CascadeOnDelete(true);
        }
    }
}
