package decaf.core;

import java.util.*;

public class DecafConfiguration {
	
	private final Map<String, String> _mapping;

	public DecafConfiguration() {
		this._mapping = Collections.emptyMap();
	}

	public DecafConfiguration(Map<String, String> mapping) {
		this._mapping = mapping;
	}
	
	public Iterable<String> mappedTypeKeys() {
		return _mapping.keySet();
	}

	public Iterable<String> mappedTypeValues() {
		return _mapping.values();
	}

	public String typeNameMapping(String typeName) {
		return _mapping.get(typeName);
	}

	public String reverseTypeNameMapping(String typeName) {
		for (Map.Entry<String,String> entry : _mapping.entrySet()) {
			if(entry.getValue().equals(typeName)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static DecafConfiguration forJDK11() {
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put(Map.class.getName(), "com.db4o.foundation.Map4");
		mapping.put(HashMap.class.getName(), "com.db4o.foundation.Hashtable4");
		mapping.put(List.class.getName(), "com.db4o.foundation.Sequence4");
		mapping.put(ArrayList.class.getName(), "com.db4o.foundation.Collection4");
		mapping.put(Collections.class.getName(), "com.db4o.foundation.Collections4");
		return new DecafConfiguration(mapping);
	}

	public static DecafConfiguration forJDK12() {
		Map<String, String> mapping = Collections.emptyMap();
		return new DecafConfiguration(mapping);
	}

}
