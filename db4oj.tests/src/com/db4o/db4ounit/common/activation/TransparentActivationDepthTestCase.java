package com.db4o.db4ounit.common.activation;

import com.db4o.activation.*;
import com.db4o.internal.activation.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentActivationDepthTestCase extends AbstractDb4oTestCase {
	
	public static final class NonTAAware {
	}
	
	public static final class TAAware implements Activatable {
		public void activate() {
		}

		public void bind(Activator activator) {
		}
	}
	
	protected void store() throws Exception {
		store(new TAAware());
		store(new NonTAAware());
	}
	
	public void testDescendingFromNonTAAwareToTAAware() {
		
		ActivationDepth depth = nonTAAwareDepth();
		
		ActivationDepth child = depth.descend(classMetadataFor(TAAware.class));
		Assert.isFalse(child.requiresActivation());
		
	}

	public void testDefaultActivationNonTAAware() {
		ActivationDepth depth = nonTAAwareDepth();
		Assert.isTrue(depth.requiresActivation());
		
		ActivationDepth child = depth.descend(classMetadataFor(NonTAAware.class));
		Assert.isTrue(child.requiresActivation());
	}

	private ActivationDepth nonTAAwareDepth() {
		return transparentActivationDepthFor(NonTAAware.class);
	}
	
	private ActivationDepth transparentActivationDepthFor(Class clazz) {
		return new TransparentActivationDepthProvider().activationDepthFor(classMetadataFor(clazz), ActivationMode.ACTIVATE);
	}

	public void testDefaultActivationTAAware() {
		ActivationDepth depth = TAAwareDepth();
		Assert.isFalse(depth.requiresActivation());
	}

	private ActivationDepth TAAwareDepth() {
		return transparentActivationDepthFor(TAAware.class);
	}
}
