/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.performance;

import java.io.*;



public class FileDeleteBenchmark {

    private static final int COUNT = 1000;
    
    public static final String FILE = "FileDeleteBenchmark.file";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            RandomAccessFile raf = new RandomAccessFile(FILE + i, "rw");
            raf.write(1);
            raf.close();
        }
        for (int i = 0; i < COUNT; i++) {
            new File(FILE + 1).delete();
        }
        
        long stop = System.currentTimeMillis();
        long duration = stop - start;
        System.out.println("Time to create and delete " + COUNT + " files:\n" + duration + "ms");
    }

}
