/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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