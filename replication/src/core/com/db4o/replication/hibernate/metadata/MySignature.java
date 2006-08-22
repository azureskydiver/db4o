/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.replication.hibernate.metadata;

import com.db4o.Unobfuscated;

/**
 * Serves as the identifier of a HibernateReplicationProvider. 
 * 
 * @see HibernateReplicationProvider
 * @author Albert Kwan
 *
 * @version 1.1
 * @since dRS 1.1
 */
public class MySignature extends ProviderSignature {
	public static MySignature generateSignature() {
		MySignature out = new MySignature();
		out.setBytes(Unobfuscated.generateSignature());
		out.setCreationTime(System.currentTimeMillis());
		return out;
	}

	public MySignature() {
		super();
	}
}
