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
using com.db4o.ext;
namespace com.db4o {

   internal class YapClient : YapStream, ExtClient {
      internal Object blobLock = new Object();
      private YapClientBlobThread blobThread;
      private YapSocket i_socket;
      internal Queue4 messageQueue = new Queue4();
      public Lock4 messageQueueLock = new Lock4();
      private String password;
      internal int[] prefetchedIDs = new int[10];
      internal YapClientThread readerThread;
      internal int remainingIDs;
      private String switchedToFile;
      private bool singleThreaded;
      private String userName;
      private Db4oDatabase i_db;
      
      internal YapClient() : base((YapStream)null) {
      }
      
      public YapClient(String xstring) : this() {
         lock (this.Lock()) {
            singleThreaded = i_config.i_singleThreadedClient;
            throw new RuntimeException("This constructor is for Debug.fakeServer use only.");
         }
      }
      
      internal YapClient(YapSocket yapsocket, String xstring, String string_0_, bool xbool) : this() {
         lock (this.Lock()) {
            singleThreaded = i_config.i_singleThreadedClient;
            if (string_0_ == null) throw new ArgumentNullException(Messages.get(56));
            if (!xbool) string_0_ = null;
            userName = xstring;
            password = string_0_;
            i_socket = yapsocket;
            try {
               {
                  loginToServer(yapsocket);
               }
            }  catch (IOException ioexception) {
               {
                  i_references.stopTimer();
                  throw ioexception;
               }
            }
            if (!singleThreaded) {
               readerThread = new YapClientThread(this, yapsocket, messageQueue, messageQueueLock);
               readerThread.setName("db4o message client for user " + xstring);
               readerThread.start();
            }
            this.logMsg(36, ToString());
            readThis();
            this.initialize3();
            Platform.postOpen(this);
         }
      }
      
      public override void backup(String xstring) {
         Db4o.throwRuntimeException(60);
      }
      
      internal override bool close2() {
         try {
            {
               Msg.CLOSE.write(this, i_socket);
            }
         }  catch (Exception exception) {
            {
            }
         }
         try {
            {
               if (!singleThreaded) readerThread.close();
            }
         }  catch (Exception exception) {
            {
            }
         }
         try {
            {
               i_socket.close();
            }
         }  catch (Exception exception) {
            {
            }
         }
         bool xbool1 = base.close2();
         return xbool1;
      }
      
      internal override void commit1() {
         i_trans.commit();
      }
      
      internal override ClassIndex createClassIndex(YapClass yapclass) {
         return new ClassIndexClient(yapclass);
      }
      
      internal YapSocket createParalellSocket() {
         Msg.GET_THREAD_ID.write(this, i_socket);
         int i1 = expectedByteResponse(Msg.ID_LIST).readInt();
         Object obj1 = null;
         YapSocket yapsocket1;
         if (i_socket is YapSocketFake) yapsocket1 = ((YapSocketFake)i_socket).i_server.openFakeClientSocket(); else {
            yapsocket1 = new YapSocket(i_socket.getHostName(), i_socket.getPort());
            loginToServer(yapsocket1);
         }
         if (switchedToFile != null) {
            MsgD msgd1 = Msg.SWITCH_TO_FILE.getWriterForString(i_systemTrans, switchedToFile);
            msgd1.write(this, yapsocket1);
            if (!Msg.OK.Equals(Msg.readMessage(i_systemTrans, yapsocket1))) throw new IOException(Messages.get(42));
         }
         Msg.USE_TRANSACTION.getWriterForInt(i_trans, i1).write(this, yapsocket1);
         return yapsocket1;
      }
      
      internal override QResult createQResult(Transaction transaction) {
         return new QResultClient(transaction);
      }
      
      internal override void createTransaction() {
         i_systemTrans = new TransactionClient(this, null);
         i_trans = new TransactionClient(this, i_systemTrans);
      }
      
