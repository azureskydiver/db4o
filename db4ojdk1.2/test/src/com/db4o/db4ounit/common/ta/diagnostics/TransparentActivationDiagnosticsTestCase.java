/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta.diagnostics;

import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.diagnostic.*;
import com.db4o.internal.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

public class TransparentActivationDiagnosticsTestCase extends TransparentActivationTestCaseBase implements OptOutCS, OptOutDefragSolo {

	public static class SomeTAAwareData {
		public int _id;

		public SomeTAAwareData(int id) {
			_id = id;
		}
	}

	public static class SomeOtherTAAwareData implements Activatable {		
		public SomeTAAwareData _data;
		
		public void bind(Activator activator) {
		}
		
		public void activate() {			
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
	
	private static class DiagnosticsRegistered {
		public int _registeredCount = 0;
	}
	
	private final DiagnosticsRegistered _registered = new DiagnosticsRegistered();
	private final DiagnosticListener _checker;
	
	public TransparentActivationDiagnosticsTestCase() {
		 _checker = new DiagnosticListener() {
			public void onDiagnostic(Diagnostic diagnostic) {
				if (!(diagnostic instanceof NotTransparentActivationEnabled)) {
					return;
				}
				NotTransparentActivationEnabled taDiagnostic=(NotTransparentActivationEnabled)diagnostic;
				Assert.areEqual(CrossPlatformServices.fullyQualifiedName(NotTAAwareData.class), ((ClassMetadata)taDiagnostic.reason()).getName());
				_registered._registeredCount++;
			}
		};
	}
	
	protected void configure(Configuration config) {
		super.configure(config);
		config.diagnostic().addListener(_checker);
	}
	
	protected void db4oTearDownBeforeClean() throws Exception {
		db().ext().configure().diagnostic().removeAllListeners();
		super.db4oTearDownBeforeClean();
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
		new TransparentActivationDiagnosticsTestCase().runAll();
	}
}
