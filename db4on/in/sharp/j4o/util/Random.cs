/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.util {
	public class Random {
		public Random() {
		}
		
		public long nextLong() {
			return j4o.lang.JavaSystem.currentTimeMillis();
		}
	}
}