package com.db4o.test.nativequery.diagnostics;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.diagnostic.NativeQueryNotOptimized;
import com.db4o.query.Predicate;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @sharpen.partial
 */
public class NativeQueryOptimizerDiagnosticsTestCase extends AbstractDb4oTestCase {
	private boolean _failed = false; 
	
	protected void configure(Configuration config) {
		config.objectClass(Subject.class).objectField("_name").indexed(true);
		
		config.diagnostic().addListener(
				new DiagnosticListener() {
					public void onDiagnostic(Diagnostic d) {
						if (d.getClass() == NativeQueryNotOptimized.class) {
							_failed = true;
						}
					}					
				});
	}
	
	protected void store() {
		db().set(new Subject("Test"));
		db().set(new Subject("Test2"));
	}
	
	public void testNativeQueryNotOptimized() {
		ObjectSet items = db().query(
								new Predicate(){
									public boolean match(final Subject subject) {
										return subject.complexName().startsWith("Test");
									}
								});
		
		Assert.isTrue(_failed);
	}
	
	private class Subject {
		private String _name;

		public Subject(String name) {
			_name = name;
		}
		
		public String complexName() {
			StringBuffer sb = new StringBuffer(_name);			
			for(int i =0; i < 10; i++) {
				sb.append(i);
			}
			
			return sb.toString();
		}
	}
}
