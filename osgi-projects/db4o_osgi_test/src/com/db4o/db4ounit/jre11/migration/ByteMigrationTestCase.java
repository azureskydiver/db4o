/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.db4ounit.jre11.migration;

import db4ounit.TestRunner;

public class ByteMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Byte value;
		
		public Item() {
		}
		
		public Item(String name_, Byte value_) {
			super(name_);
			value = value_;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value_) {
			value = (Byte) value_;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Byte)value);
	}
	
	protected String getDatabaseFileName() {
		return "bytes.db4o";
	}
	
	protected Object getMinValue() {
		return new Byte(Byte.MIN_VALUE);
	}

	protected Object getMaxValue() {
		return new Byte((byte)(Byte.MAX_VALUE-1));
	}

	protected Object getOrdinaryValue() {
		return new Byte((byte)42);
	}
	
	protected Object getUpdateValue() {
		return new Byte((byte)33);
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
		// new ByteMigrationTestCase().generateFile();
		new TestRunner(ByteMigrationTestCase.class).run();
	}
}
