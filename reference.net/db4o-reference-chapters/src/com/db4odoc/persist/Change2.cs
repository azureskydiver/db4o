/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.Threading;

namespace Db4objects.Db4odoc.Persist
{
	public class Change2
	{
		protected Thread _thread;
		protected bool   _running;
		Car _car;
		
		public Change2()
		{
		}

		public void Init(Car car)
		{
			this._car = car;
			_running = true;
			ThreadStart threadStart = new ThreadStart(Run);
			_thread = new Thread(threadStart);
			_thread.Start();
		}

		private void Run()
		{
			while(_running)
			{
				_car.Temperature = GetConditionerTemperature();
				Thread.Sleep(10);
			}
		}
		
		private int GetConditionerTemperature()
		{
			return 17;
		}

		public void Stop()
		{ 
			_running = false;
		}
	}
}