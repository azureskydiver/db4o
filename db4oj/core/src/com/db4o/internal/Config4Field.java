/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.ObjectField;
import com.db4o.foundation.*;


class Config4Field extends Config4Abstract implements ObjectField, DeepClone {
    
    private final Config4Class _configClass;
    
	private final static KeySpec QUERY_EVALUATION=new KeySpec(true);
    
	private final static KeySpec INDEXED=new KeySpec(TernaryBool.UNSPECIFIED);
    
	protected Config4Field(Config4Class a_class, KeySpecHashtable4 config) {
		super(config);
        _configClass = a_class;
	}
	
    Config4Field(Config4Class a_class, String a_name) {
        _configClass = a_class;
        setName(a_name);
    }

    private Config4Class classConfig() {
    	return _configClass;
    }
    
    String className() {
        return classConfig().getName();
    }

    public Object deepClone(Object param) {
        return new Config4Field((Config4Class)param, _config);
    }

    public void queryEvaluation(boolean flag) {
    	_config.put(QUERY_EVALUATION, flag);
    }

    public void rename(String newName) {
        classConfig().config().rename(new Rename(className(), getName(), newName));
        setName(newName);
    }

    public void indexed(boolean flag) {
    	putThreeValued(INDEXED, flag);
    }

    public void initOnUp(Transaction systemTrans, FieldMetadata yapField) {
    	
        ObjectContainerBase anyStream = systemTrans.stream();
        if (!anyStream.maintainsIndices()) {
        	return;
        }
        if(Debug.indexAllFields){
            indexed(true);
        }
        if (! yapField.supportsIndex()) {
            indexed(false);
        }
        
    	LocalObjectContainer stream = (LocalObjectContainer)anyStream;
        TernaryBool indexedFlag=_config.getAsTernaryBool(INDEXED);        
        if (indexedFlag.definiteNo()) {
            yapField.dropIndex(systemTrans);
            return;
        }
        
        if (useExistingIndex(systemTrans, yapField)) {
        	return;
        }
        
        if (!indexedFlag.definiteYes()) {
        	return;
        }
        
        createIndex(systemTrans, yapField, stream);
    }

	private boolean useExistingIndex(Transaction systemTrans, FieldMetadata yapField) {
	    return yapField.getIndex(systemTrans) != null;
	}

	private void createIndex(Transaction systemTrans, FieldMetadata yapField, LocalObjectContainer stream) {
        if (stream.configImpl().messageLevel() > Const4.NONE) {
            stream.message("creating index " + yapField.toString());
        }
	    yapField.initIndex(systemTrans);
	    stream.setDirtyInSystemTransaction(yapField.getParentYapClass());
        reindex(systemTrans, yapField, stream);
	}

	private void reindex(Transaction systemTrans, FieldMetadata yapField, LocalObjectContainer stream) {
		ClassMetadata yapClass = yapField.getParentYapClass();		
		if (yapField.rebuildIndexForClass(stream, yapClass)) {
		    systemTrans.commit();
		}
	}
	
	boolean queryEvaluation() {
		return _config.getAsBoolean(QUERY_EVALUATION);
	}


}
