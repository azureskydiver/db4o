/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;
import java.net.*;

/**
 * 
 */
public class CustomClassLoaderTest {

    public static void main(String[] args) throws Exception{
        
            URL url = new File("C:/Zystem/D/db4o30j/src").toURL();
            
            ClassLoader loader = new CustomClassLoader(new URL[] {url}, null);

            Thread.currentThread().setContextClassLoader(loader);

            // Unsing equals() as a convenient method to call into a
            // different ClassLoader space 
            loader.loadClass("com.db4o.test.CustomClassLoaderHelper").newInstance();
        
    }
    
    
    
        
        

    
    
}