      internal override bool createYapClass(YapClass yapclass, Class var_class, YapClass yapclass_1_) {
         writeMsg(Msg.CREATE_CLASS.getWriterForString(i_systemTrans, var_class.getName()));
         MsgObject msgobject1 = (MsgObject)expectedResponse(Msg.OBJECT_TO_CLIENT);
         YapWriter yapwriter1 = msgobject1.unmarshall();
         if (yapwriter1 == null) return false;
         yapwriter1.setTransaction(this.getSystemTransaction());
         if (!base.createYapClass(yapclass, var_class, yapclass_1_)) return false;
         yapclass.setID(this, msgobject1.i_id);
         yapclass.readName1(this.getSystemTransaction(), yapwriter1);
         i_classCollection.addYapClass(yapclass);
         i_classCollection.readYapClass(yapclass, var_class);
         return true;
      }
      
      internal override long currentVersion() {
         writeMsg(Msg.CURRENT_VERSION);
         return ((MsgD)expectedResponse(Msg.ID_LIST)).readLong();
      }
      
      internal override bool delete5(Transaction transaction, YapObject yapobject, int i) {
         writeMsg(Msg.DELETE.getWriterForInt(i_trans, yapobject.getID()));
         return true;
      }
      
      internal override bool detectSchemaChanges() {
         return false;
      }
      
      internal YapWriter expectedByteResponse(Msg msg) {
         Msg msg_2_1 = expectedResponse(msg);
         if (msg_2_1 == null) return null;
         return msg_2_1.getByteLoad();
      }
      
      internal Msg expectedResponse(Msg msg) {
         Msg msg_3_1 = getResponse();
         if (msg.Equals(msg_3_1)) return msg_3_1;
         return null;
      }
      
      internal void free(int i, int i_4_) {
         throw YapConst.virtualException();
      }
      
      internal override void getAll(Transaction transaction, QResult qresult) {
         writeMsg(Msg.GET_ALL);
         readResult(qresult);
      }
      
      internal Msg getResponse() {
         if (singleThreaded) {
            while (i_socket != null) {
               try {
                  {
                     Msg msg1 = Msg.readMessage(i_trans, i_socket);
                     if (Msg.PING.Equals(msg1)) writeMsg(Msg.OK); else {
                        if (Msg.CLOSE.Equals(msg1)) {
                           this.logMsg(35, ToString());
                           this.close();
                           return null;
                        }
                        if (msg1 != null) return msg1;
                     }
                  }
               }  catch (Exception exception) {
                  {
                  }
               }
            }
            return null;
         }
         return (Msg)messageQueueLock.run(new YapClient__1(this));
      }
      
      internal override YapClass getYapClass(int i) {
         YapClass yapclass1 = base.getYapClass(i);
         if (yapclass1 != null) return yapclass1;
         writeMsg(Msg.CLASS_NAME_FOR_ID.getWriterForInt(i_systemTrans, i));
         MsgD msgd1 = (MsgD)expectedResponse(Msg.CLASS_NAME_FOR_ID);
         String xstring1 = msgd1.readString();
         if (xstring1 != null && j4o.lang.JavaSystem.getLengthOf(xstring1) > 0) {
            try {
               {
                  Class var_class1 = Db4o.classForName(xstring1);
                  if (var_class1 != null) return this.getYapClass(var_class1, true);
               }
            }  catch (ClassNotFoundException classnotfoundexception) {
               {
               }
            }
         }
         return null;
      }
      
      internal YapSocket agetYapSocket() {
         return i_socket;
      }
      
      internal override bool needsLockFileThread() {
         return false;
      }
      
      internal override bool hasShutDownHook() {
         return false;
      }
      
      public override Db4oDatabase identity() {
         if (i_db == null) {
            writeMsg(Msg.IDENTITY);
            YapWriter yapwriter1 = expectedByteResponse(Msg.ID_LIST);
            i_db = (Db4oDatabase)this.getByID((long)yapwriter1.readInt());
            this.activate1(i_systemTrans, i_db, 3);
         }
         return i_db;
      }
      
      internal override bool isClient() {
         return true;
      }
      
      internal void loginToServer(YapSocket yapsocket) {
         if (password != null) {
            YapStringIOUnicode yapstringiounicode1 = new YapStringIOUnicode();
            int i1 = yapstringiounicode1.Length(userName) + yapstringiounicode1.Length(password);
            MsgD msgd1 = Msg.LOGIN.getWriterForLength(i_systemTrans, i1);
            msgd1.writeString(userName);
            msgd1.writeString(password);
            msgd1.write(this, yapsocket);
            if (!Msg.OK.Equals(Msg.readMessage(i_systemTrans, yapsocket))) throw new IOException(Messages.get(42));
         }
      }
      
