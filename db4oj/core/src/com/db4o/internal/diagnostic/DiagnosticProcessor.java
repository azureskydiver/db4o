/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.diagnostic;

import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * @exclude
 * 
 * FIXME: remove me from the core and make me a facade over Events
 */
public class DiagnosticProcessor implements DiagnosticConfiguration, DeepClone{
    
    private Collection4 _listeners;
    
    public DiagnosticProcessor() {
    }
    
    private DiagnosticProcessor(Collection4 listeners) {
    	_listeners = listeners;
    }

    public void addListener(DiagnosticListener listener) {
        if(_listeners == null){
            _listeners = new Collection4();
        }
        _listeners.add(listener);
    }
    
    public void checkClassHasFields(ClassMetadata yc){
        FieldMetadata[] fields = yc.i_fields;
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
        return new DiagnosticProcessor(cloneListeners());
    }

	private Collection4 cloneListeners() {
		return _listeners != null
			? new Collection4(_listeners)
			: null;
	}

    public boolean enabled(){
        return _listeners != null;
    }
    
    private boolean isDb4oClass(ClassMetadata yc){
        return Platform4.isDb4oClass(yc.getName());
    }

    public void loadedFromClassIndex(ClassMetadata yc) {
        if(isDb4oClass(yc)){
            return;
        }
        onDiagnostic(new LoadedFromClassIndex(yc.getName()));
    }

    public void descendIntoTranslator(ClassMetadata parent,String fieldName) {
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
        while(i.moveNext()){
            ((DiagnosticListener)i.current()).onDiagnostic(d);
        }
    }
    
    public void removeAllListeners() {
        _listeners = null;
    }
}
