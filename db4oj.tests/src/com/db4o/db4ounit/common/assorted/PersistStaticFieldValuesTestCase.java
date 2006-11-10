/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class PersistStaticFieldValuesTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo /* FIXME */ {
    public static class Data {
        public static final PsfvHelper ONE = new PsfvHelper();
        public static final PsfvHelper TWO = new PsfvHelper();
        public static final PsfvHelper THREE = new PsfvHelper();

        public PsfvHelper one;
	    public PsfvHelper two;
	    public PsfvHelper three;
    }    

    protected void configure(Configuration config) {
        config.objectClass(Data.class).persistStaticFieldValues();
    }
    
    protected void store(){
        Data psfv = new Data();
        psfv.one = Data.ONE;
        psfv.two = Data.TWO;
        psfv.three = Data.THREE; 
        store(psfv);
    }
    
    public void test(){
        Data psfv = (Data)retrieveOnlyInstance(Data.class);
        Assert.areSame(Data.ONE,psfv.one);
        Assert.areSame(Data.TWO,psfv.two);
        Assert.areSame(Data.THREE,psfv.three);
    }
    
    public static class PsfvHelper{
    }
}
