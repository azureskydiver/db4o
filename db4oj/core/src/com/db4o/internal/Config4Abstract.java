/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public abstract class Config4Abstract {
	protected KeySpecHashtable4 _config;

	private final static KeySpec CASCADE_ON_ACTIVATE=new KeySpec(TernaryBool.UNSPECIFIED);
    
	private final static KeySpec CASCADE_ON_DELETE=new KeySpec(TernaryBool.UNSPECIFIED);
    
	private final static KeySpec CASCADE_ON_UPDATE=new KeySpec(TernaryBool.UNSPECIFIED);

    private final static KeySpec NAME=new KeySpec(null);

	public Config4Abstract() {
		this(new KeySpecHashtable4(10));
	}
	
	protected Config4Abstract(KeySpecHashtable4 config) {
		_config=(KeySpecHashtable4)config.deepClone(this);
	}
	
	public void cascadeOnActivate(boolean flag){
		putThreeValued(CASCADE_ON_ACTIVATE,flag);
	}
	
	public void cascadeOnDelete(boolean flag){
		putThreeValued(CASCADE_ON_DELETE,flag);
	}
	
	public void cascadeOnUpdate(boolean flag){
		putThreeValued(CASCADE_ON_UPDATE,flag);
	}

	protected void putThreeValued(KeySpec spec,boolean flag) {
		_config.put(spec, TernaryBool.forBoolean(flag));
	}

	protected void putThreeValuedInt(KeySpec spec,boolean flag) {
		_config.put(spec, flag ? 1 : -1);
	}

	public TernaryBool cascadeOnActivate(){
		return cascade(CASCADE_ON_ACTIVATE);
	}
	
	public TernaryBool cascadeOnDelete(){
		return cascade(CASCADE_ON_DELETE);
	}
	
	public TernaryBool cascadeOnUpdate(){
		return cascade(CASCADE_ON_UPDATE);
	}

	private TernaryBool cascade(KeySpec spec) {
		return _config.getAsTernaryBool(spec);
	}
	
	abstract String className();

	/**
	 * Will raise an exception if argument class doesn't match this class - violates equals() contract in favor of failing fast.
	 */
	public boolean equals(Object obj){
		if(this==obj) {
			return true;
		}
		if(null==obj) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			Exceptions4.shouldNeverHappen();
		}
		return getName().equals(((Config4Abstract)obj).getName());
	}

	public int hashCode() {
		return getName().hashCode();
	}
	
	public String getName(){
		return _config.getAsString(NAME);
	}
	
	protected void setName(String name) {
		_config.put(NAME,name);
	}
}
