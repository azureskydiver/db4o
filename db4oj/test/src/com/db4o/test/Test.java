/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.tools.*;

public class Test extends AllTests {
    
    
    private static ObjectServer objectServer;
    private static ExtObjectContainer oc;

    static AllTests currentRunner;
    static boolean clientServer = true;
    static boolean runServer = true;
    static int errorCount = 0;
    static int assertionCount = 0;
    static int run;
    
    static MemoryFile memoryFile;
    static byte[] memoryFileContent;
    
    public static final boolean COMPARE_INTERNAL_OK = false;

    public static void main(String[] args) {
        throw new RuntimeException("This class is not intended to be run. Run AllTests.java.");
    }

    public static boolean canCheckFileSize() {
        if (currentRunner != null) {
            if(!MEMORY_FILE) {
                return !clientServer || !currentRunner.REMOTE_SERVER;    
            }
        }
        return false;
    }
    
    public static void beginTesting(){
    	File file = new File(BLOB_PATH);
    	file.mkdirs();
    	if(! file.exists()){
    		try{
				Class clazz = Test.class.getSuperclass().getSuperclass();
				System.out.println("Configured BLOB_PATH setting is not available");
				System.out.println("and it can not be created. Check the static");				System.out.println("variable BLOB_PATH in " + clazz.getName() + ".\r\n\r\n");
    		}catch(Exception e){
    			
    		}
    	}
    }
    
    private static Class classOf(Object obj){
    	if(obj == null){
    		return null;
    	}
    	if(obj instanceof Class){
    		return (Class)obj;
    	}
    	return obj.getClass();
    }

    public static void close() {
        while (!oc.close());
        if(memoryFile != null) {
            memoryFileContent = memoryFile.getBytes();
        }
        oc = null;
    }

    public static void commit() {
        oc.commit();
    }
    
    public static void configureMessageLevel(){
    	Db4o.configure().messageLevel(-1);
    }
    
    public static ObjectServer currentServer(){
    	if(clientServer && runServer){
    		return objectServer;
    	}
    	return null;
    }

    public static void delete() {
        new File(FILE_SOLO).delete();
        new File(FILE_SERVER).delete();
    }

    public static void delete(Object obj) {
        objectContainer().delete(obj);
    }

    public static void deleteAllInstances(Object obj) {
        try {
            Query q = oc.query();
            q.constrain(classOf(obj));
            ObjectSet set = q.execute();
            while (set.hasNext()) {
                oc.delete(set.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void end() {
        if (oc != null) {
            while (!oc.close());
        }
        if (objectServer != null) {
            try {
            	Thread.sleep(1000);
            } catch (Exception e) {
            }
            objectServer.close();
            objectServer = null;
        }
    }

    public static boolean ensure(boolean condition) {
        assertionCount++;
        if (!condition) {
            error();
            return false;
        }
        return true;
    }

    public static void ensureOccurrences(Object obj, int count) {
        ensure(occurrences(obj) == count);
    }

    public static void error() {
        errorCount++;
        new Exception().printStackTrace();
    }

    public static int fileLength() {
        String fileName = clientServer ? FILE_SERVER : FILE_SOLO;
        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "r");
            raf.getFD().sync();
            raf.close();
            return (int) new File(fileName).length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void forEach(Object obj, Visitor4 vis) {
        ObjectContainer con = objectContainer();
        con.deactivate(obj, Integer.MAX_VALUE);
        ObjectSet set = oc.get(obj);
        while (set.hasNext()) {
            vis.visit(set.next());
        }
    }

    public static Object getOne(Object obj) {
		Query q = oc.query();
		q.constrain(classOf(obj));
		ObjectSet set = q.execute();
		if (set.size() != 1) {
			error();
		}
        return set.next();
    }
    
    public static boolean isClientServer(){
    	return currentServer() != null;
    }

    public static void log(Query q) {
        ObjectSet set = q.execute();
        while (set.hasNext()) {
            Logger.log(oc, set.next());
        }
    }

    public static void logAll() {
        ObjectSet set = oc.get(null);
        while (set.hasNext()) {
            Logger.log(oc, set.next());
        }
    }

    public static ExtObjectContainer objectContainer() {
        if (oc == null) {
            open();
        }
        return oc;
    }

    public static int occurrences(Object obj) {
        Query q = oc.query();
        q.constrain(classOf(obj));
        return q.execute().size();
    }

    public static ExtObjectContainer open() {
        if (runServer && clientServer && objectServer == null) {
            objectServer = Db4o.openServer(FILE_SERVER, SERVER_PORT);
            objectServer.grantAccess(DB4O_USER, DB4O_PASSWORD);
            objectServer.ext().configure().messageLevel(0);
        }
        if (clientServer) {
            try {
                oc = Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
                // oc = objectServer.openClient().ext();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            if(MEMORY_FILE) {
                memoryFile = new MemoryFile(memoryFileContent);
                oc = ExtDb4o.openMemoryFile(memoryFile).ext();
            }else {
                oc = Db4o.openFile(FILE_SOLO).ext();
            }
        }
        return oc;
    }

    public static Query query() {
        return objectContainer().query();
    }

    public static ObjectContainer reOpen() {
        close();
        try {
        	Thread.sleep(100);
        } catch (Exception e) {
        }
        return open();
    }
    
    public static ObjectContainer reOpenServer(){
		if(runServer && clientServer){
			close();
			objectServer.close();
			objectServer = null;
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			return open();
		}else{
			return reOpen();
		}
    }

    public static void rollBack() {
        objectContainer().rollback();
    }
    
    public static ObjectServer server(){
    	return objectServer;
    }
    

    public static void store(Object obj) {
        objectContainer().set(obj);
    }

    public static void statistics() {
        Statistics.main(new String[] { FILE_SOLO });
    }

}
