package decaf.builder;

import java.util.*;

public class DecafConfiguration {
	
	private final Map<String, String> mapping;

	public DecafConfiguration() {
		this(defaultMapping());
	}

	public DecafConfiguration(Map<String, String> mapping) {
		this.mapping = mapping;
	}
	
	public String typeNameMapping(String typeName) {
		return mapping.get(typeName);
	}
	
	private static Map<String, String> defaultMapping() {
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put(Map.class.getName(), "com.db4o.foundation.Map4");
		mapping.put(HashMap.class.getName(), "com.db4o.foundation.Hashtable4");
		mapping.put(List.class.getName(), "com.db4o.foundation.Sequence4");
		mapping.put(ArrayList.class.getName(), "com.db4o.foundation.Collection4");
		mapping.put(Collections.class.getName(), "com.db4o.foundation.Collections4");
		return mapping;
	}

}
