/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using j4o.io;
using com.db4o.config;
using com.db4o.io;
using com.db4o.messaging;
using com.db4o.reflect;
using com.db4o.reflect.jdk;
namespace com.db4o {

   internal class Config4Impl : Configuration, Cloneable, DeepClone, MessageSender {
      
      internal Config4Impl() : base() {
      }
      internal int i_activationDepth = 5;
      internal bool i_automaticShutDown = true;
      internal byte i_blockSize = 1;
      internal String i_blobPath;
      internal bool i_callbacks = true;
      internal int i_callConstructors;
      internal bool i_classActivationDepthConfigurable = true;
      internal ClassLoader i_classLoader;
      internal bool i_detectSchemaChanges = true;
      internal bool i_disableCommitRecovery;
      internal int i_discardFreeSpace;
      internal byte i_encoding = 2;
      internal bool i_encrypt;
      internal Hashtable4 i_exceptionalClasses = new Hashtable4(16);
      internal bool i_exceptionsOnNotStorable;
      public int i_generateUUIDs;
      public int i_generateVersionNumbers;
      internal bool i_isServer = false;
      internal bool i_lockFile = true;
      internal int i_messageLevel = 0;
      internal MessageRecipient i_messageRecipient;
      internal MessageSender i_messageSender;
      internal PrintStream i_outStream;
      internal String i_password;
      internal bool i_readonly;
      internal IReflect i_reflect = new CReflect();
      internal Collection4 i_rename;
      internal int i_reservedStorageSpace;
      internal bool i_singleThreadedClient;
      internal YapStream i_stream;
      internal bool i_testConstructors = true;
      internal int i_timeoutClientSocket = 300000;
      internal int i_timeoutPingClients = 180000;
      internal int i_timeoutServerSocket = 5000;
      internal int i_updateDepth;
      internal int i_weakReferenceCollectionInterval = 1000;
      internal bool i_weakReferences = true;
      internal IoAdapter i_ioAdapter = new RandomAccessFileAdapter();
      static internal Class class__com__db4o__MetaClass;
      static internal Class class__com__db4o__MetaField;
      static internal Class class__com__db4o__MetaIndex;
      static internal Class class__com__db4o__P1HashElement;
      static internal Class class__com__db4o__P1ListElement;
      static internal Class class__com__db4o__P1Object;
      static internal Class class__com__db4o__P1Collection;
      static internal Class class__com__db4o__StaticClass;
      static internal Class class__com__db4o__StaticField;
      
      internal int activationDepth() {
         return i_activationDepth;
      }
      
      public void activationDepth(int i) {
         i_activationDepth = i;
      }
      
      public void automaticShutDown(bool xbool) {
         i_automaticShutDown = xbool;
      }
      
      public void blockSize(int i) {
         if (i < 1 || i > 127) Db4o.throwRuntimeException(2);
         if (i_stream != null) Db4o.throwRuntimeException(46);
         i_blockSize = (byte)i;
      }
      
      public void callbacks(bool xbool) {
         i_callbacks = xbool;
      }
      
      public void callConstructors(bool xbool) {
         i_callConstructors = xbool ? 1 : -1;
      }
      
      public void classActivationDepthConfigurable(bool xbool) {
         i_classActivationDepthConfigurable = xbool;
      }
      
      internal Config4Class configClass(String xstring) {
         Config4Class config4class1 = (Config4Class)i_exceptionalClasses.get(xstring);
         return config4class1;
      }
      
      public Object deepClone(Object obj) {
         Config4Impl config4impl_0_1 = (Config4Impl)j4o.lang.JavaSystem.clone(this);
         config4impl_0_1.i_stream = (YapStream)obj;
         if (i_exceptionalClasses != null) config4impl_0_1.i_exceptionalClasses = (Hashtable4)i_exceptionalClasses.deepClone(config4impl_0_1);
         if (i_rename != null) config4impl_0_1.i_rename = (Collection4)i_rename.deepClone(config4impl_0_1);
         return config4impl_0_1;
      }
      
      public void detectSchemaChanges(bool xbool) {
         i_detectSchemaChanges = xbool;
      }
      
      public void disableCommitRecovery() {
         i_disableCommitRecovery = true;
      }
      
      public void discardFreeSpace(int i) {
         i_discardFreeSpace = i;
      }
      
      public void encrypt(bool xbool) {
         globalSettingOnly();
         i_encrypt = xbool;
      }
      
      internal void ensureDirExists(String xstring) {
         File file1 = new File(xstring);
         if (!file1.exists()) file1.mkdirs();
         if (!file1.exists() || !file1.isDirectory()) throw new IOException(Messages.get(37, xstring));
      }
      
      internal PrintStream errStream() {
         return i_outStream == null ? j4o.lang.JavaSystem.err : i_outStream;
      }
      
      public void exceptionsOnNotStorable(bool xbool) {
         i_exceptionsOnNotStorable = xbool;
      }
      
