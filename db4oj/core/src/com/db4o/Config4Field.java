/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.reflect.*;

class Config4Field extends Config4Abstract implements ObjectField, Cloneable, DeepClone {
    Config4Class i_class;
    ReflectField i_fieldReflector;
    boolean i_queryEvaluation = true;
    int i_indexed = 0;
    MetaField i_metaField;
    boolean i_initialized;

    Config4Field(Config4Class a_class, String a_name) {
        i_class = a_class;
        i_name = a_name;
    }

    String className() {
        return i_class.getName();
    }

    public Object deepClone(Object param) {
        Config4Field ret = null;
        try {
            ret = (Config4Field) clone();
        } catch (CloneNotSupportedException e) {
            // won't happen
        }
        ret.i_class = (Config4Class) param;
        return ret;
    }

    private ReflectField fieldReflector() {
        if (i_fieldReflector == null) {
            try {
                i_fieldReflector = i_class.classReflector().getDeclaredField(getName());
                i_fieldReflector.setAccessible();
            } catch (Exception e) {
            }
        }
        return i_fieldReflector;
    }

    public void queryEvaluation(boolean flag) {
        i_queryEvaluation = flag;
    }

    public void rename(String newName) {
        i_class.i_config.rename(new Rename(i_class.getName(), i_name, newName));
        i_name = newName;
    }

    public void indexed(boolean flag) {
        if (flag) {
            i_indexed = 1;
        } else {
            i_indexed = -1;
        }
    }

    public void initOnUp(Transaction systemTrans, YapField yapField) {
        if (!i_initialized) {
            YapStream anyStream = systemTrans.i_stream;
            if(Tuning.fieldIndices){
	            if (anyStream.maintainsIndices()) {
	                if(Debug.indexAllFields){
	                    i_indexed = 1;
	                }
	                if (! yapField.supportsIndex()) {
	                    i_indexed = -1;
	                }
	                
	                boolean indexInitCalled = false;
	                
	            	YapFile stream = (YapFile)anyStream;
	                i_metaField = i_class.i_metaClass.ensureField(systemTrans, i_name);
	                if (i_indexed == 1) {
	                    if (i_metaField.index == null) {
	                        i_metaField.index = new MetaIndex();
	                        stream.setInternal(systemTrans, i_metaField.index, YapConst.UNSPECIFIED, false);
	                        stream.setInternal(systemTrans, i_metaField, YapConst.UNSPECIFIED, false);
	                        yapField.initIndex(systemTrans, i_metaField.index);
	                        indexInitCalled = true;
	        				if (stream.i_config.i_messageLevel > YapConst.NONE) {
	        					stream.message("creating index " + yapField.toString());
	        				}
	        				YapClass yapClassField = yapField.getParentYapClass();
	        				long[] ids = yapClassField.getIDs();
	        				for (int i = 0; i < ids.length; i++) {
                                YapWriter writer = stream.readWriterByID(systemTrans, (int)ids[i]);
                                if(writer != null){
                                    Object obj = null;
                                    YapClass yapClassObject = YapClassAny.readYapClass(writer);
                                    if(yapClassObject != null){
	                                    if(yapClassObject.findOffset(writer, yapField)){
	    	                                try {
	    	                                    obj = yapField.read(writer);
	    	                                } catch (CorruptionException e) {
	    	                                    if(Deploy.debug || Debug.atHome){
	    	                                        e.printStackTrace();
	    	                                    }
	    	                                }
	                                    }
                                    }
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
	                if (i_indexed == -1) {
	                    if (i_metaField.index != null) {
	        				if (stream.i_config.i_messageLevel > YapConst.NONE) {
	        					stream.message("dropping index " + yapField.toString());
	        				}
	                        MetaIndex mi = i_metaField.index;
	                        if (mi.indexLength > 0) {
	                            stream.free(mi.indexAddress, mi.indexLength);
	                        }
	                        if (mi.patchLength > 0) {
	                            stream.free(mi.patchAddress, mi.patchLength);
	                        }
	                        stream.delete1(systemTrans, mi, false);
	                        i_metaField.index = null;
	                        stream.setInternal(systemTrans, i_metaField, YapConst.UNSPECIFIED, false);
	                    }
	                }
	                if (i_metaField.index != null) {
	                    if(! indexInitCalled){
	                        yapField.initIndex(systemTrans, i_metaField.index);
	                    }
	                }
	            }
            }
            i_initialized = true;
        }
    }

}
