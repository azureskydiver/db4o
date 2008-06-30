package com.db4o.ibs.engine;

import com.db4o.ext.*;

public class NewObjectChange extends SlotBasedChange {
    
    private final Object _object;

	public NewObjectChange(ObjectInfo objectInfo) {
		super(objectInfo);
		_object = objectInfo.getObject();
	}
	
	public Object object()
	{
	    return _object;
	}

}
