/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;


public class LookingAtFileFormat {
    
    public String str;
    
    public LookingAtFileFormat(){
        
    }
    
    public LookingAtFileFormat(String str){
        this.str = str;
    }

    public static void main(String[] args) {
        new File("lff.yap").delete();
        ObjectContainer con = Db4o.openFile("lff.yap");
        LookingAtFileFormat laff = new LookingAtFileFormat();
        for (int i = 0; i < 10000; i++) {
            laff.str = "WWWWWWWWWW" + i;
            con.set(laff);
            con.commit();
        }
        con.close();
    }
}
