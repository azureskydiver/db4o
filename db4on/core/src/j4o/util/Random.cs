/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System.Collections;

namespace j4o.util {
	public class Random {
		public Random() {
		}
		
		public long NextLong() {
			return j4o.lang.JavaSystem.CurrentTimeMillis();
		}
	}
}