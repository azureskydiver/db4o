/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.cs.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.freespace.*;
import com.db4o.io.*;
import com.db4o.messaging.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;


/**
 * Configuration template for creating new db4o files
 * 
 * @exclude
 */
public final class Config4Impl implements Configuration, DeepClone,
		MessageSender, FreespaceConfiguration, QueryConfiguration,
		ClientServerConfiguration {
    
	private KeySpecHashtable4 _config=new KeySpecHashtable4(50);
	
	private final static KeySpec ACTIVATION_DEPTH=new KeySpec(5);
	
	private final static KeySpec ACTIVATION_DEPTH_PROVIDER=new KeySpec(LegacyActivationDepthProvider.INSTANCE);
    
	private final static KeySpec ALLOW_VERSION_UPDATES=new KeySpec(false);

    private final static KeySpec AUTOMATIC_SHUTDOWN=new KeySpec(true);

    //  TODO: consider setting default to 8, it's more efficient with freespace.
    private final static KeySpec BLOCKSIZE=new KeySpec((byte)1);
    
	private final static KeySpec BLOBPATH=new KeySpec(null);
    
    private final static KeySpec BTREE_NODE_SIZE=new KeySpec(119);
    
    private final static KeySpec BTREE_CACHE_HEIGHT=new KeySpec(1);
    
	private final static KeySpec CALLBACKS=new KeySpec(true);
    
	private final static KeySpec CALL_CONSTRUCTORS=new KeySpec(TernaryBool.UNSPECIFIED);
	
	private final static KeySpec CONFIGURATION_ITEMS=new KeySpec(null);
    
	private final static KeySpec CLASS_ACTIVATION_DEPTH_CONFIGURABLE=new KeySpec(true);
    
	private final static KeySpec CLASSLOADER=new KeySpec(null);
    
	private final static KeySpec DETECT_SCHEMA_CHANGES=new KeySpec(true);
    
    private final static KeySpec DIAGNOSTIC=new KeySpec(new DiagnosticProcessor());
    
    private final static KeySpec DISABLE_COMMIT_RECOVERY=new KeySpec(false);
    
	private final static KeySpec DISCARD_FREESPACE=new KeySpec(0);
    
	private final static KeySpec ENCODING=new KeySpec(Const4.UNICODE);
    
	private final static KeySpec ENCRYPT=new KeySpec(false);
    
	private final static KeySpec EXCEPTIONAL_CLASSES=new KeySpec(null);
    
	private final static KeySpec EXCEPTIONS_ON_NOT_STORABLE=new KeySpec(false);
    
	private final static KeySpec FLUSH_FILE_BUFFERS=new KeySpec(true);
    
	private final static KeySpec FREESPACE_FILLER=new KeySpec(null);

	private final static KeySpec FREESPACE_SYSTEM=new KeySpec(AbstractFreespaceManager.FM_DEFAULT);
    
	private final static KeySpec GENERATE_UUIDS=new KeySpec(ConfigScope.INDIVIDUALLY);
    
	private final static KeySpec GENERATE_VERSION_NUMBERS=new KeySpec(ConfigScope.INDIVIDUALLY);
	
	private final static KeySpec IS_SERVER=new KeySpec(false);
    
	private final static KeySpec QUERY_EVALUATION_MODE=new KeySpec(QueryEvaluationMode.IMMEDIATE);
	
	private final static KeySpec LOCK_FILE=new KeySpec(true);
    
	private final static KeySpec MESSAGE_RECIPIENT=new KeySpec(null);
    
	private final static KeySpec OPTIMIZE_NQ=new KeySpec(true);
    
	private final static KeySpec OUTSTREAM=new KeySpec(null);
    
	private final static KeySpec PASSWORD=new KeySpec((String)null);
	
	// for playing with different strategies of prefetching
	// object
	private static final KeySpec CLIENT_QUERY_RESULT_ITERATOR_FACTORY=new KeySpec(null);
    
	private static final KeySpec PREFETCH_ID_COUNT = new KeySpec(10);

	private static final KeySpec PREFETCH_OBJECT_COUNT = new KeySpec(10);
	
	private final static KeySpec READ_AS=new KeySpec(new Hashtable4(16));
    
	private final static KeySpec CONFIGURED_REFLECTOR=new KeySpec(null);
    
	private final static KeySpec REFLECTOR=new KeySpec(null);
    
	private final static KeySpec RENAME=new KeySpec(null);
    
	private final static KeySpec RESERVED_STORAGE_SPACE=new KeySpec(0);
    
	private final static KeySpec SINGLE_THREADED_CLIENT=new KeySpec(false);
    
	private final static KeySpec TEST_CONSTRUCTORS=new KeySpec(true);
    
	private final static KeySpec TIMEOUT_CLIENT_SOCKET=new KeySpec(Const4.CLIENT_SOCKET_TIMEOUT);
    
	private final static KeySpec TIMEOUT_SERVER_SOCKET=new KeySpec(Const4.SERVER_SOCKET_TIMEOUT);
    
	private final static KeySpec UPDATE_DEPTH=new KeySpec(0);
    
	private final static KeySpec WEAK_REFERENCE_COLLECTION_INTERVAL=new KeySpec(1000);
    
	private final static KeySpec WEAK_REFERENCES=new KeySpec(true);
    
	private final static KeySpec IOADAPTER=new KeySpec(new CachedIoAdapter(new RandomAccessFileAdapter()));
	
//	private final static KeySpec IOADAPTER=new KeySpec(new RandomAccessFileAdapter());
    
    	// NOTE: activate this config to trigger the defragment failure
    	//= new NIOFileAdapter(512,3);
    
	private final static KeySpec ALIASES=new KeySpec(null);
	
	private final static KeySpec BATCH_MESSAGES=new KeySpec(true);
	
	private static final KeySpec MAX_BATCH_QUEUE_SIZE = new KeySpec(Integer.MAX_VALUE);

	//  is null in the global configuration until deepClone is called
	private ObjectContainerBase        i_stream;
	
	// The following are very frequently being asked for, so they show up in the profiler. 
	// Let's keep them out of the Hashtable.
	private boolean _internStrings;
	private int _messageLevel;
	private boolean	_readOnly;

    public int activationDepth() {
    	return _config.getAsInt(ACTIVATION_DEPTH);
    }

    public void activationDepth(int depth) {
    	_config.put(ACTIVATION_DEPTH,depth);
    }
    
	public void add(ConfigurationItem item) {
		item.prepare(this);
		safeConfigurationItems().put(item, item);
	}

	private Hashtable4 safeConfigurationItems() {
		Hashtable4 items = configurationItems();
		if(items==null) {
			items=new Hashtable4(16);
			_config.put(CONFIGURATION_ITEMS,items);
		}
		return items;
	}
	
    public void allowVersionUpdates(boolean flag){
    	_config.put(ALLOW_VERSION_UPDATES,flag);
    }
    
    private Hashtable4 configurationItems(){
    	return (Hashtable4)_config.get(CONFIGURATION_ITEMS);
    }
    
	public void applyConfigurationItems(final InternalObjectContainer container) {
		Hashtable4 items = configurationItems();
		if(items == null){
			return;
		}
		Iterator4 i = items.iterator();
		while(i.moveNext()){
			Entry4 entry = (Entry4) i.current();
			ConfigurationItem item = (ConfigurationItem) entry.value();
			item.apply(container);
		}
	}

    public void automaticShutDown(boolean flag) {
    	_config.put(AUTOMATIC_SHUTDOWN,flag);
    }
    
    public void blockSize(int bytes){
       if (bytes < 1 || bytes > 127) {
           throw new IllegalArgumentException();
       } 
       globalSettingOnly();       
       _config.put(BLOCKSIZE,(byte)bytes);
    }
    
    public void bTreeNodeSize(int size){
        _config.put(BTREE_NODE_SIZE,size);
    }
    
    public void bTreeCacheHeight(int height){
        _config.put(BTREE_CACHE_HEIGHT,height);
    }

    public void callbacks(boolean turnOn) {
        _config.put(CALLBACKS,turnOn);
    }
    
    public void callConstructors(boolean flag){
        _config.put(CALL_CONSTRUCTORS,TernaryBool.forBoolean(flag));
    }

    public void classActivationDepthConfigurable(boolean turnOn) {
        _config.put(CLASS_ACTIVATION_DEPTH_CONFIGURABLE,turnOn);
    }

    Config4Class configClass(String className) {
		Config4Class config = (Config4Class)exceptionalClasses().get(className);

        if (Debug.configureAllClasses) {
            if (config == null) {
                if (!isIgnoredClass(className)) {
                    config = (Config4Class) objectClass(className);
                }

            }
        }
        return config;
    }

    /**
     * @deprecated using deprecated api
     */
	private boolean isIgnoredClass(String className) {
		Class[] ignore = new Class[] { P1HashElement.class,
		    P1ListElement.class, P1Object.class, P1Collection.class,

		    // XXX You may need the following for indexing tests. 

		    //                        P2HashMap.class,
		    //                        P2LinkedList.class,

		    StaticClass.class, StaticField.class
		};
		for (int i = 0; i < ignore.length; i++) {
		    if (ignore[i].getName().equals(className)) {
		        return true;
		    }

		}
		return false;
	}

    public Object deepClone(Object param) {
        Config4Impl ret = new Config4Impl();
        ret._config=(KeySpecHashtable4)_config.deepClone(this);
        ret._internStrings = _internStrings;
        ret._messageLevel = _messageLevel;
        ret._readOnly = _readOnly;
        return ret;
    }
    
    public void stream(ObjectContainerBase stream) {
    	i_stream=stream;
    }

    public void detectSchemaChanges(boolean flag) {
        _config.put(DETECT_SCHEMA_CHANGES,flag);
    }

    public void disableCommitRecovery() {
        _config.put(DISABLE_COMMIT_RECOVERY,true);
    }

    /**
     * @deprecated
     */
    public void discardFreeSpace(int bytes) {
    	if(bytes < 0){
    		throw new IllegalArgumentException();
    	}
        _config.put(DISCARD_FREESPACE,bytes);
    }
    
    public void discardSmallerThan(int byteCount) {
        discardFreeSpace(byteCount);
    }

    /**
     * @deprecated
     */
    public void encrypt(boolean flag) {
        globalSettingOnly();
        _config.put(ENCRYPT,flag);
    }
    
    void oldEncryptionOff() {
        _config.put(ENCRYPT,false);
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
    	PrintStream outStream=outStreamOrNull();
        return outStream == null ? System.err : outStream;
    }

    public void exceptionsOnNotStorable(boolean flag) {
        _config.put(EXCEPTIONS_ON_NOT_STORABLE,flag);
    }
    
    public void flushFileBuffers(boolean flag){
        _config.put(FLUSH_FILE_BUFFERS,flag);
    }

    public FreespaceConfiguration freespace() {
        return this;
    }
    
    public void freespaceFiller(FreespaceFiller freespaceFiller) {
    	_config.put(FREESPACE_FILLER, freespaceFiller);
    }

    public FreespaceFiller freespaceFiller() {
    	return (FreespaceFiller) _config.get(FREESPACE_FILLER);
    }
    
    /**
     * @deprecated Use {@link #generateUUIDs(ConfigScope)} instead.
     */
    public void generateUUIDs(int setting) {
        generateUUIDs(ConfigScope.forID(setting));
    }

	public void generateUUIDs(ConfigScope scope) {
        _config.put(GENERATE_UUIDS,scope);
    }

    /**
     * @deprecated Use {@link #generateVersionNumbers(ConfigScope)} instead.
     */
    public void generateVersionNumbers(int setting) {
        generateVersionNumbers(ConfigScope.forID(setting));
    }

    public void generateVersionNumbers(ConfigScope scope) {
        _config.put(GENERATE_VERSION_NUMBERS,scope);
    }

    public MessageSender getMessageSender() {
        return this;
    }

    private void globalSettingOnly() {
        if (i_stream != null) {
           throw new GlobalOnlyConfigException();
        }
    }
    
    public void internStrings(boolean doIntern) {
    	_internStrings = doIntern;
    }
    
    public void io(IoAdapter adapter){
        globalSettingOnly();
        _config.put(IOADAPTER,adapter);
    }

    public void lockDatabaseFile(boolean flag) {
    	_config.put(LOCK_FILE,flag);
    }
    
    public void markTransient(String marker) {
        Platform4.markTransient(marker);
    }

    public void messageLevel(int level) {
    	_messageLevel = level;
        if (outStream() == null) {
            setOut(System.out);
        }
    }

    public void optimizeNativeQueries(boolean optimizeNQ) {
    	_config.put(OPTIMIZE_NQ,optimizeNQ);
    }
    
    public boolean optimizeNativeQueries() {
    	return _config.getAsBoolean(OPTIMIZE_NQ);
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
        
        Hashtable4 xClasses=exceptionalClasses();
        Config4Class c4c = (Config4Class) xClasses.get(className);
        if (c4c == null) {
            c4c = new Config4Class(this, className);
            xClasses.put(className, c4c);
        }
        return c4c;
    }

    private PrintStream outStreamOrNull() {
    	return (PrintStream)_config.get(OUTSTREAM);
    }
    
    PrintStream outStream() {
    	PrintStream outStream=outStreamOrNull();
        return outStream == null ? System.out : outStream;
    }

    /**
     * @deprecated
     */
    public void password(String pw) {
        globalSettingOnly();
        _config.put(PASSWORD,pw);
    }

    public void readOnly(boolean flag) {
        _readOnly = flag;
    }

	public GenericReflector reflector() {
		GenericReflector reflector=(GenericReflector)_config.get(REFLECTOR);
		if(reflector == null){
			Reflector configuredReflector=(Reflector)_config.get(CONFIGURED_REFLECTOR);
			if(configuredReflector == null){
				configuredReflector=Platform4.createReflector(classLoader());
				_config.put(CONFIGURED_REFLECTOR,configuredReflector);	
			}
			reflector=new GenericReflector(null, configuredReflector);
            _config.put(REFLECTOR,reflector);
            configuredReflector.setParent(reflector);
		}
// TODO: transaction assignment has been moved to YapStreamBase#initialize1().
// implement better, more generic solution as described in COR-288
//		if(! reflector.hasTransaction() && i_stream != null){
//			reflector.setTransaction(i_stream.getSystemTransaction());
//		}
		return reflector;
	}

	public void reflectWith(Reflector reflect) {
		
        if(i_stream != null){
        	Exceptions4.throwRuntimeException(46);   // see readable message for code in Messages.java
        }
		
        if (reflect == null) {
            throw new NullPointerException();
        }
        _config.put(CONFIGURED_REFLECTOR,reflect);
		_config.put(REFLECTOR,null);
    }

    public void refreshClasses() {
        if (i_stream != null) {
            i_stream.refreshClasses();
        }
    }

    void rename(Rename a_rename) {
    	Collection4 renameCollection=rename();
        if (renameCollection == null) {
            renameCollection = new Collection4();
            _config.put(RENAME,renameCollection);
        }
        renameCollection.add(a_rename);
    }

    public void reserveStorageSpace(long byteCount) throws DatabaseReadOnlyException {
        int reservedStorageSpace = (int) byteCount;
        if (reservedStorageSpace < 0) {
            reservedStorageSpace = 0;
        }
        _config.put(RESERVED_STORAGE_SPACE,reservedStorageSpace);
        if (i_stream != null) {
            i_stream.reserve(reservedStorageSpace);
        }
    }

    /**
     * The ConfigImpl also is our messageSender
     */
    public void send(Object obj) {
        if (i_stream != null) {
            i_stream.send(obj);
        }
    }

    public void setBlobPath(String path) throws IOException {
        ensureDirExists(path);
        _config.put(BLOBPATH,path);
    }

    /**
     * @deprecated
     */
    public void setClassLoader(Object classLoader) {
        reflectWith(Platform4.createReflector(classLoader));
    }

    public void setMessageRecipient(MessageRecipient messageRecipient) {
    	_config.put(MESSAGE_RECIPIENT,messageRecipient);
    }

    public void setOut(PrintStream outStream) {
        _config.put(OUTSTREAM,outStream);
        if (i_stream != null) {
            i_stream.logMsg(19, Db4o.version());
        } else {
            Messages.logMsg(Db4o.configure(), 19, Db4o.version());
        }
    }

    public void singleThreadedClient(boolean flag) {
    	_config.put(SINGLE_THREADED_CLIENT,flag);
    }

    public void testConstructors(boolean flag) {
    	_config.put(TEST_CONSTRUCTORS,flag);
    }

    public void timeoutClientSocket(int milliseconds) {
    	_config.put(TIMEOUT_CLIENT_SOCKET,milliseconds);
    }

    public void timeoutServerSocket(int milliseconds) {
    	_config.put(TIMEOUT_SERVER_SOCKET,milliseconds);
    }

    public void unicode(boolean unicodeOn) {
    	_config.put(ENCODING,(unicodeOn ? Const4.UNICODE : Const4.ISO8859));
    }

    public void updateDepth(int depth) {
        DiagnosticProcessor dp = diagnosticProcessor();
        if (dp.enabled()) {
            dp.checkUpdateDepth(depth);
        }
    	_config.put(UPDATE_DEPTH,depth);
    }
    
    public void useBTreeSystem() {
        _config.put(FREESPACE_SYSTEM,AbstractFreespaceManager.FM_BTREE);
    }

    public void useRamSystem() {
        _config.put(FREESPACE_SYSTEM,AbstractFreespaceManager.FM_RAM);
    }

    public void useIndexSystem() {
        _config.put(FREESPACE_SYSTEM,AbstractFreespaceManager.FM_IX);
    }
    
    public void weakReferenceCollectionInterval(int milliseconds) {
    	_config.put(WEAK_REFERENCE_COLLECTION_INTERVAL,milliseconds);
    }

    public void weakReferences(boolean flag) {
    	_config.put(WEAK_REFERENCES,flag);
    }
    
    private Collection4 aliases() {
    	Collection4 aliasesCollection=(Collection4)_config.get(ALIASES);
    	if (null == aliasesCollection) {
    		aliasesCollection = new Collection4();
    		_config.put(ALIASES,aliasesCollection);
    	}
    	return aliasesCollection;
    }
    
    public void addAlias(Alias alias) {
    	if (null == alias) throw new com.db4o.foundation.ArgumentNullException("alias");
    	aliases().add(alias);
    }
    
    public void removeAlias(Alias alias) {
    	if (null == alias) throw new com.db4o.foundation.ArgumentNullException("alias");
    	aliases().remove(alias);
    }
    
    public String resolveAliasRuntimeName(String runtimeType) {

    	Collection4 configuredAliases=aliases();
    	if (null == configuredAliases) {
    		return runtimeType;
    	}
    	
    	Iterator4 i = configuredAliases.iterator();
    	while (i.moveNext()) {
    		String resolved = ((Alias)i.current()).resolveRuntimeName(runtimeType);
    		if (null != resolved){
    			return resolved; 
    		}
    	}
    	
    	return runtimeType;
    }
    
    public String resolveAliasStoredName(String storedType) {

    	Collection4 configuredAliases=aliases();
    	if (null == configuredAliases){
    		return storedType;
    	}
    	
    	Iterator4 i = configuredAliases.iterator();
    	while (i.moveNext()) {
    		String resolved = ((Alias)i.current()).resolveStoredName(storedType);
    		if (null != resolved){
    			return resolved; 
    		}
    	}
    	
    	return storedType;
    }
    
    ReflectClass reflectorFor(Object clazz) {
    	return ReflectorUtils.reflectClassFor(reflector(), clazz);
    }

	public boolean allowVersionUpdates() {
		return _config.getAsBoolean(ALLOW_VERSION_UPDATES);
	}

	boolean automaticShutDown() {
		return _config.getAsBoolean(AUTOMATIC_SHUTDOWN);
	}

	public byte blockSize() {
		return _config.getAsByte(BLOCKSIZE);
	}
    
    public int bTreeNodeSize() {
        return _config.getAsInt(BTREE_NODE_SIZE);
    }
    
    public int bTreeCacheHeight(){
        return _config.getAsInt(BTREE_CACHE_HEIGHT);
    }
    
	String blobPath() {
		return _config.getAsString(BLOBPATH);
	}

	boolean callbacks() {
		return _config.getAsBoolean(CALLBACKS);
	}

	TernaryBool callConstructors() {
		return _config.getAsTernaryBool(CALL_CONSTRUCTORS);
	}

	boolean classActivationDepthConfigurable() {
		return _config.getAsBoolean(CLASS_ACTIVATION_DEPTH_CONFIGURABLE);
	}

	Object classLoader() {
		return _config.get(CLASSLOADER);
	}

	boolean detectSchemaChanges() {
		return _config.getAsBoolean(DETECT_SCHEMA_CHANGES);
	}

	boolean commitRecoveryDisabled() {
		return _config.getAsBoolean(DISABLE_COMMIT_RECOVERY);
	}

    public DiagnosticConfiguration diagnostic() {
        return (DiagnosticConfiguration)_config.get(DIAGNOSTIC);
    }
    
    public DiagnosticProcessor diagnosticProcessor(){
        return (DiagnosticProcessor)_config.get(DIAGNOSTIC); 
    }

	public int discardFreeSpace() {
		return _config.getAsInt(DISCARD_FREESPACE);
	}

	byte encoding() {
		return _config.getAsByte(ENCODING);
	}

	boolean encrypt() {
		return _config.getAsBoolean(ENCRYPT);
	}

	public Hashtable4 exceptionalClasses() {
		Hashtable4 exceptionalClasses = (Hashtable4)_config.get(EXCEPTIONAL_CLASSES);
		if(exceptionalClasses==null) {
			exceptionalClasses=new Hashtable4(16);
			_config.put(EXCEPTIONAL_CLASSES,exceptionalClasses);
		}
		return exceptionalClasses;
	}

	public boolean exceptionsOnNotStorable() {
		return _config.getAsBoolean(EXCEPTIONS_ON_NOT_STORABLE);
	}

	public boolean flushFileBuffers() {
		return _config.getAsBoolean(FLUSH_FILE_BUFFERS);
	}

	byte freespaceSystem() {
		return _config.getAsByte(FREESPACE_SYSTEM);
	}

	public ConfigScope generateUUIDs() {
		return (ConfigScope) _config.get(GENERATE_UUIDS);
	}

	public ConfigScope generateVersionNumbers() {
		return (ConfigScope) _config.get(GENERATE_VERSION_NUMBERS);
	}

	public boolean internStrings() {
		return _internStrings;
	}
	
	public void isServer(boolean flag){
		_config.put(IS_SERVER,flag);
	}

	boolean isServer() {
		return _config.getAsBoolean(IS_SERVER);
	}

	boolean lockFile() {
		return _config.getAsBoolean(LOCK_FILE);
	}

	int messageLevel() {
		return _messageLevel;
	}

	public MessageRecipient messageRecipient() {
		return (MessageRecipient)_config.get(MESSAGE_RECIPIENT);
	}

	boolean optimizeNQ() {
		return _config.getAsBoolean(OPTIMIZE_NQ);
	}

	String password() {
		return _config.getAsString(PASSWORD);
	}

	public void prefetchIDCount(int prefetchIDCount) {
		_config.put(PREFETCH_ID_COUNT,prefetchIDCount);
	}

	public int prefetchIDCount() {
		return _config.getAsInt(PREFETCH_ID_COUNT);
	}

	public void prefetchObjectCount(int prefetchObjectCount) {
		_config.put(PREFETCH_OBJECT_COUNT,prefetchObjectCount);
	}

	public int prefetchObjectCount() {
		return _config.getAsInt(PREFETCH_OBJECT_COUNT);
	}

	Hashtable4 readAs() {
		return (Hashtable4)_config.get(READ_AS);
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	Collection4 rename() {
		return (Collection4)_config.get(RENAME);
	}

	int reservedStorageSpace() {
		return _config.getAsInt(RESERVED_STORAGE_SPACE);
	}

	public boolean singleThreadedClient() {
		return _config.getAsBoolean(SINGLE_THREADED_CLIENT);
	}

	boolean testConstructors() {
		return _config.getAsBoolean(TEST_CONSTRUCTORS);
	}

	public int timeoutClientSocket() {
		return _config.getAsInt(TIMEOUT_CLIENT_SOCKET);
	}

	public int timeoutServerSocket() {
		return _config.getAsInt(TIMEOUT_SERVER_SOCKET);
	}

	int updateDepth() {
		return _config.getAsInt(UPDATE_DEPTH);
	}

	int weakReferenceCollectionInterval() {
		return _config.getAsInt(WEAK_REFERENCE_COLLECTION_INTERVAL);
	}

	boolean weakReferences() {
		return _config.getAsBoolean(WEAK_REFERENCES);
	}

	IoAdapter ioAdapter() {
		return (IoAdapter)_config.get(IOADAPTER);
	}
	
	public QueryConfiguration queries() {
		return this;
	}

	public void evaluationMode(QueryEvaluationMode mode) {
		_config.put(QUERY_EVALUATION_MODE, mode);
	}
	
	public QueryEvaluationMode queryEvaluationMode() {
		return (QueryEvaluationMode)_config.get(QUERY_EVALUATION_MODE);
	}
	

	public void queryResultIteratorFactory(QueryResultIteratorFactory factory) {
		_config.put(CLIENT_QUERY_RESULT_ITERATOR_FACTORY, factory);
	}
	
	public QueryResultIteratorFactory queryResultIteratorFactory() {
		return (QueryResultIteratorFactory)_config.get(CLIENT_QUERY_RESULT_ITERATOR_FACTORY);
	}


	public ClientServerConfiguration clientServer() {
		return this;
	}

	public void batchMessages(boolean flag) {
		_config.put(BATCH_MESSAGES, flag);
	}
	
	public boolean batchMessages() {
		return _config.getAsBoolean(BATCH_MESSAGES);
	}
	
	public void maxBatchQueueSize(int maxSize) {
		_config.put(MAX_BATCH_QUEUE_SIZE, maxSize);
	}

	public int maxBatchQueueSize() {
		return _config.getAsInt(MAX_BATCH_QUEUE_SIZE);
	}

	public void activationDepthProvider(ActivationDepthProvider provider) {
		_config.put(ACTIVATION_DEPTH_PROVIDER, provider);
	}

	public ActivationDepthProvider activationDepthProvider() {
		return (ActivationDepthProvider) _config.get(ACTIVATION_DEPTH_PROVIDER);
	}
}