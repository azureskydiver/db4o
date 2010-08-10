/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

public class ClassMetadataAcknowledgement {
	private String _fullyQualifiedName;
	private boolean _acknowledged;

	public ClassMetadataAcknowledgement(String fullyQualifiedName, boolean acknowledged) {
		_fullyQualifiedName = fullyQualifiedName;
		_acknowledged = acknowledged;
	}
	
	public String fullyQualifiedName() {
		return _fullyQualifiedName;
	}
	
	public boolean acknowledged() {
		return _acknowledged;
	}
	
	public void acknowledged(boolean acknowledged) {
		_acknowledged = acknowledged;
	}
	
}
