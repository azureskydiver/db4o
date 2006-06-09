package com.db4o.test;

import com.db4o.*;

public class ActivationDepthZero {
	public void configure() {
		Db4o.configure().activationDepth(0);
	}
}
