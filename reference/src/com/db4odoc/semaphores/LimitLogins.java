/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.semaphores;


import java.io.*;
import com.db4o.*;

/**
 * This class demonstrates the use of semaphores to limit the
 * number of logins to a server.
 */
public class LimitLogins {
    
    static final String HOST = "localhost";
    static final int PORT = 4455;
    static final String USER = "db4o";
    static final String PASSWORD = "db4o";
    
    static final int MAXIMUM_USERS = 10; 
    
    public static ObjectContainer login(){
        
        ObjectContainer objectContainer;
        try {
            objectContainer = Db4o.openClient(HOST, PORT, USER, PASSWORD);
        } catch (IOException e) {
            return null;
        }
        
        boolean allowedToLogin = false;
        
        for (int i = 0; i < MAXIMUM_USERS; i++) {
            if(objectContainer.ext().setSemaphore("max_user_check_" + i, 0)){
                allowedToLogin = true;
                break;
            }
        }
        
        if(! allowedToLogin){
            objectContainer.close();
            return null;
        }
        
        return objectContainer;
    }
}
