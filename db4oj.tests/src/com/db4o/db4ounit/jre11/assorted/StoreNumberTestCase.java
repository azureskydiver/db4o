/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.assorted;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class StoreNumberTestCase extends AbstractDb4oTestCase {

	private static final int NUMENTRIES = 5;

	public static class Data {
	    
		public Number _number;
		
		public Number[] _numbers;
		
		public Number[] _integers;
		
		public Object[] _objects;
		
		public Object _object;

		public Data(int value) {
			_number = new Integer(value);
            _numbers = newNumberArray(value);
            _integers = new Integer[] {new Integer(value)};
            _objects = newNumberArray(value);
            _object = newNumberArray(value);
		}

        private Number[] newNumberArray(int value) {
            return new Number[] {new Integer(value)};
        }
	}
	
	protected void store() throws Exception {
		for(int i=0;i<NUMENTRIES;i++) {
			db().store(new Data(i));
		}
	}

	public void testRetrieveAll() {
		Query query=db().query();
		query.constrain(Data.class);
		ObjectSet result=query.execute();
		Assert.areEqual(NUMENTRIES,result.size());
		while(result.hasNext()) {
			Data data=(Data)result.next();
			Assert.isNotNull(data._number);
            assertArray(data._numbers);
            assertArray(data._integers);
            assertArray(data._objects);
            assertArray((Object[]) data._object);
		}
	}

    private void assertArray(Object[] array) {
        Assert.isNotNull(array);
        Assert.areEqual(1, array.length);
        Assert.isInstanceOf(Integer.class, array[0]);
    }

	public void testQueryNumber() {
		Query query=newQuery(Data.class);
		query.descend("_number").constrain(new Integer(0));
		ObjectSet result=query.execute();
		Assert.areEqual(1,result.size());
	}
	
    public void testQueryNumberSmaller() {
        Query query=newQuery(Data.class);
        query.descend("_number").constrain(new Integer(1)).smaller();
        ObjectSet result=query.execute();
        Assert.areEqual(1,result.size());
    }
    
    public void testQueryNumberGreater() {
        Query query=newQuery(Data.class);
        query.descend("_number").constrain(new Integer(0)).greater();
        ObjectSet result=query.execute();
        Assert.areEqual(NUMENTRIES - 1,result.size());
    }

    public void testQueryArrays() {
        assertArrayQuery("_numbers");
        assertArrayQuery("_integers");
        assertArrayQuery("_objects");
        assertArrayQuery("_object");
    }

    private void assertArrayQuery(String fieldName) {
        Query query=newQuery(Data.class);
        query.descend(fieldName).constrain(new Integer(0));
        ObjectSet result=query.execute();
        Assert.areEqual(1,result.size());
    }

}