      internal override bool maintainsIndices() {
         return false;
      }
      
      internal override YapWriter newObject(Transaction transaction, YapMeta yapmeta) {
         throw YapConst.virtualException();
      }
      
      internal override int newUserObject() {
         Object obj1 = null;
         if (remainingIDs < 1) {
            writeMsg(Msg.PREFETCH_IDS);
            YapWriter yapwriter1 = expectedByteResponse(Msg.ID_LIST);
            for (int i1 = 9; i1 >= 0; i1--) prefetchedIDs[i1] = yapwriter1.readInt();
            remainingIDs = 10;
         }
         remainingIDs--;
         return prefetchedIDs[remainingIDs];
      }
      
      internal int prefetchObjects(QResultClient qresultclient, Object[] objs, int i) {
         int i_5_1 = 0;
         int i_6_1 = 0;
         int[] xis1 = new int[i];
         int[] is_7_1 = new int[i];
         while (qresultclient.hasNext() && i_5_1 < i) {
            bool xbool1 = false;
            int i_8_1 = qresultclient.nextInt();
            if (i_8_1 > 0) {
               YapObject yapobject1 = this.getYapObject(i_8_1);
               if (yapobject1 != null) {
                  Object obj1 = yapobject1.getObject();
                  if (obj1 != null) {
                     objs[i_5_1] = obj1;
                     xbool1 = true;
                  } else this.yapObjectGCd(yapobject1);
               }
               if (!xbool1) {
                  xis1[i_6_1] = i_8_1;
                  is_7_1[i_6_1] = i_5_1;
                  i_6_1++;
               }
               i_5_1++;
            }
         }
         if (i_6_1 > 0) {
            writeMsg(Msg.READ_MULTIPLE_OBJECTS.getWriterForIntArray(i_trans, xis1, i_6_1));
            MsgD msgd1 = (MsgD)expectedResponse(Msg.READ_MULTIPLE_OBJECTS);
            int i_9_1 = msgd1.readInt();
            for (int i_10_1 = 0; i_10_1 < i_9_1; i_10_1++) {
               MsgObject msgobject1 = (MsgObject)Msg.OBJECT_TO_CLIENT.clone(qresultclient.i_trans);
               msgobject1.payLoad = msgd1.payLoad.readYapBytes();
               if (msgobject1.payLoad != null) {
                  msgobject1.payLoad.incrementOffset(9);
                  YapWriter yapwriter1 = msgobject1.unmarshall(9);
                  objs[is_7_1[i_10_1]] = new YapObject(xis1[i_10_1]).readPrefetch(this, qresultclient.i_trans, yapwriter1);
               }
            }
         }
         return i_5_1;
      }
      
      internal void processBlobMessage(MsgBlob msgblob) {
         lock (blobLock) {
            bool xbool1 = blobThread == null || blobThread.isTerminated();
            if (xbool1) blobThread = new YapClientBlobThread(this);
            blobThread.add(msgblob);
            if (xbool1) blobThread.start();
         }
      }
      
      internal void queryExecute(QQuery qquery, QResult qresult) {
         writeMsg(Msg.QUERY_EXECUTE.getWriter(this.marshall(qquery.getTransaction(), qquery)));
         readResult(qresult);
         qresult.reset();
      }
      
      internal override void raiseVersion(long l) {
         writeMsg(Msg.RAISE_VERSION.getWriterForLong(i_trans, l));
      }
      
      internal override void readBytes(byte[] xis, int i, int i_11_, int i_12_) {
         YapConst.virtualException();
      }
      
      internal override void readBytes(byte[] xis, int i, int i_13_) {
         writeMsg(Msg.READ_BYTES.getWriterFor2Ints(i_trans, i, i_13_));
         YapWriter yapwriter1 = expectedByteResponse(Msg.READ_BYTES);
         j4o.lang.JavaSystem.arraycopy(yapwriter1._buffer, 0, xis, 0, i_13_);
      }
      
