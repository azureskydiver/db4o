/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.semaphores;


import java.util.*;
import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

/**
 * This class demonstrates how semaphores can be used 
 * to rule out race conditions when providing exact and
 * up-to-date information about all connected clients 
 * on a server. The class also can be used to make sure
 * that only one login is possible with a give user name
 * and ipAddress combination.
 */
public class ConnectedUser {
    
    static final String SEMAPHORE_CONNECTED = "ConnectedUser_";
    static final String SEMAPHORE_LOCK_ACCESS = "ConnectedUser_Lock_";
    
    static final int TIMEOUT = 10000;  // concurrent access timeout 10 seconds
    
    String userName;
    String ipAddress;
    
    public ConnectedUser(String userName, String ipAddress){
        this.userName = userName;
        this.ipAddress = ipAddress;
    }
    
    // make sure to call this on the server before opening the database
    // to improve querying speed 
    public static void configure(){
        ObjectClass objectClass = Db4o.configure().objectClass(ConnectedUser.class); 
        objectClass.objectField("userName").indexed(true);
        objectClass.objectField("ipAddress").indexed(true);
    }
    
    // call this on the client to ensure to have a ConnectedUser record 
    // in the database file and the semaphore set
    public static void login(ObjectContainer client, String userName, String ipAddress){
        if(! client.ext().setSemaphore(SEMAPHORE_LOCK_ACCESS, TIMEOUT)){
            throw new RuntimeException("Timeout trying to get access to ConnectedUser lock");
        }
        Query q = client.query();
        q.constrain(ConnectedUser.class);
        q.descend("userName").constrain(userName);
        q.descend("ipAddress").constrain(ipAddress);
        if(q.execute().size() == 0){
            client.set(new ConnectedUser(userName, ipAddress));
            client.commit();
        }
        String connectedSemaphoreName = SEMAPHORE_CONNECTED + userName + ipAddress;
        boolean unique = client.ext().setSemaphore(connectedSemaphoreName, 0);
        client.ext().releaseSemaphore(SEMAPHORE_LOCK_ACCESS);
        if(! unique){
            throw new RuntimeException("Two clients with same userName and ipAddress");
        }
    }
    
    // here is your list of all connected users, callable on the server
    public static List connectedUsers(ObjectServer server){
        ExtObjectContainer serverObjectContainer = server.ext().objectContainer().ext();
        if(serverObjectContainer.setSemaphore(SEMAPHORE_LOCK_ACCESS, TIMEOUT)){
            throw new RuntimeException("Timeout trying to get access to ConnectedUser lock");
        }
        List list = new ArrayList();
        Query q = serverObjectContainer.query();
        q.constrain(ConnectedUser.class);
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            ConnectedUser connectedUser = (ConnectedUser)objectSet.next();
            String connectedSemaphoreName = 
                SEMAPHORE_CONNECTED + 
                connectedUser.userName + 
                connectedUser.ipAddress;
            if(serverObjectContainer.setSemaphore(connectedSemaphoreName, TIMEOUT)){
                serverObjectContainer.delete(connectedUser);
            }else{
                list.add(connectedUser);
            }
        }
        serverObjectContainer.commit();
        serverObjectContainer.releaseSemaphore(SEMAPHORE_LOCK_ACCESS);
        return list;
    }
}
