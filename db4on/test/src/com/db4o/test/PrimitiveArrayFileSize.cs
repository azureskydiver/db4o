/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o;

namespace com.db4o.test
{
	public class PrimitiveArrayFileSize
	{
        Object arr;
	
        public void TestSimpleLongInObject(){
            int call = 0;
            PrimitiveArrayFileSize pafs = new PrimitiveArrayFileSize();
            for (int i = 0; i < 12; i++) {
                pafs.arr = new long[100];
                Tester.Store(pafs);
                CheckFileSize(call++);
                Tester.Commit();
                CheckFileSize(call++);
            }
        }
	
        public void TestLongWrapperInObject(){
            int call = 0;
            PrimitiveArrayFileSize pafs = new PrimitiveArrayFileSize();
            for (int i = 0; i < 12; i++) {
                pafs.arr = LongWrapperArray();
                Tester.Store(pafs);
                CheckFileSize(call++);
                Tester.Commit();
                CheckFileSize(call++);
            }
        }
	
//        public void TestSimpleLongInHashMap(){
//            HashMap hm = new HashMap();
//            int call = 0;
//            for (int i = 0; i < 12; i++) {
//                long[] lll = new long[100];
//                lll[0] = 99999;
//                hm.Put("test", lll);
//                Tester.Store(hm);
//                CheckFileSize(call++);
//                Tester.Commit();
//                CheckFileSize(call++);
//            }
//        }
	
//        public void TestLongWrapperInHashMap(){
//            HashMap hm = new HashMap();
//            int call = 0;
//            for (int i = 0; i < 12; i++) {
//                hm.Put("test", LongWrapperArray());
//                Tester.Store(hm);
//                CheckFileSize(call++);
//                Tester.Commit();
//                CheckFileSize(call++);
//            }
//        }
	
	
        private long[] LongWrapperArray(){
            long[] larr = new long[100];
            for (int j = 0; j < larr.Length; j++) {
                larr[j] = j;
            }
            return larr;
        }
	
	
	
        private void CheckFileSize(int call){
            if(Tester.CanCheckFileSize()){
                int newFileLength = Tester.FileLength();
			
                // Interesting for manual tests:
                // Console.WriteLine(newFileLength);
			
                if(call == 6){
                    // consistency reached, start testing
                    jumps = 0;
                    fileLength = newFileLength;
                }else if(call > 6){
                    if(newFileLength > fileLength){
                        if(jumps < 4){
                            fileLength = newFileLength;
                            jumps ++;
                            // allow two further step in size
                            // may be necessary for commit space extension
                        }else{
                            // now we want constant behaviour
                            Tester.Error();
                        }
                    }
                }
            }
        }
	
        [Transient]
        private static int fileLength;
        [Transient]
        private static int jumps; 
	
    }
}
