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
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda.classes.simple;
using com.db4o.test.soda.classes.wrapper.untyped;
using com.db4o.test.soda.collections;
using com.db4o.test.soda.engines.db4o;
namespace com.db4o.test.soda {

    public class SodaTestThreadedRegression : SodaTest, Runnable {
        private static Object Lock = new Object();
        private static int RUNS = Compat.compact() ? 10 : 100;
        private STClass[] classes;
        private static volatile int runningThreads;
      
        public SodaTestThreadedRegression(STClass[] classes) : base() {
            this.classes = classes;
            setSodaTestOn(classes);
        }
      
        public static void Main(String[] args) {
            testCases = 0;
            cascadeOnDelete(new STArrayListT());
            time = j4o.lang.JavaSystem.currentTimeMillis();
            engine = new STDb4o();
            engine.reset();
            engine.open();
            try {
	            startThread(new STClass[]{new STString()});
	            startThread(new STClass[]{new STInteger()});
	            startThread(new STClass[]{new STByte()});
	            startThread(new STClass[]{new STShort()});
	            startThread(new STClass[]{new STBooleanWU()});
	            startThread(new STClass[]{new STArrayListT()});
	            do {
	                try { 
	                    Thread.sleep(300);
	                }  catch (Exception e) { 
	                }
	            }while (runningThreads > 0);
	        } finally {
	        	engine.close();
	        }
        }
      
        private static void startThread(STClass[] classes) {
            for (int i1 = 0; i1 < classes.Length; i1++) {
                if (!jdkOK(classes[i1])) {
                    JavaSystem._out.println("Test case can\'t run on this JDK: " + j4o.lang.Class.getClassForObject(classes[i1]).getName());
                    return;
                }
            }
            new Thread(new SodaTestThreadedRegression(classes)).start();
        }
      
        protected override String name() {
            return "S.O.D.A. threaded test";
        }
      
        public void run() {
            String name1;
            lock (Lock) {
                runningThreads++;
                name1 = "R " + runningThreads + " ";
            }
            Thread.currentThread().setName(name1);
            for (int i1 = 0; i1 < RUNS; i1++) {
                if (!QUIET) {
                    JavaSystem._out.println(name1 + i1);
                }
                store(classes);
                engine.commit();
                test(classes);
                for (int j1 = 0; j1 < classes.Length; j1++) {
                    Query q1 = engine.query();
                    q1.constrain(j4o.lang.Class.getClassForObject(classes[j1]));
                    ObjectSet os = q1.execute();
                    while (os.hasNext()) {
                        engine.delete(os.next());
                    }
                }
            }
            lock (Lock) {
                runningThreads--;
                if (runningThreads < 1) {
                    engine.close();
                    completed();
                }
            }
        }
      
        public static void cascadeOnDelete(Object obj) {
            Db4o.configure().objectClass(j4o.lang.Class.getClassForObject(obj).getName()).cascadeOnDelete(true);
        }
    }
}