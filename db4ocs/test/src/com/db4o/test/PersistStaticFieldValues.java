/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;

public class PersistStaticFieldValues extends ClientServerTestCase {
    
    public static final PsfvHelper ONE = new PsfvHelper();
    public static final PsfvHelper TWO = new PsfvHelper();
    public static final PsfvHelper THREE = new PsfvHelper();
    
    public PsfvHelper one;
    public PsfvHelper two;
    public PsfvHelper three;
    

    public void configure(Configuration config) {
        config.objectClass(PersistStaticFieldValues.class).persistStaticFieldValues();
    }
    
    public void store(ExtObjectContainer oc){
        PersistStaticFieldValues psfv = new PersistStaticFieldValues();
        psfv.one = ONE;
        psfv.two = TWO;
        psfv.three = THREE; 
        oc.set(psfv);
    }
    
    public void conc(ExtObjectContainer oc){
        PersistStaticFieldValues psfv = (PersistStaticFieldValues)com.db4o.cs.common.util.Db4oUtil.getOne(oc,PersistStaticFieldValues.class);
        Assert.areSame(ONE, psfv.one);
        Assert.areSame(TWO, psfv.two);
        Assert.areSame(THREE, psfv.three);
    }
    
    public static class PsfvHelper{
        
    }
    

}
