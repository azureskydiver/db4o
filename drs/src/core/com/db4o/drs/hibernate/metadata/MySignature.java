/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.drs.hibernate.metadata;

import com.db4o.inside.*;

/**
 * Serves as the identifier of a HibernateReplicationProvider. 
 * 
 * @author Albert Kwan
 *
 * @version 1.2
 * @since dRS 1.1
 */
public class MySignature extends ProviderSignature {
	public static MySignature generateSignature() {
		MySignature out = new MySignature();
		out.setSignature(Unobfuscated.generateSignature());
		return out;
	}

	public MySignature() {
		super();
	}
}
