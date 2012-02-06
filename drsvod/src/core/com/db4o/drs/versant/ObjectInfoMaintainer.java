/* Copyright (C) 2004 - 2011  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectInfo.*;
import com.db4o.drs.versant.timestamp.*;
import com.versant.odbms.*;
import com.versant.odbms.query.*;
import com.versant.odbms.query.Operator.UnaryOperator;

public class ObjectInfoMaintainer {

	private static final int OBJECTINO_STORED_THRESHOLD = Integer.MAX_VALUE / 2;
	
	private static final long OBJECT_VERSION_FOR_PREEXISTING = 1; 	// can't use 0 because we query for > 0

	private final VodCobraFacade _cobra;
	
	private final TimestampGenerator _timestampGenerator;

	public ObjectInfoMaintainer(VodCobraFacade cobra) {
		_cobra = cobra;
		_timestampGenerator = new TimestampGenerator(cobra);
		System.err.println("TODO: Replace TimestampGenerator with more efficient algorithm that reserves timestamps and doesn't call commit for every timestamp.");
	}

	public void ensureObjectInfosExist(Class<?> clazz) {
		DatastoreQuery query = new DatastoreQuery(_cobra.storedClassName(clazz.getName()));
		Expression expression = new Expression(
				new SubExpression(new Field("o_ts_timestamp")),
				UnaryOperator.LESS_THAN,
				new SubExpression(OBJECTINO_STORED_THRESHOLD));
		query.setExpression(expression);
		Object[] loids = _cobra.executeQuery(query);
		
		long classMetadataLoid = _cobra.classMetadata(clazz).loid();
		
		// FIXME: Group write all changes to improve performance.
		System.err.println("TODO: group write objects");
		
		for (Object loid : loids) {
			DatastoreLoid dsl = (DatastoreLoid) loid;
			long loidAsLong = dsl.value();
			ObjectInfo objectInfo = new ObjectInfo(
					_cobra.defaultSignatureLoid(), 
					classMetadataLoid, 
					loidAsLong, 
					_timestampGenerator.generate(), 
					OBJECT_VERSION_FOR_PREEXISTING, 
					Operations.CREATE.value);
			_cobra.store(objectInfo);
			
			System.err.println("Next step: set the o_ts_timestamp in ObjectInfo");
		}
		
		
		_cobra.commit();

	}

}
