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
using com.db4o;
using com.db4o.messaging;
namespace com.db4o.test {

   public class Messaging : MessageRecipient {
      
      public Messaging() : base() {
      }
      static internal String MSG = "hibabe";
      private Object lastMessage;
      internal String messageString;
      
      public void test() {
         if (Test.isClientServer()) {
            Test.server().ext().configure().setMessageRecipient(this);
            MessageSender sender1 = Test.objectContainer().configure().getMessageSender();
            this.messageString = MSG;
            sender1.send(this);
            Thread.sleep(100);
            Test.ensure(lastMessage is Messaging);
            Messaging received1 = (Messaging)lastMessage;
            Test.ensure(received1.messageString.Equals(MSG));
         }
      }
      
      public void processMessage(ObjectContainer con, Object message) {
         lastMessage = message;
      }
   }
}