/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang 
{
	public class Thread : Runnable 
	{
		public const int MIN_PRIORITY = 0;

		private Runnable _target;

		private string _name;

		private System.Threading.Thread _thread;
        
		private bool _isDaemon;

		static int idGenerator = 1;

		public Thread() 
		{
			_target = this;
		}

		public Thread(Runnable target) 
		{
			this._target = target;
		}

		public Thread(System.Threading.Thread thread) 
		{
			this._thread = thread;
		}

		public static Thread currentThread() 
		{
			return new Thread(System.Threading.Thread.CurrentThread);
		}

		public ClassLoader getContextClassLoader() 
		{
			return null;
		}

		public virtual void run() 
		{
		}

		public void setName(string name) 
		{
			this._name = name;
			if(_thread != null && name != null) 
			{
				try 
				{
					com.db4o.Compat.threadSetName(_thread, name);
				} 
				catch(Exception e) 
				{
					// do nothing
				}
			}
		}

		public void setPriority(int priority) 
		{
			// TODO: how ?
		}

		public void setPriority(System.Threading.ThreadPriority priority) 
		{
			_thread.Priority = priority;
		}

		public static void sleep(long milliseconds) 
		{
			System.Threading.Thread.Sleep((int)milliseconds);
		}

		public void start() 
		{
			_thread = new System.Threading.Thread(new System.Threading.ThreadStart(_run));
#if !CF_1_0
			_thread.IsBackground = _isDaemon;
#endif
			if (_name != null) 
			{
				setName(_name);
			}
			_thread.Start();
		}
		
		public void setDaemon(bool isDaemon)
		{
			_isDaemon = isDaemon;
		}
		
		// HACK: for PascalCase conversion purposes
		private void _run() 
		{
			_target.run();
		}

	}
}
