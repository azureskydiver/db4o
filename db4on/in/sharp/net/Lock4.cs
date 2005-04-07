/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Threading;
namespace com.db4o {

    internal class Lock4 {
    
        public void awake() {
            Monitor.Pulse(this);
        }

        public Object run(Closure4 closure) {
            lock (this) {
                return closure.run();
            }
        }
    
        public void snooze(long timeout) {
            Monitor.Wait(this, (int)timeout);
        }
    }
}