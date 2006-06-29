/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.diagnostic;

import com.db4o.diagnostic.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class DiagnosticProcessor implements DiagnosticConfiguration, DiagnosticListener, DeepClone{
    
    private Collection4 _listeners;

    public void addListener(DiagnosticListener listener) {
        if(_listeners == null){
            _listeners = new Collection4();
            
        }
        _listeners.add(listener);
    }
    
    public Object deepClone(Object context) {
        DiagnosticProcessor ret = new DiagnosticProcessor();
        if(_listeners != null){
            Iterator4 i = _listeners.iterator();
            while(i.hasNext()){
                ret.addListener((DiagnosticListener)i.next());
            }
        }
        return ret;
    }

    public boolean enabled(){
        return _listeners != null;
    }

    public void onDiagnostic(Diagnostic d) {
        if(_listeners == null){
            return;
        }
        Iterator4 i = _listeners.iterator();
        while(i.hasNext()){
            ((DiagnosticListener)i.next()).onDiagnostic(d);
        }
    }

    public void removeAllListeners() {
        _listeners = null;
    }
    
}
