/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.staging;

import db4ounit.*;
import db4ounit.extensions.*;


public class PrimitiveWrapperArrayTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new PrimitiveWrapperArrayTestCase().runSolo();
	}
	
	protected void store() throws Exception {
		store(Item.testInstance());
	}
	
	public void test(){
		Item item = (Item) retrieveOnlyInstance(Item.class);
		Assert.areEqual(Item.testInstance(), item);
	}
	
	public static class Item{
		
		public Boolean[] _booleans;
		
		public Item(){
			
		}
		
		public Item(Boolean[] booleans){
			_booleans = booleans;
		}
		
		public static Item testInstance(){
			Boolean[] booleans = new Boolean[]{new Boolean(true), new Boolean(false), null };
			return new Item(booleans);
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Item)){
				return false;
			}
			Item other = (Item)obj;
			return areEqual(_booleans, other._booleans);
		}
		
		public static boolean areEqual(Boolean[] cmp, Boolean[] with){
			if(cmp == null){
				return with == null;
			}
			if(with == null){
				return false;
			}
			if(cmp.length != with.length){
				return false;
			}
			for (int i = 0; i < cmp.length; i++) {
				if(! areEqual(cmp[i], with[i])){
					return false;
				}
			}
			return true;
		}
		
		public static boolean areEqual(Boolean cmp, Boolean with){
			if(cmp == null){
				return with == null;
			}
			return cmp.equals(with);
		}
		
		public String toString() {
			String res = "Item: ";
			if(_booleans == null){
				return res + "[null]";
			}
			if(_booleans.length == 0){
				return res + "{ }";
			}
			res += "{ " + _booleans[0]; 
			for (int i = 1; i < _booleans.length; i++) {
				res += ", " + _booleans[i];
			}
			res += " }";
			return res;
		}
		
	}

}
