/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.acid;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.test.lib.*;


public class CrashSimulatingBatch {
    
    Collection4 writes = new Collection4();
    Collection4 currentWrite = new Collection4();
    
    public void add(byte[] bytes, long offset, int length){
        currentWrite.add(new CrashSimulatingWrite(bytes, offset, length));
    }

    public void sync() {
        writes.add(currentWrite);
        currentWrite = new Collection4();
    }

    public int writeVersions(String file) throws IOException {
        
        int count = 0;
        
        String lastFileName = file + "0";
        
        String rightFileName = file + "R" ;
        
        new File4(lastFileName).copy(rightFileName);
        
        RandomAccessFile rightRaf = new RandomAccessFile(rightFileName, "rw");
        
        Iterator4 syncIter = writes.strictIterator();
        while(syncIter.hasNext()){
            Collection4 writesBetweenSync = (Collection4)syncIter.next();
            
            Iterator4 singleForwardIter = writesBetweenSync.strictIterator();
            while(singleForwardIter.hasNext()){
                CrashSimulatingWrite csw = (CrashSimulatingWrite)singleForwardIter.next();
                csw.write(rightRaf);
            }
            
            Iterator4 singleBackwardIter = writesBetweenSync.iterator();
            while(singleBackwardIter.hasNext()){
                count ++;
                CrashSimulatingWrite csw = (CrashSimulatingWrite)singleBackwardIter.next();
                String currentFileName = file + "W" + count;
                new File4(lastFileName).copy(currentFileName);
                
                RandomAccessFile raf = new RandomAccessFile(currentFileName, "rw");
                csw.write(raf);
                raf.close();
                lastFileName = currentFileName;
            }
            
            lastFileName = rightFileName;
            
            
        }
        return count;
    }
    
    
    

}
