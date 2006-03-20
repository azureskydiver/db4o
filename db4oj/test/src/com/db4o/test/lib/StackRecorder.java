package com.db4o.test.lib;

import com.db4o.foundation.*;

/**
 * simple annotated stack trace for debugging
 * 
 * @exclude
 */
public class StackRecorder {
	// exclude addCase()/record() + newTrace()
	private static final int EXCLUDEDEPTH = 2;
	private static StackTrace _curTrace;
	private static Collection4 _traces=new Collection4();
	
	public static void addCase(String caseInfo) {
		_curTrace=newTrace(caseInfo);
	}

	public static void record() {
		StackTrace trace=newTrace(null);
		if(!_traces.contains(trace)) {
			_traces.add(trace);
		}
	}
	
	public static void logAll() {
		Iterator4 iter=_traces.strictIterator();
		while(iter.hasNext()) {
			System.out.println(iter.next());
			if(iter.hasNext()) {
				System.out.println("---");
			}
		}
	}

	private static StackTrace newTrace(String caseInfo) {
		return new StackTrace(EXCLUDEDEPTH,caseInfo,_curTrace);
	}
	
	public static void main(String[] args) {
		for(int i=0;i<2;i++) {
			for(int j=0;j<2;j++) {
				StackRecorder.addCase("main"+i);
				foo();
			}
		}
		for(int i=0;i<2;i++) {
			for(int j=0;j<2;j++) {
				StackRecorder.addCase("mainX"+i);
				foo();
			}
		}
		StackRecorder.logAll();
	}
	
	public static void foo() {
		for(int i=0;i<2;i++) {
			for(int j=0;j<2;j++) {
				StackRecorder.addCase("foo"+i);
				bar();
			}
		}
	}
	
	public static void bar() {
		for(int i=0;i<2;i++) {
			StackRecorder.record();
		}
	}
}
