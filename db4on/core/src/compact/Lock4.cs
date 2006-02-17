/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Threading;

namespace com.db4o.foundation
{
    internal class Lock4
    {
        private volatile Thread lockedByThread;

        private volatile Thread waitReleased;
        private volatile Thread closureReleased;

        AutoResetEvent waitEvent = new AutoResetEvent(false);
        AutoResetEvent closureEvent = new AutoResetEvent(false);

        public Object run(Closure4 closure4)
        {
            enterClosure();
            Object ret;
            try
            {
                ret = closure4.run();
            }
            catch (Exception e)
            {
                awakeClosure();
                throw;
            }
            awakeClosure();
            return ret;
        }

        public void snooze(long l)
        {
            awakeClosure();
            waitWait();
            enterClosure();
        }

        public void awake()
        {
            awakeWait();
        }

        private void awakeWait()
        {
            lock (this)
            {
                waitReleased = Thread.CurrentThread;
                waitEvent.Set();
                Thread.Sleep(0);
                if (waitReleased == Thread.CurrentThread)
                {
                    waitEvent.Reset();
                }
            }
        }

        private void awakeClosure()
        {
            lock (this)
            {
                removeLock();
                closureReleased = Thread.CurrentThread;
                closureEvent.Set();
                Thread.Sleep(0);
                if (closureReleased == Thread.CurrentThread)
                {
                    closureEvent.Reset();
                }
            }
        }

        private void waitWait()
        {
            waitEvent.WaitOne();
            waitReleased = Thread.CurrentThread;
        }

        private void waitClosure()
        {
            closureEvent.WaitOne();
            closureReleased = Thread.CurrentThread;
        }

        private void enterClosure()
        {
            while (lockedByThread != Thread.CurrentThread)
            {
                while (!setLock())
                {
                    waitClosure();
                }
            }
        }

        private bool setLock()
        {
            lock (this)
            {
                if (lockedByThread == null)
                {
                    lockedByThread = Thread.CurrentThread;
                    return true;
                }
                return false;
            }
        }

        private void removeLock()
        {
            lock (this)
            {
                if (lockedByThread == Thread.CurrentThread)
                {
                    lockedByThread = null;
                }
            }
        }
    }
}