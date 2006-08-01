/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


public class PersistentTimeStampIdGenerator {
    
    private boolean _dirty;
    
    private final TimeStampIdGenerator _generator = new TimeStampIdGenerator();
    
    public long next() {
        _dirty = true;
        return _generator.generate();
    }

    public void setMinimumNext(long val) {
        if(_generator.setMinimumNext(val)){
            _dirty = true;
        }
    }

    public long lastTimeStampId() {
        return _generator.last();
    }

    public boolean isDirty() {
        return _dirty;
    }

    public void setClean() {
        _dirty = false;        
    }
}
