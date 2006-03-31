/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.freespace.*;
import com.db4o.io.*;
import com.db4o.messaging.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

/**
 * Configuration template for creating new db4o files
 * 
 * @exclude
 */
public final class Config4Impl

implements Configuration, Cloneable, DeepClone, MessageSender, FreespaceConfiguration {
	private Hashtable4 _config=new Hashtable4(50);
	
	private static class BoolWrapper implements DeepClone {
		final static BoolWrapper TRUE=new BoolWrapper(true);
		final static BoolWrapper FALSE=new BoolWrapper(false);

		static BoolWrapper valueOf(boolean value) {
			return (value ? TRUE : FALSE);
		}
		
		final boolean value;
		
		BoolWrapper(boolean value) {
			this.value=value;
		}
		
		public Object deepClone(Object context) {
			return this;
		}
		
		boolean booleanValue() {
			return value;
		}
	}

	private static class IntWrapper implements DeepClone {
		final int value;
		
		IntWrapper(int value) {
			this.value=value;
		}
		
		public Object deepClone(Object context) {
			return this;
		}
		
		int intValue() {
			return value;
		}
	}

	private static class ByteWrapper implements DeepClone {
		final byte value;
		
		ByteWrapper(byte value) {
			this.value=value;
		}
		
		public Object deepClone(Object context) {
			return this;
		}
		
		byte byteValue() {
			return value;
		}
	}

	private static class StringWrapper implements DeepClone {
		final String value;
		
		StringWrapper(String value) {
			this.value=value;
		}
		
		public Object deepClone(Object context) {
			return this;
		}
		
		public String toString() {
			return value;
		}
	}

	private static class KeySpec {
		final Object defaultValue;
		
		KeySpec(byte defaultValue) {
			this(new ByteWrapper(defaultValue));
		}

		KeySpec(int defaultValue) {
			this(new IntWrapper(defaultValue));
		}

		KeySpec(boolean defaultValue) {
			this(BoolWrapper.valueOf(defaultValue));
		}

		KeySpec(String defaultValue) {
			this(new StringWrapper(defaultValue));
		}

		KeySpec(Object defaultValue) {
			this.defaultValue=defaultValue;
		}		
	}
	
	private final static KeySpec ACTIVATION_DEPTH=new KeySpec(5);
//    private int              i_activationDepth                  = 5;
	private final static KeySpec ALLOW_VERSION_UPDATES=new KeySpec(false);
//    private boolean          _allowVersionUpdates;
	private final static KeySpec AUTOMATIC_SHUTDOWN=new KeySpec(true);
//    private boolean          i_automaticShutDown                = true;
	private final static KeySpec BLOCKSIZE=new KeySpec((byte)1);
//	private byte			 i_blockSize						= 1;  // TODO: set this to 8, when implementation is done
	private final static KeySpec BLOBPATH=new KeySpec(null);
//    private String           i_blobPath;
	private final static KeySpec CALLBACKS=new KeySpec(true);
//    private boolean          i_callbacks                        = true;
	private final static KeySpec CALL_CONSTRUCTORS=new KeySpec(YapConst.DEFAULT);
//    private int			 	 i_callConstructors;
	private final static KeySpec CLASS_ACTIVATION_DEPTH_CONFIGURABLE=new KeySpec(true);
//    private boolean          i_classActivationDepthConfigurable = true;
	private final static KeySpec CLASSLOADER=new KeySpec(null);
    private ClassLoader      i_classLoader;
	private final static KeySpec DETECT_SCHEMA_CHANGES=new KeySpec(true);
//    private boolean          i_detectSchemaChanges              = true;
	private final static KeySpec DISABLE_COMMIT_RECOVERY=new KeySpec(false);
//	private boolean          i_disableCommitRecovery;
	private final static KeySpec DISCARD_FREESPACE=new KeySpec(0);
//    private int       i_discardFreeSpace;
	private final static KeySpec ENCODING=new KeySpec(YapConst.UNICODE);
//    private byte             i_encoding                         = YapConst.UNICODE;
	private final static KeySpec ENCRYPT=new KeySpec(false);
//    private boolean          i_encrypt;
	private final static KeySpec EXCEPTIONAL_CLASSES=new KeySpec(new Hashtable4(16));
//    private Hashtable4       i_exceptionalClasses               = new Hashtable4(16);
	private final static KeySpec EXCEPTIONS_ON_NOT_STORABLE=new KeySpec(false);
//    private boolean          i_exceptionsOnNotStorable;
	private final static KeySpec FLUSH_FILE_BUFFERS=new KeySpec(true);
//    private boolean   _flushFileBuffers                  = true;
	private final static KeySpec FREESPACE_SYSTEM=new KeySpec(FreespaceManager.FM_DEFAULT);
//    private byte      _freespaceSystem;                   
	private final static KeySpec GENERATE_UUIDS=new KeySpec(0);
//    private int       i_generateUUIDs;
	private final static KeySpec GENERATE_VERSION_NUMBERS=new KeySpec(0);
//    private int       i_generateVersionNumbers;
	private final static KeySpec INTERN_STRINGS=new KeySpec(false);
//    private boolean 		 i_internStrings = false;
	private final static KeySpec IS_SERVER=new KeySpec(false);
//    private boolean			 i_isServer = false;
	private final static KeySpec LOCK_FILE=new KeySpec(true);
//    private boolean          i_lockFile                         = true;
	private final static KeySpec MESSAGE_LEVEL=new KeySpec(YapConst.NONE);
//    private int              i_messageLevel                     = YapConst.NONE;
    private MessageRecipient i_messageRecipient; // XXX
    private MessageSender    i_messageSender; // XXX
	private final static KeySpec OPTIMIZE_NQ=new KeySpec(true);
//	private boolean          _optimizeNQ                        = true;
    private PrintStream      i_outStream; // XXX
	private final static KeySpec PASSWORD=new KeySpec((String)null);
//    private String           i_password;
	private final static KeySpec READ_AS=new KeySpec(new Hashtable4(16));
//    private Hashtable4       _readAs                            = new Hashtable4(16);
	private final static KeySpec READ_ONLY=new KeySpec(false);
//    private boolean          i_readonly;
    private Reflector _configuredReflector; // XXX
    private GenericReflector _reflector; // XXX
    private Collection4      i_rename; // XXX (filled from the outside with 'real' strings)
	private final static KeySpec RESERVED_STORAGE_SPACE=new KeySpec(0);
//    private int              i_reservedStorageSpace;
	private final static KeySpec SINGLE_THREADED_CLIENT=new KeySpec(false);
//    private boolean          i_singleThreadedClient;
    private YapStream        i_stream; // XXX                                                           // is null until deepClone is called
	private final static KeySpec TEST_CONSTRUCTORS=new KeySpec(true);
//    private boolean          i_testConstructors                 = true;
	private final static KeySpec TIMEOUT_CLIENT_SOCKET=new KeySpec(YapConst.CLIENT_SOCKET_TIMEOUT);
//    private int              i_timeoutClientSocket              = YapConst.CLIENT_SOCKET_TIMEOUT;
	private final static KeySpec TIMEOUT_PING_CLIENTS=new KeySpec(YapConst.CONNECTION_TIMEOUT);
//    private int              i_timeoutPingClients               = YapConst.CONNECTION_TIMEOUT;
	private final static KeySpec TIMEOUT_SERVER_SOCKET=new KeySpec(YapConst.SERVER_SOCKET_TIMEOUT);
//    private int              i_timeoutServerSocket              = YapConst.SERVER_SOCKET_TIMEOUT;
	private final static KeySpec UPDATE_DEPTH=new KeySpec(0);
//    private int              i_updateDepth;
	private final static KeySpec WEAK_REFERENCE_COLLECTION_INTERVAL=new KeySpec(1000);
//    private int              i_weakReferenceCollectionInterval  = 1000;
	private final static KeySpec WEAK_REFERENCES=new KeySpec(true);
//    private boolean          i_weakReferences                   = true;
    private IoAdapter        i_ioAdapter // XXX
    	// NOTE: activate this config to trigger the defragment failure
    	//= new NIOFileAdapter(512,3);
    	= new RandomAccessFileAdapter();
    
    private Collection4 _aliases; // strange stringwrapper cast problem - how should this get in for this key?
    
    private void put(KeySpec spec,byte value) {
    	put(spec,new ByteWrapper(value));
    }

    private void put(KeySpec spec,boolean value) {
    	put(spec,BoolWrapper.valueOf(value));
    }

    private void put(KeySpec spec,int value) {
    	put(spec,new IntWrapper(value));
    }

    private void put(KeySpec spec,String value) {
    	put(spec,new StringWrapper(value));
    }

    private void put(KeySpec spec,Object value) {
    	_config.put(spec,value);
    }

    private byte getAsByte(KeySpec spec) {
    	return ((ByteWrapper)get(spec)).byteValue();
    }

    private boolean getAsBoolean(KeySpec spec) {
    	return ((BoolWrapper)get(spec)).booleanValue();
    }

    private int getAsInt(KeySpec spec) {
    	return ((IntWrapper)get(spec)).intValue();
    }

    private String getAsString(KeySpec spec) {
    	return ((StringWrapper)get(spec)).toString();
    }

    private Object get(KeySpec spec) {
        Object value=_config.get(spec);
        return (value==null ? spec.defaultValue : value);
    }
    
    int activationDepth() {
    	return getAsInt(ACTIVATION_DEPTH);
    }

    public void activationDepth(int depth) {
    	put(ACTIVATION_DEPTH,depth);
    }
    
    public void allowVersionUpdates(boolean flag){
    	put(ALLOW_VERSION_UPDATES,flag);
    }

    public void automaticShutDown(boolean flag) {
    	put(AUTOMATIC_SHUTDOWN,flag);
    }
    
    public void blockSize(int bytes){
       if (bytes < 1 || bytes > 127) {
           Exceptions4.throwRuntimeException(1);
       }
       
       if (i_stream != null) {
           Exceptions4.throwRuntimeException(46);   // see readable message for code in Messages.java
       }
       
       put(BLOCKSIZE,(byte)bytes);
    }

    public void callbacks(boolean turnOn) {
        put(CALLBACKS,turnOn);
    }
    
    public void callConstructors(boolean flag){
        put(CALL_CONSTRUCTORS,(flag ? YapConst.YES : YapConst.NO));
    }

    public void classActivationDepthConfigurable(boolean turnOn) {
        put(CLASS_ACTIVATION_DEPTH_CONFIGURABLE,turnOn);
    }

    Config4Class configClass(String className) {
		Config4Class config = (Config4Class)exceptionalClasses().get(className);

        if (Debug.configureAllClasses) {
            if (config == null) {

                boolean skip = false;

                Class[] ignore = new Class[] { MetaClass.class,
                    MetaField.class, MetaIndex.class, P1HashElement.class,
                    P1ListElement.class, P1Object.class, P1Collection.class,

                    // XXX You may need the following for indexing tests. 

                    //                        P2HashMap.class,
                    //                        P2LinkedList.class,

                    StaticClass.class, StaticField.class

                };
                for (int i = 0; i < ignore.length; i++) {
                    if (ignore[i].getName().equals(className)) {
                        skip = true;
                        break;
                    }

                }
                if (!skip) {
                    config = (Config4Class) objectClass(className);
                }

            }
        }
        return config;
    }

    public Object deepClone(Object param) {
        Config4Impl ret = null;
        try {
            ret = (Config4Impl) this.clone();
        } catch (CloneNotSupportedException e) {
            // wont happen
        }
        ret._config=(Hashtable4)_config.deepClone(this);
        ret.i_stream = (YapStream) param;
        Hashtable4 exceptionalClasses=exceptionalClasses();
        if (exceptionalClasses != null) {
            ret.put(EXCEPTIONAL_CLASSES,exceptionalClasses.deepClone(ret));
        }
        if (i_rename != null) {
            ret.i_rename=(Collection4)i_rename.deepClone(ret);
        }
        if(_reflector != null){
        	ret._reflector = (GenericReflector)_reflector.deepClone(ret);
        }
        return ret;
    }

    public void detectSchemaChanges(boolean flag) {
        put(DETECT_SCHEMA_CHANGES,flag);
    }

    public void disableCommitRecovery() {
        put(DISABLE_COMMIT_RECOVERY,true);
    }

    public void discardFreeSpace(int bytes) {
        put(DISCARD_FREESPACE,bytes);
    }
    
    public void discardSmallerThan(int byteCount) {
        discardFreeSpace(byteCount);
    }

    public void encrypt(boolean flag) {
        globalSettingOnly();
        put(ENCRYPT,flag);
    }

    void ensureDirExists(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists() && file.isDirectory()) {
        } else {
            throw new IOException(Messages.get(37, path));
        }
    }

    PrintStream errStream() {
        return i_outStream == null ? System.err : i_outStream;
    }

    public void exceptionsOnNotStorable(boolean flag) {
        put(EXCEPTIONS_ON_NOT_STORABLE,flag);
    }
    
    public void flushFileBuffers(boolean flag){
        put(FLUSH_FILE_BUFFERS,flag);
    }

    public FreespaceConfiguration freespace() {
        return this;
    }
    
    public void generateUUIDs(int setting) {
        put(GENERATE_UUIDS,setting);
        storeStreamBootRecord();
    }
    
    private void storeStreamBootRecord() {
        if(i_stream == null){
            return;
        }
        PBootRecord bootRecord = i_stream.bootRecord();
        if(bootRecord != null) {
            bootRecord.initConfig(this);
            Transaction trans = i_stream.getSystemTransaction();
            i_stream.setInternal(trans, bootRecord, false);
            trans.commit();
        }
    }

    public void generateVersionNumbers(int setting) {
        put(GENERATE_VERSION_NUMBERS,setting);
        storeStreamBootRecord();
    }

    public MessageSender getMessageSender() {
        return this;
    }

    private void globalSettingOnly() {
        if (i_stream != null) {
            new Exception().printStackTrace();
            Exceptions4.throwRuntimeException(46);
        }
    }
    
    public void internStrings(boolean doIntern) {
    	put(INTERN_STRINGS,doIntern);
    }
    
    public void io(IoAdapter adapter){
        globalSettingOnly();
        i_ioAdapter = adapter;
    }

    public void lockDatabaseFile(boolean flag) {
    	put(LOCK_FILE,flag);
    }
    
    public void markTransient(String marker) {
        Platform4.markTransient(marker);
    }

    public void messageLevel(int level) {
    	put(MESSAGE_LEVEL,level);
        if (i_outStream == null) {
            setOut(System.out);
        }
    }

    public void optimizeNativeQueries(boolean optimizeNQ) {
    	put(OPTIMIZE_NQ,optimizeNQ);
    }
    
    public boolean optimizeNativeQueries() {
    	return getAsBoolean(OPTIMIZE_NQ);
    }
    
    public ObjectClass objectClass(Object clazz) {
        
        String className = null;
        
        if(clazz instanceof String){
            className = (String)clazz;
        }else{
            ReflectClass claxx = reflectorFor(clazz);
            if(claxx == null){
                return null;
            }
            className = claxx.getName();
        }
        
        Hashtable4 exceptionalClasses=exceptionalClasses();
        Config4Class c4c = (Config4Class) exceptionalClasses.get(className);
        if (c4c == null) {
            c4c = new Config4Class(this, className);
            exceptionalClasses.put(className, c4c);
        }
        return c4c;
    }

    PrintStream outStream() {
        return i_outStream == null ? System.out : i_outStream;
    }

    public void password(String pw) {
        globalSettingOnly();
        put(PASSWORD,pw);
    }

    public void readOnly(boolean flag) {
        globalSettingOnly();
        put(READ_ONLY,flag);
    }

	GenericReflector reflector() {
		if(_reflector == null){
			if(_configuredReflector == null){
				_configuredReflector = Platform4.createReflector(this);	
			}
            _reflector = new GenericReflector(null, _configuredReflector);
            _configuredReflector.setParent(_reflector);
		}
		if(! _reflector.hasTransaction() && i_stream != null){
			_reflector.setTransaction(i_stream.i_systemTrans);
		}
		return _reflector;
	}

	public void reflectWith(Reflector reflect) {
		
        if(i_stream != null){
        	Exceptions4.throwRuntimeException(46);   // see readable message for code in Messages.java
        }
		
        if (reflect == null) {
            throw new NullPointerException();
        }
        _configuredReflector = reflect;
		_reflector=null;
    }

    public void refreshClasses() {
        if (i_stream == null) {
            Db4o.forEachSession(new Visitor4() {

                public void visit(Object obj) {
                    YapStream ys = ((Session) obj).i_stream;
                    if (!ys.isClosed()) {
                        ys.refreshClasses();
                    }
                }
            });
        } else {
            i_stream.refreshClasses();
        }
    }

    void rename(Rename a_rename) {
        if (i_rename == null) {
            i_rename = new Collection4();
        }
        i_rename.add(a_rename);
    }

    public void reserveStorageSpace(long byteCount) {
        int reservedStorageSpace = (int) byteCount;
        if (reservedStorageSpace < 0) {
            reservedStorageSpace = 0;
        }
        put(RESERVED_STORAGE_SPACE,reservedStorageSpace);
        if (i_stream != null) {
            i_stream.reserve(reservedStorageSpace);
        }
    }

    /**
     * The ConfigImpl also is our messageSender
     */
    public void send(Object obj) {
        if (i_stream == null) {
            Db4o.forEachSession(new Visitor4() {

                public void visit(Object session) {
                    YapStream ys = ((Session) session).i_stream;
                    if (!ys.isClosed()) {
                        ys.send(session);
                    }

                }
            });
        } else {
            i_stream.send(obj);
        }
    }

    public void setBlobPath(String path) throws IOException {
        ensureDirExists(path);
        put(BLOBPATH,path);
    }

    public void setClassLoader(ClassLoader classLoader) {
    	i_classLoader=classLoader;
//        put(CLASSLOADER,classLoader);
        reflectWith(Platform4.createReflector(this));
    }

    public void setMessageRecipient(MessageRecipient messageRecipient) {
        i_messageRecipient = messageRecipient;
    }

    public void setOut(PrintStream outStream) {
        i_outStream = outStream;
        if (i_stream != null) {
            i_stream.logMsg(19, Db4o.version());
        } else {
            Messages.logMsg(Db4o.i_config, 19, Db4o.version());
        }
    }

    public void singleThreadedClient(boolean flag) {
    	put(SINGLE_THREADED_CLIENT,flag);
    }

    public void testConstructors(boolean flag) {
    	put(TEST_CONSTRUCTORS,flag);
    }

    public void timeoutClientSocket(int milliseconds) {
    	put(TIMEOUT_CLIENT_SOCKET,milliseconds);
    }

    public void timeoutPingClients(int milliseconds) {
    	put(TIMEOUT_PING_CLIENTS,milliseconds);
    }

    public void timeoutServerSocket(int milliseconds) {
    	put(TIMEOUT_SERVER_SOCKET,milliseconds);

    }

    public void unicode(boolean unicodeOn) {
    	put(ENCODING,(unicodeOn ? YapConst.UNICODE : YapConst.ISO8859));
    }

    public void updateDepth(int depth) {
    	put(UPDATE_DEPTH,depth);
    }

    public void useRamSystem() {
        put(FREESPACE_SYSTEM,FreespaceManager.FM_RAM);
    }

    public void useIndexSystem() {
        put(FREESPACE_SYSTEM,FreespaceManager.FM_IX);
    }
    
    public void weakReferenceCollectionInterval(int milliseconds) {
    	put(WEAK_REFERENCE_COLLECTION_INTERVAL,milliseconds);
    }

    public void weakReferences(boolean flag) {
    	put(WEAK_REFERENCES,flag);
    }
    
    private Collection4 aliases() {
    	if (null == _aliases) {
    		_aliases = new Collection4();
    	}
    	return _aliases;
    }
    
    public void addAlias(Alias alias) {
    	if (null == alias) throw new IllegalArgumentException("alias");
    	aliases().add(alias);
    }
    
    public String resolveAlias(String runtimeType) {

    	Collection4 aliases=aliases();
    	if (null == aliases) return runtimeType;
    	
    	Iterator4 i = aliases.iterator();
    	while (i.hasNext()) {
    		String resolved = ((Alias)i.next()).resolve(runtimeType);
    		if (null != resolved) return resolved; 
    	}
    	
    	return runtimeType;
    }
    
    ReflectClass reflectorFor(Object clazz) {
        
        clazz = Platform4.getClassForType(clazz);
        
        if(clazz instanceof ReflectClass){
            return (ReflectClass)clazz;
        }
        
        if(clazz instanceof Class){
            return reflector().forClass((Class)clazz);
        }
        
        if(clazz instanceof String){
            return reflector().forName((String)clazz);
        }
        
        return reflector().forObject(clazz);
    }

	public boolean allowVersionUpdates() {
		return getAsBoolean(ALLOW_VERSION_UPDATES);
	}

	boolean automaticShutDown() {
		return getAsBoolean(AUTOMATIC_SHUTDOWN);
	}

	byte blockSize() {
		return getAsByte(BLOCKSIZE);
	}

	String blobPath() {
		return getAsString(BLOBPATH);
	}

	boolean callbacks() {
		return getAsBoolean(CALLBACKS);
	}

	int callConstructors() {
		return getAsInt(CALL_CONSTRUCTORS);
	}

	boolean classActivationDepthConfigurable() {
		return getAsBoolean(CLASS_ACTIVATION_DEPTH_CONFIGURABLE);
	}

	ClassLoader classLoader() {
		return i_classLoader;
		//return (ClassLoader)get(CLASSLOADER);
	}

	boolean detectSchemaChanges() {
		return getAsBoolean(DETECT_SCHEMA_CHANGES);
	}

	boolean commitRecoveryDisabled() {
		return getAsBoolean(DISABLE_COMMIT_RECOVERY);
	}

	public int discardFreeSpace() {
		return getAsInt(DISCARD_FREESPACE);
	}

	byte encoding() {
		return getAsByte(ENCODING);
	}

	boolean encrypt() {
		return getAsBoolean(ENCRYPT);
	}

	Hashtable4 exceptionalClasses() {
		return (Hashtable4)get(EXCEPTIONAL_CLASSES);
	}

	boolean exceptionsOnNotStorable() {
		return getAsBoolean(EXCEPTIONS_ON_NOT_STORABLE);
	}

	public boolean flushFileBuffers() {
		return getAsBoolean(FLUSH_FILE_BUFFERS);
	}

	byte freespaceSystem() {
		return getAsByte(FREESPACE_SYSTEM);
	}

	int generateUUIDs() {
		return getAsInt(GENERATE_UUIDS);
	}

	int generateVersionNumbers() {
		return getAsInt(GENERATE_VERSION_NUMBERS);
	}

	boolean internStrings() {
		return getAsBoolean(INTERN_STRINGS);
	}
	
	void isServer(boolean flag){
		put(IS_SERVER,flag);
	}

	boolean isServer() {
		return getAsBoolean(IS_SERVER);
	}

	boolean lockFile() {
		return getAsBoolean(LOCK_FILE);
	}

	int messageLevel() {
		return getAsInt(MESSAGE_LEVEL);
	}

	MessageRecipient messageRecipient() {
		return i_messageRecipient;
	}

	MessageSender messageSender() {
		return i_messageSender;
	}

	boolean optimizeNQ() {
		return getAsBoolean(OPTIMIZE_NQ);
	}

	String password() {
		return getAsString(PASSWORD);
	}

	Hashtable4 readAs() {
		return (Hashtable4)get(READ_AS);
	}

	boolean isReadOnly() {
		return getAsBoolean(READ_ONLY);
	}

	Collection4 rename() {
		return i_rename;
	}

	int reservedStorageSpace() {
		return getAsInt(RESERVED_STORAGE_SPACE);
	}

	boolean singleThreadedClient() {
		return getAsBoolean(SINGLE_THREADED_CLIENT);
	}

	boolean testConstructors() {
		return getAsBoolean(TEST_CONSTRUCTORS);
	}

	int timeoutClientSocket() {
		return getAsInt(TIMEOUT_CLIENT_SOCKET);
	}

	int timeoutPingClients() {
		return getAsInt(TIMEOUT_PING_CLIENTS);
	}

	int timeoutServerSocket() {
		return getAsInt(TIMEOUT_SERVER_SOCKET);
	}

	int updateDepth() {
		return getAsInt(UPDATE_DEPTH);
	}

	int weakReferenceCollectionInterval() {
		return getAsInt(WEAK_REFERENCE_COLLECTION_INTERVAL);
	}

	boolean weakReferences() {
		return getAsBoolean(WEAK_REFERENCES);
	}

	IoAdapter ioAdapter() {
		return i_ioAdapter;
	}
}