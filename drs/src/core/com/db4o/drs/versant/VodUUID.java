/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.drs.foundation.*;
import com.db4o.foundation.*;

public class VodUUID implements DrsUUID {
	
	private final Signature _signature;
	
	private final short _databaseId;
	
	private final int _objectId1;
	
	private final long _objectId2;
	
	public VodUUID(Signature signature, short dbId, int id1, long id2){
		_signature = signature;
		_databaseId = dbId;
		_objectId1 = id1;
		_objectId2 = id2;
	}

	public long getLongPart() {
		return TimeStampIdGenerator.convert48BitIdTo64BitId(_objectId1 << 32  | _objectId2);
	}

	public byte[] getSignaturePart() {
		return _signature.bytes;
	}

}
