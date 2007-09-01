/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.Rename;
import com.db4o.config.*;
import com.db4o.ext.Db4oException;
import com.db4o.foundation.DeepClone;
import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.KeySpec;
import com.db4o.foundation.KeySpecHashtable4;
import com.db4o.foundation.TernaryBool;
import com.db4o.reflect.ReflectClass;


/**
 * @exclude
 */
public class Config4Class extends Config4Abstract implements ObjectClass,
    DeepClone {
    
    private final Config4Impl _configImpl;

	private final static KeySpec CALL_CONSTRUCTOR=new KeySpec(TernaryBool.UNSPECIFIED);
	
	private final static KeySpec CLASS_INDEXED = new KeySpec(true);
	
	private final static KeySpec EXCEPTIONAL_FIELDS=new KeySpec(null);

	private final static KeySpec GENERATE_UUIDS=new KeySpec(false);
    
	private final static KeySpec GENERATE_VERSION_NUMBERS=new KeySpec(false);
    
    /**
     * We are running into cyclic dependancies on reading the PBootRecord
     * object, if we maintain MetaClass information there 
     */
	private final static KeySpec MAINTAIN_METACLASS=new KeySpec(true);
	
	private final static KeySpec MARSHALLER=new KeySpec(null);

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

    public int adjustActivationDepth(int depth) {
		TernaryBool cascadeOnActivate = cascadeOnActivate();
		if (cascadeOnActivate.definiteYes() && depth < 2) {
			depth = 2;
		}
		if (cascadeOnActivate.definiteNo() && depth > 1) {
			depth = 1;
		}
		if (config().classActivationDepthConfigurable()) {
			int minimumActivationDepth = minimumActivationDepth();
			if (minimumActivationDepth != 0 && depth < minimumActivationDepth) {
				depth = minimumActivationDepth;
			}
			int maximumActivationDepth = maximumActivationDepth();
			if (maximumActivationDepth != 0 && depth > maximumActivationDepth) {
				depth = maximumActivationDepth;
			}
		}
		return depth;
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
    	_config.put(GENERATE_UUIDS, setting);
    }

    public void generateVersionNumbers(boolean setting) {
    	_config.put(GENERATE_VERSION_NUMBERS, setting);
    }
    
    public ObjectTranslator getTranslator() {
    	ObjectTranslator translator = (ObjectTranslator) _config
				.get(TRANSLATOR);
		if (translator != null) {
			return translator;
		}

		String translatorName = _config.getAsString(TRANSLATOR_NAME);
		if (translatorName == null) {
			return null;
		}
		try {
			translator = newTranslatorFromReflector(translatorName);
		} catch (RuntimeException t) {
			try {
				translator = newTranslatorFromPlatform(translatorName);
			} catch (Exception e) {
				throw new Db4oException(e);
			} 
		}
		translate(translator);
        return translator;
    }


	private ObjectTranslator newTranslatorFromPlatform(String translatorName) throws InstantiationException, IllegalAccessException{
		return (ObjectTranslator) ReflectPlatform.forName(translatorName).newInstance();
	}
   
	private ObjectTranslator newTranslatorFromReflector(String translatorName) {
		return (ObjectTranslator) config().reflector().forName(
		    translatorName).newInstance();
	}
    
	public void indexed(boolean flag) {
		_config.put(CLASS_INDEXED, flag);
	}
	
	public boolean indexed() {
		return _config.getAsBoolean(CLASS_INDEXED);
	}
	
    Object instantiate(ObjectContainerBase a_stream, Object a_toTranslate) {
        return ((ObjectConstructor) _config.get(TRANSLATOR)).onInstantiate((InternalObjectContainer)a_stream, a_toTranslate);
    }

    boolean instantiates() {
        return getTranslator() instanceof ObjectConstructor;
    }
    
	public void marshallWith(ObjectMarshaller marshaller) {
    	_config.put(MARSHALLER, marshaller);
	}
	
	ObjectMarshaller getMarshaller(){
		return (ObjectMarshaller) _config.get(MARSHALLER);
	}

    public void maximumActivationDepth(int depth) {
    	_config.put(MAXIMUM_ACTIVATION_DEPTH,depth);
    }
    
    int maximumActivationDepth() {
    	return _config.getAsInt(MAXIMUM_ACTIVATION_DEPTH);
    }

    public void minimumActivationDepth(int depth) {
    	_config.put(MINIMUM_ACTIVATION_DEPTH,depth);
    }
    
    public int minimumActivationDepth() {
    	return _config.getAsInt(MINIMUM_ACTIVATION_DEPTH);
    }
    
    public TernaryBool callConstructor() {
        if(_config.get(TRANSLATOR) != null){
            return TernaryBool.YES;
        }
        return _config.getAsTernaryBool(CALL_CONSTRUCTOR);
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

	boolean generateUUIDs() {
		return _config.getAsBoolean(GENERATE_UUIDS);
	}

	boolean generateVersionNumbers() {
		return _config.getAsBoolean(GENERATE_VERSION_NUMBERS);
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