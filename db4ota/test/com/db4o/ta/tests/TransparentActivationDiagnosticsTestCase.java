package com.db4o.ta.tests;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.internal.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentActivationDiagnosticsTestCase extends AbstractDb4oTestCase {

	public static class SomeTAAwareData {
		public int _id;

		public SomeTAAwareData(int id) {
			_id = id;
		}
	}

	public static class SomeOtherTAAwareData implements Activatable {		
		public SomeTAAwareData _data;
		
		public void bind(ObjectContainer container) {
		}

		public SomeOtherTAAwareData(SomeTAAwareData data) {
			_data = data;
		}
	}
	
	public static class NotTAAwareData {
		public SomeTAAwareData _data;

		public NotTAAwareData(SomeTAAwareData data) {
			_data = data;
		}
	}
	
	private DiagnosticsRegistered _registered = new DiagnosticsRegistered();

	private static class DiagnosticsRegistered {
		public int _registeredCount = 0;
	}
	
	protected void configure(Configuration config) {
		config.add(new TransparentActivationSupport());
		config.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic diagnostic) {
				NotTransparentActivationEnabled taDiagnostic=(NotTransparentActivationEnabled)diagnostic;
				Assert.areEqual(NotTAAwareData.class.getName(), ((ClassMetadata)taDiagnostic.reason()).getName());
				_registered._registeredCount++;
			}
		});
	}

	public void testTADiagnostics() {
		store(new SomeTAAwareData(1));
		Assert.areEqual(0, _registered._registeredCount);
		store(new SomeOtherTAAwareData(new SomeTAAwareData(2)));
		Assert.areEqual(0, _registered._registeredCount);
		store(new NotTAAwareData(new SomeTAAwareData(3)));
		Assert.areEqual(1, _registered._registeredCount);
	}
	
	public static void main(String[] args) {
		new TransparentActivationDiagnosticsTestCase().runSolo();
	}
}
