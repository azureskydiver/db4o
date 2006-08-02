/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;

class Config4Field extends Config4Abstract implements ObjectField, DeepClone {
    
    private final Config4Class _configClass;
    
	private final static KeySpec FIELD_REFLECTOR=new KeySpec(null);
    
	private final static KeySpec QUERY_EVALUATION=new KeySpec(true);
    
	private final static KeySpec INDEXED=new KeySpec(YapConst.DEFAULT);
    
	private final static KeySpec METAFIELD=new KeySpec(null);
    
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

    private ReflectField fieldReflector() {
    	ReflectField fieldReflector=(ReflectField)_config.get(FIELD_REFLECTOR);
        if (fieldReflector == null) {
            try {
                fieldReflector = classConfig().classReflector().getDeclaredField(getName());
                fieldReflector.setAccessible();
                _config.put(FIELD_REFLECTOR, fieldReflector);
            } catch (Exception e) {
            }
        }
        return fieldReflector;
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
        if (!_initialized) {
            YapStream anyStream = systemTrans.stream();
            if(Tuning.fieldIndices){
	            if (anyStream.maintainsIndices()) {
	                if(Debug.indexAllFields){
	                    indexed(true);
	                }
	                if (! yapField.supportsIndex()) {
	                    indexed(false);
	                }
	                
	                boolean indexInitCalled = false;
	                
	            	YapFile stream = (YapFile)anyStream;
	                MetaField metaField = classConfig().metaClass().ensureField(systemTrans, getName());
	                _config.put(METAFIELD, metaField);
	                int indexedFlag=_config.getAsInt(INDEXED);
	                if (indexedFlag == YapConst.YES) {
                        
                        if(Debug.bTreeFieldIndex){
                            yapField.initIndex(systemTrans);
                        }
                        
	                    if (metaField.index == null) {
	                        metaField.index = new MetaIndex();
	                        stream.setInternal(systemTrans, metaField.index, YapConst.UNSPECIFIED, false);
	                        stream.setInternal(systemTrans, metaField, YapConst.UNSPECIFIED, false);
                            
                            if(Debug.oldFieldIndex){
                                yapField.initOldIndex(systemTrans, metaField.index);
                            }
                            
	                        indexInitCalled = true;
	        				if (stream.configImpl().messageLevel() > YapConst.NONE) {
	        					stream.message("creating index " + yapField.toString());
	        				}
	        				YapClass yapClassField = yapField.getParentYapClass();
                            
                            // FIXME: BTree traversal over index here.
                            
	        				long[] ids = yapClassField.getIDs();
	        				for (int i = 0; i < ids.length; i++) {
                                YapWriter writer = stream.readWriterByID(systemTrans, (int)ids[i]);
                                if(writer != null){
                                    ObjectHeader oh = new ObjectHeader(stream, writer);
                                    Object obj = oh.objectMarshaller().readIndexEntry(oh._yapClass, oh._headerAttributes, yapField, writer);
                                    yapField.addIndexEntry(systemTrans, (int)ids[i], obj);
                                    
                                }else{
                                    if(Deploy.debug){
                                        throw new RuntimeException("Unexpected null object for ID");
                                    }
                                }
                            }
	        				if(ids.length > 0){
	        				    systemTrans.commit();
	        				}
	                    }
	                }
	                if (indexedFlag == YapConst.NO) {
	                    if (metaField.index != null) {
	        				if (stream.configImpl().messageLevel() > YapConst.NONE) {
	        					stream.message("dropping index " + yapField.toString());
	        				}
	                        MetaIndex mi = metaField.index;
	                        if (mi.indexLength > 0) {
	                            stream.free(mi.indexAddress, mi.indexLength);
	                        }
	                        if (mi.patchLength > 0) {
	                            stream.free(mi.patchAddress, mi.patchLength);
	                        }
	                        stream.delete1(systemTrans, mi, false);
	                        metaField.index = null;
	                        stream.setInternal(systemTrans, metaField, YapConst.UNSPECIFIED, false);
	                    }
	                }
	                if (metaField.index != null) {
	                    if(! indexInitCalled){
	                        yapField.initOldIndex(systemTrans, metaField.index);
	                    }
	                }
	            }
            }
            _initialized = true;
        }
    }

	boolean queryEvaluation() {
		return _config.getAsBoolean(QUERY_EVALUATION);
	}


}
