/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Renaming actions are stored to the database file to make 
 * sure that they are only performed once.
 * 
 * @exclude
 * @persistent
 */
public final class Rename implements Internal4 {
	
	public static Rename forField(String className, String name, String newName) {
		return new Rename(className, name, newName);
	}
	
	public static Rename forClass(String name, String newName) {
		return new Rename("", name, newName);
	}

	public static Rename forInverseQBE(Rename ren) {
		return new Rename(ren.rClass, null, ren.rFrom);
	}

	public String rClass;
	public String rFrom;
	public String rTo;
	
	public Rename(){}
	
	private Rename(String aClass, String aFrom, String aTo){
		rClass = aClass;
		rFrom = aFrom;
		rTo = aTo;
	}

	public boolean isField() {
		return rClass.length() != 0;
	}

}
