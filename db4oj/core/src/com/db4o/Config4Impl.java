/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.io.IoAdapter;
import com.db4o.io.RandomAccessFileAdapter;
import com.db4o.messaging.MessageRecipient;
import com.db4o.messaging.MessageSender;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.generic.*;

/**
 * Configuration template for creating new db4o files
 * 
 * @exclude
 */
public final class Config4Impl

implements Configuration, Cloneable, DeepClone, MessageSender {

    int              i_activationDepth                  = 5;
    boolean          i_automaticShutDown                = true;
    byte			 i_blockSize						= 1;  // TODO: set this to 8, when implementation is done
    String           i_blobPath;
    boolean          i_callbacks                        = true;
    int			 	 i_callConstructors;
    boolean          i_classActivationDepthConfigurable = true;
    ClassLoader      i_classLoader;
    boolean          i_detectSchemaChanges              = true;
    boolean          i_disableCommitRecovery;
    int              i_discardFreeSpace;
    byte             i_encoding                         = YapConst.UNICODE;
    boolean          i_encrypt;
    Hashtable4       i_exceptionalClasses               = new Hashtable4(16);
    boolean          i_exceptionsOnNotStorable;
    public int       i_generateUUIDs;
    public int       i_generateVersionNumbers;
    boolean			 i_isServer = false;
    boolean          i_lockFile                         = true;
    int              i_messageLevel                     = YapConst.NONE;
    MessageRecipient i_messageRecipient;
    MessageSender    i_messageSender;
    PrintStream      i_outStream;
    String           i_password;
    boolean          i_readonly;
    private Reflector _configuredReflector;
    private GenericReflector _reflector;
    Collection4      i_rename;
    int              i_reservedStorageSpace;
    boolean          i_singleThreadedClient;
    YapStream        i_stream;                                                           // is null until deepClone is called
    boolean          i_testConstructors                 = true;
    int              i_timeoutClientSocket              = YapConst.CLIENT_SOCKET_TIMEOUT;
    int              i_timeoutPingClients               = YapConst.CONNECTION_TIMEOUT;
    int              i_timeoutServerSocket              = YapConst.SERVER_SOCKET_TIMEOUT;
    int              i_updateDepth;
    int              i_weakReferenceCollectionInterval  = 1000;
    boolean          i_weakReferences                   = true;
    IoAdapter        i_ioAdapter 
    	// NOTE: activate this config to trigger the defragment failure
    	//= new NIOFileAdapter(512,3);
    	= new RandomAccessFileAdapter();
    
    int activationDepth() {
        return i_activationDepth;
    }

    public void activationDepth(int depth) {
        i_activationDepth = depth;
    }

    public void automaticShutDown(boolean flag) {
        i_automaticShutDown = flag;
    }
    
    public void blockSize(int bytes){
       if (bytes < 1 || bytes > 127) {
           Db4o.throwRuntimeException(1);
       }
       
       if (i_stream != null) {
           Db4o.throwRuntimeException(46);   // see readable message for code in Messages.java
       }
       
       i_blockSize = (byte)bytes;
    }

    public void callbacks(boolean turnOn) {
        i_callbacks = turnOn;
    }
    
    public void callConstructors(boolean flag){
        i_callConstructors = flag ? YapConst.YES : YapConst.NO;
    }

    public void classActivationDepthConfigurable(boolean turnOn) {
        i_classActivationDepthConfigurable = turnOn;
    }

    Config4Class configClass(String className) {
        Config4Class config = (Config4Class) i_exceptionalClasses
            .get(className);

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

    public Object deepClone(Object param) throws CloneNotSupportedException {
        Config4Impl ret = (Config4Impl) this.clone();
        ret.i_stream = (YapStream) param;
        if (i_exceptionalClasses != null) {
            ret.i_exceptionalClasses = (Hashtable4) i_exceptionalClasses
                .deepClone(ret);
        }
        if (i_rename != null) {
            ret.i_rename = (Collection4) i_rename.deepClone(ret);
        }
        if(_reflector != null){
        	ret._reflector = (GenericReflector)_reflector.deepClone(ret);
        }
        return ret;
    }

    public void detectSchemaChanges(boolean flag) {
        i_detectSchemaChanges = flag;
    }

    public void disableCommitRecovery() {
        i_disableCommitRecovery = true;
    }

    public void discardFreeSpace(int bytes) {
        i_discardFreeSpace = bytes;
    }

    public void encrypt(boolean flag) {
        globalSettingOnly();
        i_encrypt = flag;
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
        i_exceptionsOnNotStorable = flag;
    }

    public void generateUUIDs(int setting) {
        i_generateUUIDs = setting;
        storeStreamBootRecord();
    }
    
    private void storeStreamBootRecord() {
        if(i_stream instanceof YapFile) {
            YapFile yapFile = (YapFile)i_stream;
            yapFile.i_bootRecord.initConfig(this);
            yapFile.setInternal(yapFile.i_systemTrans, yapFile.i_bootRecord, false);
            yapFile.i_systemTrans.commit();
        }
    }

    public void generateVersionNumbers(int setting) {
        i_generateVersionNumbers = setting;
        storeStreamBootRecord();
    }

    public MessageSender getMessageSender() {
        return this;
    }

    private void globalSettingOnly() {
        if (i_stream != null) {
            new Exception().printStackTrace();
            Db4o.throwRuntimeException(46);
        }
    }
    
    public void io(IoAdapter adapter){
        globalSettingOnly();
        i_ioAdapter = adapter;
    }

    public void lockDatabaseFile(boolean flag) {
        i_lockFile = flag;
    }
    
    public void markTransient(String marker) {
        Platform.markTransient(marker);
    }

    public void messageLevel(int level) {
        i_messageLevel = level;
        if (i_outStream == null) {
            setOut(System.out);
        }
    }

    public ObjectClass objectClass(Object clazz) {
        
        ReflectClass claxx = reflectorFor(clazz);
        
        if (claxx == null) {
            return null;
        }
        
        String className = claxx.getName(); 
        
        Config4Class c4c = (Config4Class) i_exceptionalClasses.get(className);
        if (c4c == null) {
            c4c = new Config4Class(this, className);
            i_exceptionalClasses.put(className, c4c);
        }
        return c4c;
    }

    PrintStream outStream() {
        return i_outStream == null ? System.out : i_outStream;
    }

    public void password(String pw) {
        globalSettingOnly();
        i_password = pw;
    }

    public void readOnly(boolean flag) {
        globalSettingOnly();
        i_readonly = flag;
    }

	GenericReflector reflector() {
		if(_reflector == null){
			if(_configuredReflector == null){
				_configuredReflector = Platform.createReflector(this);	
			}
			_reflector = new GenericReflector(null, _configuredReflector);
		}
		if(! _reflector.hasTransaction() && i_stream != null){
			_reflector.setTransaction(i_stream.i_systemTrans);
		}
		return _reflector;
	}

	public void reflectWith(Reflector reflect) {
		
        if(i_stream != null){
        	Db4o.throwRuntimeException(46);   // see readable message for code in Messages.java
        }
		
        if (reflect == null) {
            throw new NullPointerException();
        }
        _configuredReflector = reflect;
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
        i_reservedStorageSpace = (int) byteCount;
        if (i_reservedStorageSpace < 0) {
            i_reservedStorageSpace = 0;
        }
        if (i_stream != null) {
            i_stream.reserve(i_reservedStorageSpace);
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
        i_blobPath = path;
    }

    public void setClassLoader(ClassLoader classLoader) {
        i_classLoader = classLoader;
    }

    public void setMessageRecipient(MessageRecipient messageRecipient) {
        i_messageRecipient = messageRecipient;
    }

    public void setOut(PrintStream outStream) {
        i_outStream = outStream;
        if (i_stream != null) {
            i_stream.logMsg(19, Db4o.version());
        } else {
            Db4o.logMsg(Db4o.i_config, 19, Db4o.version());
        }
    }

    public void singleThreadedClient(boolean flag) {
        i_singleThreadedClient = flag;
    }

    public void testConstructors(boolean flag) {
        i_testConstructors = flag;
    }

    public void timeoutClientSocket(int milliseconds) {
        i_timeoutClientSocket = milliseconds;
    }

    public void timeoutPingClients(int milliseconds) {
        i_timeoutPingClients = milliseconds;
    }

    public void timeoutServerSocket(int milliseconds) {
        i_timeoutServerSocket = milliseconds;

    }

    public void unicode(boolean unicodeOn) {
        if (unicodeOn) {
            i_encoding = YapConst.UNICODE;
        } else {
            i_encoding = YapConst.ISO8859;
        }
    }

    public void updateDepth(int depth) {
        i_updateDepth = depth;
    }

    public void weakReferenceCollectionInterval(int milliseconds) {
        i_weakReferenceCollectionInterval = milliseconds;
    }

    public void weakReferences(boolean flag) {
        i_weakReferences = flag;
    }

    ReflectClass reflectorFor(Object clazz) {
        
        clazz = Platform.getClassForType(clazz);
        
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
    
    
}