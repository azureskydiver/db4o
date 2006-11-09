/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.acid;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;


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

    public int numSyncs() {
    	return writes.size();
    }
    
    public int writeVersions(String file) throws IOException {
        
        int count = 0;
        int rcount = 0;
        
        String lastFileName = file + "0";
        
        String rightFileName = file + "R" ;
        
        File4.copy(lastFileName, rightFileName);
                
        Iterator4 syncIter = writes.iterator();
        while(syncIter.moveNext()){
            
            rcount++;
            
            Collection4 writesBetweenSync = (Collection4)syncIter.current();
            
            if(CrashSimulatingTestCase.LOG){
                System.out.println("Writing file " + rightFileName + rcount );
            }
            
            RandomAccessFile rightRaf = new RandomAccessFile(rightFileName, "rw");
            Iterator4 singleForwardIter = writesBetweenSync.iterator();
            while(singleForwardIter.moveNext()){
                CrashSimulatingWrite csw = (CrashSimulatingWrite)singleForwardIter.current();
                csw.write(rightRaf);
                
                if(CrashSimulatingTestCase.LOG){
                    System.out.println(csw);
                }
                
            }
            rightRaf.close();
                        
            Iterator4 singleBackwardIter = writesBetweenSync.iterator();
            while(singleBackwardIter.moveNext()){
                count ++;
                CrashSimulatingWrite csw = (CrashSimulatingWrite)singleBackwardIter.current();
                String currentFileName = file + "W" + count;
                File4.copy(lastFileName, currentFileName);
                
                RandomAccessFile raf = new RandomAccessFile(currentFileName, "rw");
                csw.write(raf);
                raf.close();
                lastFileName = currentFileName;
            }
            File4.copy(rightFileName, rightFileName+rcount);
            lastFileName = rightFileName;
        }
        return count;
    }

}
