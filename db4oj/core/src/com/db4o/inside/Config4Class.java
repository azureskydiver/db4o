/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class Config4Class extends Config4Abstract implements ObjectClass,
    DeepClone {
    
    private final Config4Impl _configImpl;

	private final static KeySpec CALL_CONSTRUCTOR=new KeySpec(0);
	
	private final static KeySpec CLASS_INDEXED = new KeySpec(true);

	private final static KeySpec EXCEPTIONAL_FIELDS=new KeySpec(null);

	private final static KeySpec GENERATE_UUIDS=new KeySpec(0);
    
	private final static KeySpec GENERATE_VERSION_NUMBERS=new KeySpec(0);
    
    /**
     * We are running into cyclic dependancies on reading the PBootRecord
     * object, if we maintain MetaClass information there 
     */
	private final static KeySpec MAINTAIN_METACLASS=new KeySpec(true);

	private final static KeySpec MAXIMUM_ACTIVATION_DEPTH=new KeySpec(0);

	private final static KeySpec MINIMUM_ACTIVATION_DEPTH=new KeySpec(0);

	private final static KeySpec PERSIST_STATIC_FIELD_VALUES=new KeySpec(false);
    
	private final static KeySpec QUERY_ATTRIBUTE_PROVIDER=new KeySpec(null);
    
	private final static KeySpec STORE_TRANSIENT_FIELDS=new KeySpec(false);
    
	private final static KeySpec TRANSLATOR=new KeySpec(null);

	private final static KeySpec TRANSLATOR_NAME=new KeySpec((String)null);
    
	private final static KeySpec UPDATE_DEPTH=new KeySpec(0);
    
	private final static KeySpec WRITE_AS=new KeySpec((String)null);
    
    protected Config4Class(Config4Impl configuration, KeySpecHashtable4 config) {
    	super(config);
        _configImpl = configuration;
    }

	Config4Class(Config4Impl a_configuration, String a_name) {
        _configImpl = a_configuration;
        setName(a_name);
    }

    int adjustActivationDepth(int a_depth) {
        if ((cascadeOnActivate() == Const4.YES)&& a_depth < 2) {
            a_depth = 2;
        }
        if((cascadeOnActivate() == Const4.NO)  && a_depth > 1){
            a_depth = 1;
        }
        if (config().classActivationDepthConfigurable()) {
        	int minimumActivationDepth=_config.getAsInt(MINIMUM_ACTIVATION_DEPTH);
            if (minimumActivationDepth != 0) {
                if (a_depth < minimumActivationDepth) {
                    a_depth = minimumActivationDepth;
                }
            }
        	int maximumActivationDepth=_config.getAsInt(MAXIMUM_ACTIVATION_DEPTH);
            if (maximumActivationDepth != 0) {
                if (a_depth > maximumActivationDepth) {
                    a_depth = maximumActivationDepth;
                }
            }
        }
        return a_depth;
    }
    
    public void callConstructor(boolean flag){
    	putThreeValued(CALL_CONSTRUCTOR, flag);
    }

    String className() {
        return getName();
    }
    
    ReflectClass classReflector() {
    	return config().reflector().forName(getName());
    }

    public void compare(ObjectAttribute comparator) {
        _config.put(QUERY_ATTRIBUTE_PROVIDER,comparator);
    }

    Config4Field configField(String fieldName) {
    	Hashtable4 exceptionalFields=exceptionalFieldsOrNull();
        if (exceptionalFields == null) {
            return null;
        }
        return (Config4Field) exceptionalFields.get(fieldName);
    }

    public Object deepClone(Object param){
        return new Config4Class((Config4Impl)param,_config);
    }

	public void enableReplication(boolean setting) {
		generateUUIDs(setting);
		generateVersionNumbers(setting);
	}
    
    public void generateUUIDs(boolean setting) {
    	putThreeValued(GENERATE_UUIDS, setting);
    }

    public void generateVersionNumbers(boolean setting) {
    	putThreeValued(GENERATE_VERSION_NUMBERS, setting);
    }

    public ObjectTranslator getTranslator() {
    	ObjectTranslator translator=(ObjectTranslator)_config.get(TRANSLATOR);
        if (translator != null) {
        	return translator;
        }

        String translatorName=_config.getAsString(TRANSLATOR_NAME);
        if (translatorName == null) {
        	return null;
        }
        
        try {
            translator = (ObjectTranslator) config().reflector().forName(
                translatorName).newInstance();
        } catch (Throwable t) {
            if (! Deploy.csharp){
            	// TODO: why?
                try{
                    translator = (ObjectTranslator) Class.forName(translatorName).newInstance();
                    if(translator != null){
                    	translate(translator);
                        return translator;
                    }
                }catch(Throwable th){
                }
            }
            Messages.logErr(config(), 48, translatorName, null);
            translateOnDemand(null);
        }
        translate(translator);
        return translator;
    }
    
	public void indexed(boolean flag) {
		_config.put(CLASS_INDEXED, flag);
	}
	
	public boolean indexed() {
		return _config.getAsBoolean(CLASS_INDEXED);
	}

    Object instantiate(ObjectContainerBase a_stream, Object a_toTranslate) {
        return ((ObjectConstructor) _config.get(TRANSLATOR)).onInstantiate(a_stream,
            a_toTranslate);
    }

    boolean instantiates() {
        return getTranslator() instanceof ObjectConstructor;
    }

    public void maximumActivationDepth(int depth) {
    	_config.put(MAXIMUM_ACTIVATION_DEPTH,depth);
    }

    public void minimumActivationDepth(int depth) {
    	_config.put(MINIMUM_ACTIVATION_DEPTH,depth);
    }
    
    public int callConstructor() {
        if(_config.get(TRANSLATOR) != null){
            return Const4.YES;
        }
        return _config.getAsInt(CALL_CONSTRUCTOR);
    }

    private Hashtable4 exceptionalFieldsOrNull() {
    	return (Hashtable4)_config.get(EXCEPTIONAL_FIELDS);

    }
    
    private Hashtable4 exceptionalFields() {
    	Hashtable4 exceptionalFieldsCollection=exceptionalFieldsOrNull();
        if (exceptionalFieldsCollection == null) {
            exceptionalFieldsCollection = new Hashtable4(16);
            _config.put(EXCEPTIONAL_FIELDS,exceptionalFieldsCollection);
        }
        return exceptionalFieldsCollection;
    }
    
    public ObjectField objectField(String fieldName) {
    	Hashtable4 exceptionalFieldsCollection=exceptionalFields();
        Config4Field c4f = (Config4Field) exceptionalFieldsCollection.get(fieldName);
        if (c4f == null) {
            c4f = new Config4Field(this, fieldName);
            exceptionalFieldsCollection.put(fieldName, c4f);
        }
        return c4f;
    }

    public void persistStaticFieldValues() {
        _config.put(PERSIST_STATIC_FIELD_VALUES, true);
    }

    boolean queryEvaluation(String fieldName) {
    	Hashtable4 exceptionalFields=exceptionalFieldsOrNull();
        if (exceptionalFields != null) {
            Config4Field field = (Config4Field) exceptionalFields
                .get(fieldName);
            if (field != null) {
                return field.queryEvaluation();
            }
        }
        return true;
    }

    public void readAs(Object clazz) {
	   Config4Impl configRef=config();
       ReflectClass claxx = configRef.reflectorFor(clazz);
       if (claxx == null) {
           return;
       }
       _config.put(WRITE_AS,getName());
       configRef.readAs().put(getName(), claxx.getName());
   }

    public void rename(String newName) {
        config().rename(new Rename("", getName(), newName));
        setName(newName);
    }

    public void storeTransientFields(boolean flag) {
    	_config.put(STORE_TRANSIENT_FIELDS, flag);
    }

    public void translate(ObjectTranslator translator) {
        if (translator == null) {
            _config.put(TRANSLATOR_NAME, null);
        }
        _config.put(TRANSLATOR, translator);
    }

    void translateOnDemand(String a_translatorName) {
        _config.put(TRANSLATOR_NAME,a_translatorName);
    }

    public void updateDepth(int depth) {
    	_config.put(UPDATE_DEPTH, depth);
    }

	Config4Impl config() {
		return _configImpl;
	}

	int generateUUIDs() {
		return _config.getAsInt(GENERATE_UUIDS);
	}

	int generateVersionNumbers() {
		return _config.getAsInt(GENERATE_VERSION_NUMBERS);
	}

	void maintainMetaClass(boolean flag){
		_config.put(MAINTAIN_METACLASS,flag);
	}

	boolean staticFieldValuesArePersisted() {
		return _config.getAsBoolean(PERSIST_STATIC_FIELD_VALUES);
	}

	public ObjectAttribute queryAttributeProvider() {
		return (ObjectAttribute)_config.get(QUERY_ATTRIBUTE_PROVIDER);
	}

	boolean storeTransientFields() {
		return _config.getAsBoolean(STORE_TRANSIENT_FIELDS);
	}

	int updateDepth() {
		return _config.getAsInt(UPDATE_DEPTH);
	}

	String writeAs() {
		return _config.getAsString(WRITE_AS);
	}

}