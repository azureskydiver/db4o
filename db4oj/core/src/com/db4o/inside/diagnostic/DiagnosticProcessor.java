/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.diagnostic;

import com.db4o.diagnostic.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class DiagnosticProcessor implements DiagnosticConfiguration, DiagnosticListener, DeepClone{
    
    private Collection4 _listeners;
    
    public DiagnosticProcessor() {
    }
    
    private DiagnosticProcessor(Collection4 listeners) {
    	_listeners = listeners;
    }

    public void addListener(DiagnosticListener listener) {
        if (_listeners == null) {
            _listeners = new Collection4();
        }
        _listeners.add(listener);
    }
    
    public Object deepClone(Object context) {
        return _listeners != null
        	? new DiagnosticProcessor(new Collection4(_listeners))
        	: new DiagnosticProcessor();
    }

    public boolean enabled() {
        return _listeners != null;
    }

    public void onDiagnostic(Diagnostic d) {
        if (_listeners == null) {
            return;
        }
        // TODO: does this need to be thread safe?
        Iterator4 i = _listeners.iterator();
        while (i.hasNext()) {
            ((DiagnosticListener)i.next()).onDiagnostic(d);
        }
    }

    public void removeAllListeners() {
        _listeners = null;
    }
}
