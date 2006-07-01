/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.diagnostic;

import com.db4o.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

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
        if(_listeners == null){
            _listeners = new Collection4();
        }
        _listeners.add(listener);
    }
    
    public Object deepClone(Object context) {
        return _listeners != null
        	? new DiagnosticProcessor(new Collection4(_listeners))
        	: new DiagnosticProcessor();
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
            onDiagnostic(
                new DiagnosticMessage(name 
                    + " : This class does not contain any persistent fields.\n"
                    + "  Every class in the class hierarchy requires some overhead for the maintenance of a class index." 
                    + " Consider removing this class from the hierarchy, if it is not needed.")
            );
        }
    }

    public void nativeQueryUnoptimized(Predicate predicate) {
        String msg = " The following native query predicate could not be run optimized:\n" + predicate;
        onDiagnostic(new DiagnosticMessage(msg));
    }

    public void checkUpdateDepth(int depth) {
        if (depth > 1) {
            String msg = "Db4o.configure().updateDepth(" + depth + ")\n"
            + "  Increasing the global updateDepth to a value greater than 1 is only recommended for"
            + " testing, not for production use. If individual deep updates are needed, consider using"
            + " ExtObjectContainer#set(object, depth) and make sure to profile the performance of each call.";
            onDiagnostic(new DiagnosticMessage(msg));
        }
    }

    public void loadedFromClassIndex(YapClass yc) {
        if(isDb4oClass(yc)){
            return;
        }
        onDiagnostic(
            new DiagnosticMessage( yc.getName() 
                + " : Query candidate set could not be loaded from a field index.\n"
                + "  Consider indexing the fields that you want to query for using: \n"
                + "  Db4o.configure().objectClass([class]).objectField([fieldName]).indexed(true);" ));
    }
    
    private boolean isDb4oClass(YapClass yc){
        String name = yc.getName();
        if(name.indexOf("com.db4o.test") == 0){
            return false;
        }
        return name.indexOf("com.db4o.") == 0;
    }
    
}
