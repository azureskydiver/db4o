/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public class PredicateQuery implements Serializable{
    
    private final Predicate _predicate;
    
    private final transient YapStream _stream; 
    
    public PredicateQuery(YapStream stream, Predicate predicate){
        _stream = stream;
        _predicate = predicate;
    }
    
    public ObjectSet execute(){
        Query q = _stream.query();
        q.constrain(_predicate.getExtent());
        q.constrain(new PredicateEvaluation(_predicate));
        return q.execute();
    }
}
