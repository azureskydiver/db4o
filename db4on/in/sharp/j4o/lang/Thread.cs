/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang {

    public class Thread : Runnable {
    
    	public const int MIN_PRIORITY = 0;

        private Runnable target;

        private string name;

        private System.Threading.Thread thread;

        static int idGenerator = 1;

        public Thread() {
            target = this;
        }

        public Thread(Runnable target) {
            this.target = target;
        }

        public Thread(System.Threading.Thread thread) {
            this.thread = thread;
        }

        public static Thread currentThread() {
            return new Thread(System.Threading.Thread.CurrentThread);
        }

        public ClassLoader getContextClassLoader() {
            return null;
        }

        public virtual void run() {
        }

        public void setName(string name) {
            this.name = name;
            if(thread != null && name != null) {
                try {
                    com.db4o.Compat.threadSetName(thread, name);
                } catch(Exception e) {
                    // do nothing
                }
            }
        }

        public void setPriority(int priority) {
            // TODO: how ?
        }

        public void setPriority(System.Threading.ThreadPriority priority) {
            thread.Priority = priority;
        }

        public static void sleep(long milliseconds) {
            System.Threading.Thread.Sleep((int)milliseconds);
        }

        public void start() {
            thread = new System.Threading.Thread(new System.Threading.ThreadStart(target.run));
            if(name != null) {
                setName(name);
            }
            thread.Start();
        }

    }
}
