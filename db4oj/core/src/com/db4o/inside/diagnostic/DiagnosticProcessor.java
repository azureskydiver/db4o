/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.diagnostic;

import com.db4o.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public class DiagnosticProcessor implements DiagnosticConfiguration, DeepClone{
    
    private Collection4 _listeners;
    
    // TODO: shouldn't we be using the same strategy as Config4Impl? 
    private boolean _queryStatistics;
    
    public DiagnosticProcessor() {
    }
    
    private DiagnosticProcessor(Collection4 listeners, boolean queryStatistics) {
    	_listeners = listeners;
    	_queryStatistics = queryStatistics;
    }

    public void addListener(DiagnosticListener listener) {
        if(_listeners == null){
            _listeners = new Collection4();
        }
        _listeners.add(listener);
    }
    
    public void checkClassHasFields(YapClass yc){
        YapField[] fields = yc.i_fields;
        if(fields != null && fields.length == 0){
            String name = yc.getName();
            String[] ignoredPackages = new String[]{
                "java.util."
            };
            for (int i = 0; i < ignoredPackages.length; i++) {
                if (name.indexOf(ignoredPackages[i]) == 0){
                    return;
                }
            }
            if(isDb4oClass(yc)){
                return;
            }
            onDiagnostic(new ClassHasNoFields(name));
        }
    }

    public void checkUpdateDepth(int depth) {
        if (depth > 1) {
            onDiagnostic(new UpdateDepthGreaterOne(depth));
        }
    }

    public Object deepClone(Object context) {
        return new DiagnosticProcessor(cloneListeners(), _queryStatistics);
    }

	private Collection4 cloneListeners() {
		return _listeners != null
			? new Collection4(_listeners)
			: null;
	}

    public boolean enabled(){
        return _listeners != null;
    }
    
    private boolean isDb4oClass(YapClass yc){
        String name = yc.getName();
        if(name.indexOf("com.db4o.test") == 0){
            return false;
        }
        return name.indexOf("com.db4o.") == 0;
    }

    public void loadedFromClassIndex(YapClass yc) {
        if(isDb4oClass(yc)){
            return;
        }
        onDiagnostic(new LoadedFromClassIndex(yc.getName()));
    }

    public void descendIntoTranslator(YapClass parent,String fieldName) {
        onDiagnostic(new DescendIntoTranslator(parent.getName(),fieldName));
    }
    
    public void nativeQueryUnoptimized(Predicate predicate) {
        onDiagnostic(new NativeQueryNotOptimized(predicate));
    }

    private void onDiagnostic(Diagnostic d) {
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

	public void queryStatistics(boolean enabled) {
		_queryStatistics = enabled;
	}
	
	public boolean queryStatistics() {
		return _queryStatistics;
	}
}
