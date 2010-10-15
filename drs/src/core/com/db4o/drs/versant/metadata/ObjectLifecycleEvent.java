/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.metadata;

public class ObjectLifecycleEvent extends VodLoidAwareObject {
	
	public static class Operations {
		
		public final int value;

		private final String _description;

		private Operations(int value, String description) {
			this.value = value;
			_description = description;
		}

		public static final Operations CREATE = new Operations(1, "create");
		
		public static final Operations UPDATE = new Operations(2, "update");
		
		public static final Operations DELETE = new Operations(3, "delete");
		
		public static Operations forValue(int value){
			switch(value){
				case 1:
					return CREATE;
				case 2:
					return UPDATE;
				case 3:
					return DELETE;
				default:
					throw new IllegalArgumentException();
			}
		}
		
		@Override
		public String toString() {
			return _description;
		}
		
	}
	
	private long classMetadataLoid;
	
	private long objectLoid;
	
	private int operation;
	
	private long timestamp;
	
	private String transactionId;
	
	public ObjectLifecycleEvent(long classMetadataLoid, long objectLoid, int operation, long timestamp, String transactionId) {
		this.classMetadataLoid = classMetadataLoid;
		this.objectLoid = objectLoid;
		this.operation = operation;
		this.timestamp = timestamp;
		this.transactionId = transactionId;
	}

	public ObjectLifecycleEvent(){
		
	}
	
	public void activate(){
		// just access one field
		timestamp();
	}
	
	public long classMetadataLoid() {
		return classMetadataLoid;
	}

	public long objectLoid() {
		return objectLoid;
	}

	public int operation() {
		return operation;
	}
	
	public long timestamp(){
		return timestamp;
	}
	
	public void timestamp(long newTimestamp){
		timestamp = newTimestamp;
	}
	
	public String transactionId(){
		return transactionId;
	}

	@Override
	public String toString() {
		return "(obj:" + objectLoid + ", " + Operations.forValue(operation) + " ,time:" + timestamp + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof ObjectLifecycleEvent) ){
			return false;
		}
		ObjectLifecycleEvent other = (ObjectLifecycleEvent) obj;
		return objectLoid == other.objectLoid && timestamp == other.timestamp && operation == other.operation;
	}

}
