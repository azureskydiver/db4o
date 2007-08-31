/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.tests;

import com.db4o.activation.Activator;
import com.db4o.config.Configuration;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.internal.ClassMetadata;
import com.db4o.ta.Activatable;
import com.db4o.ta.NotTransparentActivationEnabled;
import com.db4o.ta.TransparentActivationSupport;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.util.CrossPlatformServices;

public class TransparentActivationDiagnosticsTestCase extends AbstractDb4oTestCase {

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
		config.add(new TransparentActivationSupport());
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
		new TransparentActivationDiagnosticsTestCase().runSolo();
	}
}
