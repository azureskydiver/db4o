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
