package com.db4o.replication.hibernate.metadata;

import com.db4o.foundation.*;

public class UuidLongPartSequence {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "UuidLongPartSequence";

	private long current;
    
    private transient TimeStampIdGenerator _generator;

// --------------------------- CONSTRUCTORS ---------------------------

	public UuidLongPartSequence() {
		
	}

// --------------------- GETTER / SETTER METHODS ---------------------

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return "UuidLongPartSequence{" +
				"current=" + current +
				'}';
	}
    
    public long getNext(){
        if(_generator == null){
            _generator = new TimeStampIdGenerator(current);
        }
        current = _generator.generate();
        return current;
    }
    

}
