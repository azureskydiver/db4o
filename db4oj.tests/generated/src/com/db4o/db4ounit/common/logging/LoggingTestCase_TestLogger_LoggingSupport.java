package com.db4o.db4ounit.common.logging;

import com.db4o.internal.logging.*;

/* DO NOT EDIT THIS FILE */
/* AUTOMATIC CODE GENERATION */

public class LoggingTestCase_TestLogger_LoggingSupport {

	public static class LoggingTestCase_TestLoggerAdapter implements LoggingTestCase.TestLogger {
	
		public void msg() {}
	
	}
	
	public static class LoggingTestCase_TestLoggerLogger implements LoggingTestCase.TestLogger {
	
		private LoggingWrapper<LoggingTestCase.TestLogger> wrapper;
		private Level level;
	
		public LoggingTestCase_TestLoggerLogger(LoggingWrapper<LoggingTestCase.TestLogger> wrapper, Level level) {
			this.wrapper = wrapper;
			this.level = level;
		} 
		
		private void log(String methodName, Object[] args) {
			wrapper.log(level, methodName, args);
		}
	
		public void msg() {
			LoggingTestCase.TestLogger forward = wrapper.forward();
			if (forward != null) {
				wrapper.pushCurrentLevel(level);
				try {
					forward.msg();
				} catch (Throwable _exceptionThrown) {
					wrapper.exceptionCaughtInForward("msg", new Object[]{}, _exceptionThrown);
				} finally {
					wrapper.popCurrentLevel();
				}
				return;
			}
			log("msg", new Object[]{});
		}
	
	}
}

