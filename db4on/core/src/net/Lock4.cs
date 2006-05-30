﻿/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Threading;
namespace com.db4o.foundation {

    internal class Lock4 {
    
        public void Awake() {
            Monitor.Pulse(this);
        }

        public Object Run(Closure4 closure) {
            lock (this) {
                return closure.Run();
            }
        }
    
        public void Snooze(long timeout) {
            Monitor.Wait(this, (int)timeout);
        }
    }
}