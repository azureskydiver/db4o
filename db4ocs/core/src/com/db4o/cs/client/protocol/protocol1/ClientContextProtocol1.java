package com.db4o.cs.client.protocol.protocol1;

import com.db4o.cs.client.ClientContext;
import com.db4o.cs.common.ClassMetaData;

import java.util.HashMap;
import java.util.Map;

/**
 * User: treeder
 * Date: Nov 29, 2006
 * Time: 12:34:52 PM
 */
public class ClientContextProtocol1 implements ClientContext {
	Map<String, ClassMetaData> classMetaDataMap = new HashMap();
	private Map idMap;

	public static final Long UNSAVED_ID = new Long(0);

	public Map<String, ClassMetaData> getClassMetaDataMap() {
		return classMetaDataMap;
	}

	public void setIdMap(Map idMap) {
		this.idMap = idMap;
	}

	public Long getIdForObject(Object o) {
		Long id = (Long) idMap.get(o.hashCode());
		if (id == null) {
			id = UNSAVED_ID;
		}
		return id;
	}

	public ClassMetaData getClassMetaData(String className) {
		return classMetaDataMap.get(className);
	}

	public void setClassMetaData(String className, ClassMetaData classMetaData) {
		classMetaDataMap.put(className, classMetaData);
	}
}
