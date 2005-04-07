/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.io;
using com.db4o;
using com.db4o.ext;
using com.db4o.types;
namespace com.db4o.test {

    public class ExternalBlobs {
      
        public ExternalBlobs() : base() {
        }
        static internal String BLOB_FILE_IN = "regressionBlobIn.txt";
        static internal String BLOB_FILE_OUT = "regressionBlobOut.txt";
        internal Blob blob;
      
        internal void configure() {
            try {
                Db4o.configure().setBlobPath(Test.BLOB_PATH);
            }  catch (Exception e) {
                j4o.lang.JavaSystem.printStackTrace(e);
            }
        }
      
        internal void storeOne() {
        }
      
        public void testOne() {
            if (new File(Test.BLOB_PATH).exists()) {
                try { {
                   byte[] chout1 = new byte[]{
                                                 (byte)72,
                                                 (byte)105,
                                                 (byte)32,
                                                 (byte)102,
                                                 (byte)111,
                                                 (byte)108,
                                                 (byte)107,
                                                 (byte)115                  };
                   new File(BLOB_FILE_IN).delete();
                   new File(BLOB_FILE_OUT).delete();
                   
                   RandomAccessFile fw1 = new RandomAccessFile(BLOB_FILE_IN, "rw");
                   fw1.write(chout1);
                   fw1.close();
                   blob.readFrom(new File(BLOB_FILE_IN));
                   double status1 = blob.getStatus();
                   while (status1 > Status.COMPLETED) {
                       Thread.sleep(50);
                       status1 = blob.getStatus();
                   }
                   blob.writeTo(new File(BLOB_FILE_OUT));
                   status1 = blob.getStatus();
                   while (status1 > Status.COMPLETED) {
                       Thread.sleep(50);
                       status1 = blob.getStatus();
                   }
                   File resultingFile1 = new File(BLOB_FILE_OUT);
                   Test.ensure(resultingFile1.exists());
                   if (resultingFile1.exists()) {
                       RandomAccessFile fr1 = new RandomAccessFile(BLOB_FILE_OUT, "rw");
                       byte[] chin1 = new byte[chout1.Length];
                       fr1.read(chin1);
                       for (int i1 = 0; i1 < chin1.Length; i1++) {
                           Test.ensure(chout1[i1] == chin1[i1]);
                       }
                       fr1.close();
                   }
               }
                }  catch (Exception e) { {
                   Test.ensure(false);
                   j4o.lang.JavaSystem.printStackTrace(e);
               }
                }
            }
        }
    }
}