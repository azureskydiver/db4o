/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.diagnostic;

import com.db4o.query.*;

/**
 * Diagnostic, if Native Query can not be run optimized.
 */
public class NativeQueryNotOptimized extends DiagnosticBase{
    
    private final Predicate _predicate;
    
    public NativeQueryNotOptimized(Predicate predicate) {
        _predicate = predicate;
    }

    public Object reason() {
        return _predicate;
    }

    public String problem() {
        return "Native Query Predicate could not be run optimized";
    }

    public String solution() {
        return "This Native Query was run by instantiating all objects of the candidate class. "
        + "Consider simplifying the expression in the Native Query method. If you feel that "
        + "the Native Query processor should understand your code better, you are invited to "
        + "post yout query code to db4o forums at http://developer.db4o.com/forums";
    }

}
