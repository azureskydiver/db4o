package com.db4o.drs.test;

import java.util.*;

import com.db4o.*;

import db4ounit.*;

public class DateReplicationTestCase extends DrsTestCase {
	
	public static final class Item {
		
		public Date _date1;
		public Date _date2;
		public Date[] _dateArray;
		
		public Item(Date date1, Date date2) {
			_date1 = date1;
			_date2 = date2;
			_dateArray = new Date[] { _date1, _date2 };
		}
		
		@Override
		public boolean equals(Object obj) {
			Item other = (Item)obj;
			if (!other._date1.equals(_date1)) {
				return false;
			}
			if (!other._date2.equals(_date2)) {
				return false;
			}
			return Arrays.equals(_dateArray, other._dateArray);
		}
	}
	
	
	public void test() {
		final Item item1 = new Item(new Date(1988, 7, 4), new Date(1999, 12, 31));
		final Item item2 = new Item(new Date(1995, 7, 12), new Date(2001, 11, 8));
		
		a().provider().storeNew(item1);
		a().provider().storeNew(item2);
		a().provider().commit();
		
		replicateAll(a().provider(), b().provider());
		
		final ObjectSet found = b().provider().getStoredObjects(Item.class);
		Iterator4Assert.sameContent(new Object[] { item2, item1 }, ReplicationTestPlatform.adapt(found.iterator()));
	}

}
