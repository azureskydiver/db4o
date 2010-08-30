package com.db4o.drs.versant;

import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;

public class VodReplicationReference extends ReplicationReferenceImpl {

	private final boolean isNew;

	public VodReplicationReference(Object obj, DrsUUID uuid, long version) {
		this(obj, uuid, version, false);
	}

	public VodReplicationReference(Object obj, DrsUUID uuid, long version, boolean isNew) {
		super(obj, uuid, version);
		this.isNew = isNew;
	}
	
	public boolean isNew() {
		return isNew;
	}

}
