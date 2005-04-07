/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o;

namespace com.db4o.test
{
	public class PrimitiveArrayFileSize
	{
        Object arr;
	
        public void testSimpleLongInObject(){
            int call = 0;
            PrimitiveArrayFileSize pafs = new PrimitiveArrayFileSize();
            for (int i = 0; i < 12; i++) {
                pafs.arr = new long[100];
                Test.store(pafs);
                checkFileSize(call++);
                Test.commit();
                checkFileSize(call++);
            }
        }
	
        public void testLongWrapperInObject(){
            int call = 0;
            PrimitiveArrayFileSize pafs = new PrimitiveArrayFileSize();
            for (int i = 0; i < 12; i++) {
                pafs.arr = longWrapperArray();
                Test.store(pafs);
                checkFileSize(call++);
                Test.commit();
                checkFileSize(call++);
            }
        }
	
//        public void testSimpleLongInHashMap(){
//            HashMap hm = new HashMap();
//            int call = 0;
//            for (int i = 0; i < 12; i++) {
//                long[] lll = new long[100];
//                lll[0] = 99999;
//                hm.put("test", lll);
//                Test.store(hm);
//                checkFileSize(call++);
//                Test.commit();
//                checkFileSize(call++);
//            }
//        }
	
//        public void testLongWrapperInHashMap(){
//            HashMap hm = new HashMap();
//            int call = 0;
//            for (int i = 0; i < 12; i++) {
//                hm.put("test", longWrapperArray());
//                Test.store(hm);
//                checkFileSize(call++);
//                Test.commit();
//                checkFileSize(call++);
//            }
//        }
	
	
        private long[] longWrapperArray(){
            long[] larr = new long[100];
            for (int j = 0; j < larr.Length; j++) {
                larr[j] = j;
            }
            return larr;
        }
	
	
	
        private void checkFileSize(int call){
            if(Test.canCheckFileSize()){
                int newFileLength = Test.fileLength();
			
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
                            Test.error();
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
