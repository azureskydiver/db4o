/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Threading;

namespace com.db4o.test.cs
{
	/// <summary>
	/// Summary description for CsAutoResetEvent.
	/// </summary>
	public class CsAutoResetEvent
	{
        static AutoResetEvent are = new AutoResetEvent(false);

        private int id;

        public CsAutoResetEvent(int id){
            this.id = id;
        }

        public static void Main(String[] args) {
            new Thread(new ThreadStart(new CsAutoResetEvent(1).run)).Start();
            new Thread(new ThreadStart(new CsAutoResetEvent(2).run)).Start();
        }

        public void run(){
            if(id == 1){
                Console.WriteLine("1 arrived");
                Thread.Sleep(500);
                Console.WriteLine("1 calls Set");
                are.Set();
            }else{
                Console.WriteLine("2 beginning to wait");
                are.WaitOne();
                Console.WriteLine("2 wait complete");
            }
        }
	}
}
