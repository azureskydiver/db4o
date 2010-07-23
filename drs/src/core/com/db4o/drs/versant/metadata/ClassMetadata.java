/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.metadata;

import com.db4o.internal.*;

public class ClassMetadata {
	
	private String name;
	
	private String fullyQualifiedName;

	public ClassMetadata(String name, String fullyQualifiedName){
		this.name = name;
		this.fullyQualifiedName = fullyQualifiedName;
	}
	
	@Override
	public String toString() {
		return Reflection4.dump(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(! (obj instanceof ClassMetadata)){
			return false;
		}
		return fullyQualifiedName.equals(((ClassMetadata) obj).fullyQualifiedName);
	}
	
	@Override
	public int hashCode() {
		return fullyQualifiedName.hashCode();
	}

	public String name() {
		return name;
	}

	public String fullyQualifiedName() {
		return fullyQualifiedName;
	}

}