      protected override bool rename1(Config4Impl config4impl) {
         this.logMsg(58, null);
         return false;
      }
      
      internal override YapWriter readWriterByID(Transaction transaction, int i) {
         try {
            {
               writeMsg(Msg.READ_OBJECT.getWriterForInt(transaction, i));
               YapWriter yapwriter1 = ((MsgObject)expectedResponse(Msg.OBJECT_TO_CLIENT)).unmarshall();
               yapwriter1.setTransaction(transaction);
               return yapwriter1;
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      internal override YapReader readReaderByID(Transaction transaction, int i) {
         return readWriterByID(transaction, i);
      }
      
      private void readResult(QResult qresult) {
         YapWriter yapwriter1 = expectedByteResponse(Msg.ID_LIST);
         int i1 = yapwriter1.readInt();
         for (int i_14_1 = 0; i_14_1 < i1; i_14_1++) qresult.add(yapwriter1.readInt());
      }
      
      internal void readThis() {
         writeMsg(Msg.GET_CLASSES.getWriter(i_systemTrans));
         YapWriter yapwriter1 = expectedByteResponse(Msg.GET_CLASSES);
         i_classCollection.setID(this, yapwriter1.readInt());
         this.createStringIO(yapwriter1.readByte());
         i_classCollection.read(i_systemTrans);
         i_classCollection.refreshClasses();
      }
      
      public override void releaseSemaphore(String xstring) {
         if (xstring == null) throw new ArgumentNullException();
         writeMsg(Msg.RELEASE_SEMAPHORE.getWriterForString(i_trans, xstring));
      }
      
      internal override void releaseSemaphores(Transaction transaction) {
      }
      
      private void reReadAll() {
         remainingIDs = 0;
         this.initialize0();
         this.initialize1();
         createTransaction();
         readThis();
      }
      
      internal override void rollback1() {
         writeMsg(Msg.ROLLBACK);
         i_trans.rollback();
      }
      
      public override void send(Object obj) {
         lock (i_lock) {
            if (obj != null) writeMsg(Msg.USER_MESSAGE.getWriter(this.marshall(i_trans, obj)));
         }
      }
      
      internal override void setDirty(UseSystemTransaction usesystemtransaction) {
      }
      
      public override bool setSemaphore(String xstring, int i) {
         if (xstring == null) throw new ArgumentNullException();
         writeMsg(Msg.SET_SEMAPHORE.getWriterForIntString(i_trans, i, xstring));
         Msg msg1 = getResponse();
         return msg1.Equals(Msg.SUCCESS);
      }
      
      public void switchToFile(String xstring) {
         lock (i_lock) {
            this.commit();
            writeMsg(Msg.SWITCH_TO_FILE.getWriterForString(i_trans, xstring));
            expectedResponse(Msg.OK);
            reReadAll();
            switchedToFile = xstring;
         }
      }
      
      public void switchToMainFile() {
         lock (i_lock) {
            this.commit();
            writeMsg(Msg.SWITCH_TO_MAIN_FILE);
            expectedResponse(Msg.OK);
            reReadAll();
            switchedToFile = null;
         }
      }
      
      public String name() {
         return ToString();
      }
      
      public override String ToString() {
         return "Client Connection " + userName;
      }
      
      internal override YapWriter updateObject(Transaction transaction, YapMeta yapmeta) {
         throw YapConst.virtualException();
      }
      
      internal override void write(bool xbool) {
      }
      
      internal override void writeDirty() {
      }
      
      internal override void writeEmbedded(YapWriter yapwriter, YapWriter yapwriter_15_) {
         yapwriter.addEmbedded(yapwriter_15_);
      }
      
      internal void writeMsg(Msg msg) {
         msg.write(this, i_socket);
      }
      
      internal override void writeNew(YapClass yapclass, YapWriter yapwriter) {
         writeMsg(Msg.WRITE_NEW.getWriter(yapclass, yapwriter));
      }
      
      internal override void writeTransactionPointer(int i) {
      }
      
      internal override void writeUpdate(YapClass yapclass, YapWriter yapwriter) {
         writeMsg(Msg.WRITE_UPDATE.getWriter(yapclass, yapwriter));
      }
   }
}