      public void generateUUIDs(int i) {
         i_generateUUIDs = i;
         storeStreamBootRecord();
      }
      
      private void storeStreamBootRecord() {
         if (i_stream is YapFile) {
            YapFile yapfile1 = (YapFile)i_stream;
            yapfile1.i_bootRecord.initConfig(this);
            yapfile1.setInternal(yapfile1.i_systemTrans, yapfile1.i_bootRecord, false);
            yapfile1.i_systemTrans.commit();
         }
      }
      
      public void generateVersionNumbers(int i) {
         i_generateVersionNumbers = i;
         storeStreamBootRecord();
      }
      
      public MessageSender getMessageSender() {
         return this;
      }
      
      private void globalSettingOnly() {
         if (i_stream != null) {
            j4o.lang.JavaSystem.printStackTrace(new Exception());
            Db4o.throwRuntimeException(46);
         }
      }
      
      public void io(IoAdapter ioadapter) {
         globalSettingOnly();
         i_ioAdapter = ioadapter;
      }
      
      public void lockDatabaseFile(bool xbool) {
         i_lockFile = xbool;
      }
      
      public void markTransient(String xstring) {
         Platform.markTransient(xstring);
      }
      
      public void messageLevel(int i) {
         i_messageLevel = i;
         if (i_outStream == null) setOut(j4o.lang.JavaSystem._out);
      }
      
      public ObjectClass objectClass(Object obj) {
         String xstring1 = classNameFor(obj);
         if (xstring1 == null) return null;
         Config4Class config4class1 = (Config4Class)i_exceptionalClasses.get(xstring1);
         if (config4class1 == null) {
            config4class1 = new Config4Class(this, xstring1);
            i_exceptionalClasses.put(xstring1, config4class1);
         }
         return config4class1;
      }
      
      internal PrintStream outStream() {
         return i_outStream == null ? j4o.lang.JavaSystem._out : i_outStream;
      }
      
      public void password(String xstring) {
         globalSettingOnly();
         i_password = xstring;
      }
      
      public void readOnly(bool xbool) {
         globalSettingOnly();
         i_readonly = xbool;
      }
      
      public IReflect reflector() {
         return i_reflect;
      }
      
      public void reflectWith(IReflect ireflect) {
         if (ireflect == null) throw new ArgumentNullException();
         i_reflect = ireflect;
      }
      
      public void refreshClasses() {
         if (i_stream == null) Db4o.forEachSession(new Config4Impl__1(this)); else i_stream.refreshClasses();
      }
      
      internal void rename(Rename rename) {
         if (i_rename == null) i_rename = new Collection4();
         i_rename.add(rename);
      }
      
      public void reserveStorageSpace(long l) {
         i_reservedStorageSpace = (int)l;
         if (i_reservedStorageSpace < 0) i_reservedStorageSpace = 0;
         if (i_stream != null) i_stream.reserve(i_reservedStorageSpace);
      }
      
      public void send(Object obj) {
         if (i_stream == null) Db4o.forEachSession(new Config4Impl__2(this)); else i_stream.send(obj);
      }
      
      public void setBlobPath(String xstring) {
         ensureDirExists(xstring);
         i_blobPath = xstring;
      }
      
      public void setClassLoader(ClassLoader classloader) {
         i_classLoader = classloader;
      }
      
      public void setMessageRecipient(MessageRecipient messagerecipient) {
         i_messageRecipient = messagerecipient;
      }
      
      public void setOut(PrintStream printstream) {
         i_outStream = printstream;
         if (i_stream != null) i_stream.logMsg(19, Db4o.version()); else Db4o.logMsg(Db4o.i_config, 19, Db4o.version());
      }
      
      public void singleThreadedClient(bool xbool) {
         i_singleThreadedClient = xbool;
      }
      
      public void testConstructors(bool xbool) {
         i_testConstructors = xbool;
      }
      
      public void timeoutClientSocket(int i) {
         i_timeoutClientSocket = i;
      }
      
      public void timeoutPingClients(int i) {
         i_timeoutPingClients = i;
      }
      
      public void timeoutServerSocket(int i) {
         i_timeoutServerSocket = i;
      }
      
      public void unicode(bool xbool) {
         if (xbool) i_encoding = (byte)2; else i_encoding = (byte)1;
      }
      
      public void updateDepth(int i) {
         i_updateDepth = i;
      }
      
      public void weakReferenceCollectionInterval(int i) {
         i_weakReferenceCollectionInterval = i;
      }
      
      public void weakReferences(bool xbool) {
         i_weakReferences = xbool;
      }
      
      static internal String classNameFor(Object obj) {
         if (obj == null) return null;
         obj = Platform.getClassForType(obj);
         if (obj is String) return (String)obj;
         Class var_class1;
         if (obj is Class) var_class1 = (Class)obj; else var_class1 = j4o.lang.Class.getClassForObject(obj);
         return var_class1.getName();
      }
   }
}