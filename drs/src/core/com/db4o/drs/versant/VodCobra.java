/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.versant.core.vds.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;

public class VodCobra {
	
	private final VodDatabase _vod;
	
	private DatastoreManager _dm;


	public VodCobra(VodDatabase vod) {
		_vod = vod;
		_dm = _vod.createDatastoreManager();
	}

	public Object fieldValue(String loidAsString, String fieldName) {
		
		DatastoreLoid datastoreLoid = new DatastoreLoid(DatastoreLoid.asValue(loidAsString));
		_dm.beginTransaction();
		try{
			DatastoreObject datastoreObject = new DatastoreObject(datastoreLoid);
			_dm.readObject(datastoreObject, DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
			DatastoreSchemaField[] fields = datastoreObject.getSchemaClass().getFields();
			for (DatastoreSchemaField field : fields) {
				if(fieldName.equals(field.getName())){
					return datastoreObject.readObject(field);
				}
			}
			throw new IllegalArgumentException("Field " + fieldName + " not found.");
		} finally{
			_dm.rollbackTransaction();
		}
		
	}
	
	private DatastoreObject datastoreObject(long loid) {
		DatastoreLoid datastoreLoid = new DatastoreLoid(loid);
		DatastoreObject[] loidsAsDSO = _dm.getLoidsAsDSO(new Object[] { datastoreLoid });
		_dm.groupReadObjects(loidsAsDSO, DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
		return loidsAsDSO[0];
	}

	
	

	public void close() {
		_dm.close();
	}

	public VodId idFor(long loid) {
		DatastoreLoid datastoreLoid = new DatastoreLoid(loid);
		
		DatastoreObject datastoreObject = datastoreObject(loid);
		// TODO: Create correct timestamp here
		
		return new VodId(datastoreLoid.getDatabaseId(), datastoreLoid.getObjectId1(), datastoreLoid.getObjectId2(), datastoreObject.getTimestamp());
	}
	

}
