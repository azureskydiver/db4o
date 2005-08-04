/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;

class Config4Class extends Config4Abstract implements ObjectClass, Cloneable,
    DeepClone {

    int			 	   i_callConstructor;
    
    Config4Impl        i_config;

    private Hashtable4 i_exceptionalFields;

    int                i_generateUUIDs;
    
    int                i_generateVersionNumbers;
    
    /**
     * We are running into cyclic dependancies on reading the PBootRecord
     * object, if we maintain MetaClass information there 
     */
    boolean            _maintainMetaClass = true;

    int                i_maximumActivationDepth;

    MetaClass          i_metaClass;

    int                i_minimumActivationDepth;

    boolean            i_persistStaticFieldValues;
    
    ObjectAttribute    i_queryAttributeProvider;
    
    boolean            i_storeTransientFields;
    
    ObjectTranslator   i_translator;

    String             i_translatorName;
    
    int                i_updateDepth;
    
    String             _writeAs;

    Config4Class(Config4Impl a_configuration, String a_name) {
        i_config = a_configuration;
        i_name = a_name;
    }

    int adjustActivationDepth(int a_depth) {
        if ((i_cascadeOnActivate == 1)&& a_depth < 2) {
            a_depth = 2;
        }
        if((i_cascadeOnActivate == -1)  && a_depth > 1){
            a_depth = 1;
        }
        if (i_config.i_classActivationDepthConfigurable) {
            if (i_minimumActivationDepth != 0) {
                if (a_depth < i_minimumActivationDepth) {
                    a_depth = i_minimumActivationDepth;
                }
            }
            if (i_maximumActivationDepth != 0) {
                if (a_depth > i_maximumActivationDepth) {
                    a_depth = i_maximumActivationDepth;
                }
            }
        }
        return a_depth;
    }
    
    public void callConstructor(boolean flag){
        i_callConstructor = flag ? YapConst.YES : YapConst.NO;
    }

    String className() {
        return getName();
    }
    
    ReflectClass classReflector() throws ClassNotFoundException {
    	return i_config.reflector().forName(i_name);
    }

    public void compare(ObjectAttribute comparator) {
        i_queryAttributeProvider = comparator;
    }

    Config4Field configField(String fieldName) {
        if (i_exceptionalFields == null) {
            return null;
        }
        return (Config4Field) i_exceptionalFields.get(fieldName);
    }

    public Object deepClone(Object param){
        Config4Class ret = null;
        try {
            ret = (Config4Class) clone();
        } catch (CloneNotSupportedException e) {
            // won't happen
        }
        ret.i_config = (Config4Impl) param;
        if (i_exceptionalFields != null) {
            ret.i_exceptionalFields = (Hashtable4) i_exceptionalFields
                .deepClone(ret);
        }
        return ret;
    }

	public void enableReplication(boolean setting) {
		generateUUIDs(setting);
		generateVersionNumbers(setting);
	}
    
    public void generateUUIDs(boolean setting) {
        i_generateUUIDs = setting ? YapConst.YES : YapConst.NO;
    }

    public void generateVersionNumbers(boolean setting) {
        i_generateVersionNumbers = setting ? YapConst.YES : YapConst.NO;
    }

    public ObjectTranslator getTranslator() {
        if (i_translator == null && i_translatorName != null) {
            try {
                i_translator = (ObjectTranslator) i_config.reflector().forName(
                    i_translatorName).newInstance();
            } catch (Throwable t) {
                if(! Deploy.csharp){
                    try{
                        i_translator = (ObjectTranslator) Class.forName(i_translatorName).newInstance();
                        if(i_translator != null){
                            return i_translator;
                        }
                    }catch(Throwable th){
                    }
                }
                Messages.logErr(i_config, 48, i_translatorName, null);
                i_translatorName = null;
            }
        }
        return i_translator;
    }

    public void initOnUp(Transaction systemTrans) {
        if (Tuning.fieldIndices) {
            YapStream stream = systemTrans.i_stream;
            if (stream.maintainsIndices()) {
                if(_maintainMetaClass){
                    i_metaClass = (MetaClass) stream.get1(systemTrans,
                        new MetaClass(i_name)).next();
                    if (i_metaClass == null) {
                        i_metaClass = new MetaClass(i_name);
                        stream.setInternal(systemTrans, i_metaClass, Integer.MAX_VALUE, false);
                    } else {
                        stream.activate1(systemTrans, i_metaClass,
                            Integer.MAX_VALUE);
                    }
                }
            }
        }
    }

    Object instantiate(YapStream a_stream, Object a_toTranslate) {
        return ((ObjectConstructor) i_translator).onInstantiate(a_stream,
            a_toTranslate);
    }

    boolean instantiates() {
        return getTranslator() instanceof ObjectConstructor;
    }

    public void maximumActivationDepth(int depth) {
        i_maximumActivationDepth = depth;
    }

    public void minimumActivationDepth(int depth) {
        i_minimumActivationDepth = depth;
    }
    
    public int callConstructor() {
        if(i_translator != null){
            return YapConst.YES;
        }
        return i_callConstructor;
    }
    
    public ObjectField objectField(String fieldName) {
        if (i_exceptionalFields == null) {
            i_exceptionalFields = new Hashtable4(16);
        }
        Config4Field c4f = (Config4Field) i_exceptionalFields.get(fieldName);
        if (c4f == null) {
            c4f = new Config4Field(this, fieldName);
            i_exceptionalFields.put(fieldName, c4f);
        }
        return c4f;
    }

    public void persistStaticFieldValues() {
        i_persistStaticFieldValues = true;
    }

    boolean queryEvaluation(String fieldName) {
        if (i_exceptionalFields != null) {
            Config4Field field = (Config4Field) i_exceptionalFields
                .get(fieldName);
            if (field != null) {
                return field.i_queryEvaluation;
            }
        }
        return true;
    }
    
   public void readAs(Object clazz) {
       ReflectClass claxx = i_config.reflectorFor(clazz);
       if (claxx == null) {
           return;
       }
       _writeAs = i_name;
       i_config._readAs.put(_writeAs, claxx.getName());
   }

    public void rename(String newName) {
        i_config.rename(new Rename("", i_name, newName));
        i_name = newName;
    }

    public void storeTransientFields(boolean flag) {
        i_storeTransientFields = flag;
    }

    public void translate(ObjectTranslator translator) {
        if (translator == null) {
            i_translatorName = null;
        }
        i_translator = translator;
    }

    void translateOnDemand(String a_translatorName) {
        i_translatorName = a_translatorName;
    }

    public void updateDepth(int depth) {
        i_updateDepth = depth;
    }

}