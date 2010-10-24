/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.foundation;

import java.util.*;

public class Signatures {
	
	private final Map<Integer, Signature> _byId = new HashMap<Integer, Signature>();
	
	private final Map<Signature, Integer> _idBySignature = new HashMap<Signature, Integer>();
	
	private final Map<Signature, Long> _loidBySignature = new HashMap<Signature, Long>();

	private final Map<Long, Signature> _signatureByLoid = new HashMap<Long, Signature>();

	public Signature signatureForDatabaseId(int databaseId) {
		return _byId.get(databaseId);
	}
	
	public int idFor(Signature signature) {
		Integer id = _idBySignature.get(signature);
		if(id == null){
			return 0;
		}
		return id;
	}

	public void add(int databaseId, Signature signature, long signatureLoid) {
		_byId.put(databaseId, signature);
		_idBySignature.put(signature, databaseId);
		_loidBySignature.put(signature, signatureLoid);
		_signatureByLoid.put(signatureLoid, signature);
	}

	public int idFor(DrsUUID uuid) {
		return idFor(new Signature(uuid.getSignaturePart()));
	}
	
	public Long loidFor(Signature signature){
		return _loidBySignature.get(signature);
	}
	
	public Signature signatureForLoid(long signatureLoid){
		return _signatureByLoid.get(signatureLoid);
	}

}
