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
