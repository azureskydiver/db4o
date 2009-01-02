/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class IdentitySet4TestCase implements TestCase{
	
	public static class Item {
		
		int _id;
		
		public Item(int id){
			_id = id;
		}
		
		@Override
		public int hashCode() {
			return _id;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Item)){
				return false;
			}
			Item other = (Item) obj;
			return _id == other._id;
		}
		
	}
	
	public void testByIdentity(){
		IdentitySet4 table = new IdentitySet4(2);
		Item item1 = new Item(1);
		Assert.isFalse(table.contains(item1));
		table.add(item1);
		Assert.isTrue(table.contains(item1));
		Item item2 = new Item(2);
		Assert.isFalse(table.contains(item2));
		table.add(item2);
		Assert.isTrue(table.contains(item2));
		Assert.areEqual(2, table.size());
		int size = 0;
		Iterator4 i = table.iterator();
		while(i.moveNext()){
			size++;
		}
		Assert.areEqual(2, size);
	}
	
	


}
