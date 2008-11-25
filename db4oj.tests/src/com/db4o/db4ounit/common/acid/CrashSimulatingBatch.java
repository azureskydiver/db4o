/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.acid;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.transactionlog.*;


public class CrashSimulatingBatch {
    
    Collection4 writes = new Collection4();
    
    Collection4 currentWrite = new Collection4();
    
    public void add(String path, byte[] bytes, long offset, int length){
    	byte[] lockFileBuffer = null;
    	byte[] logFileBuffer = null;
    	if(File4.exists(FileBasedTransactionLogHandler.lockFileName(path))){
    		try {
				lockFileBuffer = readAllBytes(FileBasedTransactionLogHandler.lockFileName(path));
				logFileBuffer =  readAllBytes(FileBasedTransactionLogHandler.logFileName(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        CrashSimulatingWrite crashSimulatingWrite = new CrashSimulatingWrite(bytes, offset, length, lockFileBuffer, logFileBuffer);
		currentWrite.add(crashSimulatingWrite);
    }
    
    private byte[] readAllBytes(String fileName) throws IOException{
    	int length = (int) new File(fileName).length();
    	RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
    	byte[] buffer = new byte[length];
    	raf.read(buffer);
    	raf.close();
    	return buffer;
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
            
            if(CrashSimulatingTestCase.VERBOSE){
                System.out.println("Writing file " + rightFileName + rcount );
            }
            
            RandomAccessFile rightRaf = new RandomAccessFile(rightFileName, "rw");
            Iterator4 singleForwardIter = writesBetweenSync.iterator();
            while(singleForwardIter.moveNext()){
                CrashSimulatingWrite csw = (CrashSimulatingWrite)singleForwardIter.current();
                csw.write(rightFileName, rightRaf);
                
                if(CrashSimulatingTestCase.VERBOSE){
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
                csw.write(currentFileName, raf);
                raf.close();
                lastFileName = currentFileName;
            }
            File4.copy(rightFileName, rightFileName+rcount);
            lastFileName = rightFileName;
        }
        return count;
    }

}
