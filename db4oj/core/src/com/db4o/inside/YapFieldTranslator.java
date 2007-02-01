/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.inside.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.marshall.*;


final class YapFieldTranslator extends YapField
{
	private final ObjectTranslator i_translator;

	YapFieldTranslator(YapClass a_yapClass, ObjectTranslator a_translator){
	    super(a_yapClass, a_translator);
		i_translator = a_translator;
		YapStream stream = a_yapClass.getStream();
		configure(stream.reflector().forClass(a_translator.storedClass()), false);
	}
    
    public boolean canUseNullBitmap(){
        return false;
    }

	void deactivate(Transaction a_trans, Object a_onObject, int a_depth){
		if(a_depth > 0){
			cascadeActivation(a_trans, a_onObject, a_depth, false);
		}
		setOn(a_trans.stream(), a_onObject, null);
	}

	public Object getOn(Transaction a_trans, Object a_OnObject){
		try{
			return i_translator.onStore(a_trans.stream(), a_OnObject);
		}catch(Throwable t){
			return null;
		}
	}
	
	public Object getOrCreate(Transaction a_trans, Object a_OnObject) {
		return getOn(a_trans, a_OnObject);
	}

	public void instantiate(MarshallerFamily mf,  YapObject a_yapObject, Object a_onObject, StatefulBuffer a_bytes) throws CorruptionException{
		Object toSet = read(mf, a_bytes);

		// Activation of members is necessary on purpose here.
		// Classes like Hashtable need fully activated members
		// to be able to calculate hashCode()
		
		a_bytes.getStream().activate1(a_bytes.getTransaction(), toSet, a_bytes.getInstantiationDepth());

		setOn(a_bytes.getStream(), a_onObject, toSet);
	}
	
	void refresh() {
	    // do nothing
	}
	
	private void setOn(YapStream a_stream, Object a_onObject, Object toSet){
		try{
			i_translator.onActivate(a_stream, a_onObject, toSet);
		}catch(Throwable t){}
	}
	
	protected Object indexEntryFor(Object indexEntry) {
		return indexEntry;
	}
	
	protected Indexable4 indexHandler(YapStream stream) {
		return i_handler;
	}
}
