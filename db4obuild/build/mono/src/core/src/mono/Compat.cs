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
using System.IO;
using System.Diagnostics;
using System.Reflection;
using System.Threading;

namespace com.db4o {

	/// <exlude />
    public class Compat {

        public static void addShutDownHook(EventHandler handler) {
            AppDomain.CurrentDomain.ProcessExit += handler;
            AppDomain.CurrentDomain.DomainUnload += handler;
        }

        public static Stream buffer(Stream stream){
            return new BufferedStream(stream);
        }

        public static Stream buffer(Stream stream, int bufferSize){
            return new BufferedStream(stream, bufferSize);
        }

        public static bool compact(){
            return false;
        }

        public static long doubleToLong(double a_double) {
            return BitConverter.DoubleToInt64Bits(a_double);
        }

        public static int getArrayRank(Type type) {
            return type.GetArrayRank();
        }

        public static bool isDirectory(string path) {
            return (File.GetAttributes(path) &  FileAttributes.Directory) != 0;
        }

        public static void lockFileStream(FileStream fs) {
            // Problems on earlier Mono versions.
            // TODO: Check, if the functionality works.
            // If yes, the normal .NET version will work on 
            // Mono also.
            // fs.Lock(0, 1);
        }

        public static double longToDouble(long a_long) {
            return BitConverter.Int64BitsToDouble(a_long);
        }
    
        public static void notify(object obj) {
            Monitor.Pulse(obj);
        }

        public static void notifyAll(object obj){
            Monitor.PulseAll(obj);
        }

        public static string stackTrace() {
            return new StackTrace().ToString();
        }

        public static string threadGetName(Thread thread) {
            return thread.Name;
        }

        public static void threadSetName(Thread thread, string name) {
            thread.Name = name;
        }

        public static void wait(object obj, long timeout) {
            Monitor.Wait(obj, (int)timeout);
        }
    }
}
