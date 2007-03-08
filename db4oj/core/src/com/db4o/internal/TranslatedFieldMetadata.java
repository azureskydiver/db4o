/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.ix.*;
import com.db4o.internal.marshall.*;


final class TranslatedFieldMetadata extends FieldMetadata
{
	private final ObjectTranslator i_translator;

	TranslatedFieldMetadata(ClassMetadata containingClass, ObjectTranslator translator){
	    super(containingClass, translator);
		i_translator = translator;
		ObjectContainerBase stream = containingClass.getStream();
		configure(stream.reflector().forClass(translator.storedClass()), false);
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
		return i_translator.onStore(a_trans.stream(), a_OnObject);
	}
	
	public Object getOrCreate(Transaction a_trans, Object a_OnObject) {
		return getOn(a_trans, a_OnObject);
	}

	public void instantiate(MarshallerFamily mf,  ObjectReference a_yapObject, Object a_onObject, StatefulBuffer a_bytes) throws CorruptionException{
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
	
	private void setOn(ObjectContainerBase a_stream, Object a_onObject, Object toSet){
		i_translator.onActivate(a_stream, a_onObject, toSet);
	}
	
	protected Object indexEntryFor(Object indexEntry) {
		return indexEntry;
	}
	
	protected Indexable4 indexHandler(ObjectContainerBase stream) {
		return i_handler;
	}
}
