/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.ObjectField;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.marshall.*;

class Config4Field extends Config4Abstract implements ObjectField, DeepClone {
    
    private final Config4Class _configClass;
    
	private final static KeySpec QUERY_EVALUATION=new KeySpec(true);
    
	private final static KeySpec INDEXED=new KeySpec(YapConst.DEFAULT);
    
    private boolean _initialized;

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

    public void initOnUp(Transaction systemTrans, YapField yapField) {
        if (_initialized) {
        	return;
        }
        _initialized = true;
        
        YapStream anyStream = systemTrans.stream();
        if (!anyStream.maintainsIndices()) {
        	return;
        }
        if(Debug.indexAllFields){
            indexed(true);
        }
        if (! yapField.supportsIndex()) {
            indexed(false);
        }
        
    	YapFile stream = (YapFile)anyStream;
        int indexedFlag=_config.getAsInt(INDEXED);        
        if (indexedFlag == YapConst.NO) {
            dropIndex(systemTrans, yapField, stream);
            return;
        }
        
        if (useExistingIndex(systemTrans, yapField)) {
        	return;
        }
        
        if (indexedFlag != YapConst.YES) {
        	return;
        }
        
        createIndex(systemTrans, yapField, stream);
    }

	private boolean useExistingIndex(Transaction systemTrans, YapField yapField) {
		
		if (MarshallerFamily.OLD_FIELD_INDEX) {
			MetaField metaField = getMetaField(systemTrans);
	        if (metaField.index == null) {
	        	return false;
	        }
	        
	        yapField.initOldIndex(systemTrans, metaField.index);
		}
		
		if (MarshallerFamily.BTREE_FIELD_INDEX){
			return yapField.getIndex(systemTrans) != null;
		}
		
        return true;
	}

	private void createIndex(Transaction systemTrans, YapField yapField, YapFile stream) {
		
		if(MarshallerFamily.BTREE_FIELD_INDEX){
            if (stream.configImpl().messageLevel() > YapConst.NONE) {
                stream.message("creating index " + yapField.toString());
            }
		    yapField.initIndex(systemTrans);
		    stream.setDirtyInSystemTransaction(yapField.getParentYapClass());
            reindex(systemTrans, yapField, stream);
		}

		if(MarshallerFamily.OLD_FIELD_INDEX) {
			MetaField metaField = getMetaField(systemTrans);
			if (metaField.index == null) {
			    metaField.index = new MetaIndex();
			    stream.setInternal(systemTrans, metaField.index, YapConst.UNSPECIFIED, false);
			    stream.setInternal(systemTrans, metaField, YapConst.UNSPECIFIED, false);
			    
			    
			    yapField.initOldIndex(systemTrans, metaField.index);
			    
				if (stream.configImpl().messageLevel() > YapConst.NONE) {
					stream.message("creating index " + yapField.toString());
				}
				reindex(systemTrans, yapField, stream);
			}
		}
	}

	private MetaField getMetaField(Transaction systemTrans) {
		return classConfig().metaClass().ensureField(systemTrans, getName());
	}

	private void dropIndex(Transaction systemTrans, YapField yapField, YapFile stream) {
        
        if(MarshallerFamily.BTREE_FIELD_INDEX){
        
        	// FIXME: Work in progress.
        	// FIXME: Work in progress.
        	// FIXME: Work in progress.
            
            BTree btree = yapField.getIndex(systemTrans);
            if(btree == null){
                return;
            }
            
            
            return;
        }
        
		final MetaField metaField = getMetaField(systemTrans);
		if (metaField.index != null) {
			if (stream.configImpl().messageLevel() > YapConst.NONE) {
				stream.message("dropping index " + yapField.toString());
			}
		    MetaIndex mi = metaField.index;
		    mi.free(stream);
		    
		    stream.delete1(systemTrans, mi, false);
		    metaField.index = null;
		    stream.setInternal(systemTrans, metaField, YapConst.UNSPECIFIED, false);
		}
	}

	private void reindex(Transaction systemTrans, YapField yapField, YapFile stream) {
		YapClass yapClass = yapField.getParentYapClass();		
		if (yapField.rebuildIndexForClass(stream, yapClass)) {
		    systemTrans.commit();
		}
	}
	
	boolean queryEvaluation() {
		return _config.getAsBoolean(QUERY_EVALUATION);
	}


}
