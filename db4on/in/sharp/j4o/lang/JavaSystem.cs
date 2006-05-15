/* Copyright (C) 2004	db4objects Inc.	  http://www.db4o.com */

using System;
using System.Threading;
using j4o.io;
using System.Reflection;

namespace j4o.lang 
{
	public class JavaSystem 
	{
		public static PrintStream _out = new ConsoleWriter();
		public static PrintStream err = new ConsoleWriter();

		public static long currentTimeMillis() 
		{
			return j4o.util.Date.toJavaMilliseconds(DateTime.Now.ToUniversalTime());
		}

		public static int floatToIntBits(float value) 
		{
			return BitConverter.ToInt32(BitConverter.GetBytes(value), 0);
		}

		public static void gc() 
		{
			System.GC.Collect();
		}
		
		public static bool equalsIgnoreCase(string lhs, string rhs) 
		{
			return 0 == string.Compare(lhs, rhs, true);
		}

		public static string substring(String s, int startIndex)
		{
			return s.Substring(startIndex);
		}

		public static string substring(String s, int startIndex, int endIndex)
		{
			return s.Substring(startIndex, endIndex-startIndex);
		}

		public static char getCharAt(string str, int index) 
		{
			return str[index];
		}

		public static void getCharsForString(string str, int start, int end, char[] destination, int destinationStart) 
		{
			str.CopyTo(start, destination, 0, end-start);
		}
		
		public static string getStringValueOf(object value) 
		{
			return null == value
				? "null"
				: value.ToString();
		}

		public static String getProperty(String key) 
		{
#if CF_1_0 || CF_2_0
			return key.Equals("line.separator") ? "\n" : null;
#else
			return key.Equals("line.separator")
				? Environment.NewLine
				: null;
#endif
		}

		public static object getReferenceTarget(WeakReference reference) 
		{
			return reference.Target;
		}

		public static long getTimeForDate(DateTime dateTime) 
		{
			return j4o.util.Date.toJavaMilliseconds(dateTime);
		}

		public static int identityHashCode(object obj) 
		{
			return IdentityHashCodeProvider.identityHashCode(obj);
		}

		public static float intBitsToFloat(int value) 
		{
			return BitConverter.ToSingle(BitConverter.GetBytes(value), 0);
		}

		public static void wait(object obj, long timeout) 
		{
#if !CF_1_0 && !CF_2_0
			Monitor.Wait(obj, (int) timeout);
#endif
		}

		public static void notify(object obj) 
		{
#if !CF_1_0 && !CF_2_0
			Monitor.Pulse(obj);
#endif
		}

		public static void notifyAll(object obj) 
		{
#if !CF_1_0 && !CF_2_0
			Monitor.PulseAll(obj);
#endif
		}

		public static void printStackTrace(Exception exception) 
		{
			err.println(exception);
		}

		public static void printStackTrace(Exception exception, PrintStream printStream) 
		{
			printStream.println(exception);
		}

		public static void runFinalization() 
		{
			System.GC.WaitForPendingFinalizers();
		}

		public static void runFinalizersOnExit(bool flag) 
		{
			// do nothing
		}
	}
}