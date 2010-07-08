/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.foundation;

import java.util.*;

import com.db4o.drs.versant.metadata.*;

public class Signatures {
	
	private final Map<Integer, Signature> _byId = new HashMap<Integer, Signature>();
	
	private final Map<Signature, Integer> _bySignature = new HashMap<Signature, Integer>();

	public Signature signatureFor(int databaseId) {
		return _byId.get(databaseId);
	}
	
	public int idFor(Signature signature) {
		Integer id = _bySignature.get(signature);
		if(id == null){
			return 0;
		}
		return id;
	}

	public void add(int databaseId, Signature signature) {
		_byId.put(databaseId, signature);
		_bySignature.put(signature, databaseId);
	}

	public long idFor(DrsUUID uuid) {
		return idFor(new Signature(uuid.getSignaturePart()));
	}

	public void add(DatabaseSignature databaseSignature) {
		add( databaseSignature.databaseId(),new Signature(databaseSignature.signature()));
	}

}
