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
      
      public void Test() {
         if (Tester.IsClientServer()) {
            Tester.Server().Ext().Configure().ClientServer().SetMessageRecipient(this);
            MessageSender sender1 = Tester.ObjectContainer().Configure().ClientServer().GetMessageSender();
            this.messageString = MSG;
            sender1.Send(this);
            Thread.Sleep(100);
            Tester.Ensure(lastMessage is Messaging);
            Messaging received1 = (Messaging)lastMessage;
            Tester.Ensure(received1.messageString.Equals(MSG));
         }
      }
      
      public void ProcessMessage(ObjectContainer con, Object message) {
         lastMessage = message;
      }
   }
}