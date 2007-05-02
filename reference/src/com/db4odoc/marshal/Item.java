/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.marshal;

public  class Item{
		
		public int _one;
		
		public long _two;
		
		public int _three;
		
		public Item(int one, long two, int three) {
			_one = one;
			_two = two;
			_three = three;
			
		}

		public Item() {
			
		}
		
		public String toString() {
			return String.format("%h, %h, %d", _one, _two, _three);
		}
		
	}
