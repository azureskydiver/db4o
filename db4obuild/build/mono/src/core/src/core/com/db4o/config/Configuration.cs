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
using com.db4o.io;
using com.db4o.messaging;
using com.db4o.reflect;
namespace com.db4o.config {

   public interface Configuration {
      
      void activationDepth(int i);
      
      void automaticShutDown(bool xbool);
      
      void blockSize(int i);
      
      void callbacks(bool xbool);
      
      void callConstructors(bool xbool);
      
      void classActivationDepthConfigurable(bool xbool);
      
      void detectSchemaChanges(bool xbool);
      
      void disableCommitRecovery();
      
      void discardFreeSpace(int i);
      
      void encrypt(bool xbool);
      
      void exceptionsOnNotStorable(bool xbool);
      
      void generateUUIDs(int i);
      
      void generateVersionNumbers(int i);
      
      MessageSender getMessageSender();
      
      void io(IoAdapter ioadapter);
      
      void markTransient(String xstring);
      
      void messageLevel(int i);
      
      void lockDatabaseFile(bool xbool);
      
      ObjectClass objectClass(Object obj);
      
      void password(String xstring);
      
      void readOnly(bool xbool);
      
      void reflectWith(IReflect ireflect);
      
      void refreshClasses();
      
      void reserveStorageSpace(long l);
      
      void setBlobPath(String xstring);
      
      void setClassLoader(ClassLoader classloader);
      
      void setMessageRecipient(MessageRecipient messagerecipient);
      
      void setOut(PrintStream printstream);
      
      void singleThreadedClient(bool xbool);
      
      void testConstructors(bool xbool);
      
      void timeoutClientSocket(int i);
      
      void timeoutServerSocket(int i);
      
      void timeoutPingClients(int i);
      
      void unicode(bool xbool);
      
      void updateDepth(int i);
      
      void weakReferences(bool xbool);
      
      void weakReferenceCollectionInterval(int i);
   }
}