package com.db4o.reflect.db;

import java.util.*;

class DBObject {
	private DBClass type;
	private Map fields=new HashMap();
	
	public DBObject(DBClass type) {
		this.type=type;
	}
	
	public DBClass type() {
		return type;
	}
	
	public void set(String name,Object value) {
		fields.put(name,value);
	}
	
	public Object get(String name) {
		return fields.get(name);
	}
	
	public String toString() {
		StringBuffer buf=new StringBuffer("DBOBJ:")
			.append(type.getName())
			.append('{');
		int count=0;
		for (Iterator iter = fields.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			buf.append(entry.getKey())
				.append("=>")
				.append(entry.getValue());
			if(count>0) {
				buf.append(',');
			}
			count++;
		}
		buf.append('}');
		return buf.toString();
	}
}
