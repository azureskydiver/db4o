/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.util {

    public class Date {

		public static long toJavaMilliseconds(DateTime dateTimeNet)
		{
			return dateTimeNet.Ticks / RATIO - DIFFERENCE_IN_TICKS;
		}

        private static long DIFFERENCE_IN_TICKS = 62135604000000;
        private static long RATIO = 10000;

        private long javaMilliSeconds;

        public Date():this(DateTime.Now) {
        }

        public Date(long javaMilliSeconds) {
            this.javaMilliSeconds = javaMilliSeconds;
        }

        public Date(DateTime dateTimeNet) {
            javaMilliSeconds = toJavaMilliseconds(dateTimeNet);
        }

    	public long getJavaMilliseconds() {
            return javaMilliSeconds;
        }

        public long getTicks() {
            return (javaMilliSeconds + DIFFERENCE_IN_TICKS) * RATIO;
        }

        public long getTime() {
            return getJavaMilliseconds();
        }

        public void setTime(long javaMilliSeconds) {
            this.javaMilliSeconds = javaMilliSeconds;
        }
    }
}
