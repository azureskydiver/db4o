/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class PreparedArrayContainsComparison implements PreparedComparison {
	
	private final ArrayHandler _arrayHandler;
	
	private final PreparedComparison _preparedComparison; 
	
	public PreparedArrayContainsComparison(Context context, ArrayHandler arrayHandler, TypeHandler4 typeHandler, Object obj){
		_arrayHandler = arrayHandler;
		_preparedComparison = typeHandler.prepareComparison(context, obj);
	}

	public int compareTo(Object obj) {
		// We never expect this call
		// TODO: The callers of this class should be refactored to pass a matcher and
		//       to expect a PreparedArrayComparison.
		throw new IllegalStateException();
	}
	
    public boolean IsEqual(Object array) {
    	return isMatch(array, IntMatcher.ZERO);
    }

    public boolean isGreaterThan(Object array) {
    	return isMatch(array, IntMatcher.POSITIVE);
    }

    public boolean isSmallerThan(Object array) {
    	return isMatch(array, IntMatcher.NEGATIVE);
    }
    
    private boolean isMatch(Object array, IntMatcher matcher){
        if(array == null){
            return false;
        }
        Iterator4 i = _arrayHandler.allElements(array);
        while (i.moveNext()) {
        	if(matcher.match(_preparedComparison.compareTo(i.current()))){
        		return true;
        	}
        }
        return false;
    }

}
