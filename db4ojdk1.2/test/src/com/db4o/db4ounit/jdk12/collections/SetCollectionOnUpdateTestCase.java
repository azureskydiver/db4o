package com.db4o.db4ounit.jdk12.collections;

import java.util.*;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SetCollectionOnUpdateTestCase extends AbstractDb4oTestCase {
	private static final String OLDNAME = "A";

	public static class Data {
		public String name;

		public Data(String name) {
			this.name = name;
		}
		
		public String name() {
			return name;
		}
		
		public void name(String name) {
			this.name=name;
		}
	}

	public static class DataList {
		public List list;
		
		public DataList(Data data) {
			list=new ArrayList(1);
			list.add(data);
		}

		public Data data() {
			return (Data)list.get(0);
		}
		
		public void objectOnUpdate(ObjectContainer container) {
			container.ext().set(this.list, 1);
		}
	}

    protected void store() {
		Data data=new Data(OLDNAME);
		DataList list=new DataList(data);
		db().set(list);
    }
    
    public void testUpdateAndReread() throws Exception{
		DataList list=readDataList();
		db().ext().activate(list,Integer.MAX_VALUE);
		list.data().name(OLDNAME+"X");
		db().set(list);
		db().commit();
        
        reopen();
        
		list=readDataList();
		Assert.areEqual(OLDNAME, list.data().name());
    }

	private DataList readDataList() {
		ObjectSet result = db().get(DataList.class);
		return (DataList)result.next();
	}

}
