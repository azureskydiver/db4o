/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import com.db4o.ObjectContainer;
import java.util.*;

/**
 * @exclude
 */
public class TMap implements ObjectTranslator
{
	public Object onStore(ObjectContainer con, Object object){
		Map map = (Map)object;
		Entry[] entries = new Entry[map.size()];
		Iterator it = map.keySet().iterator();
		int i = 0;
		while(it.hasNext()){
			entries[i] = new Entry();
			entries[i].key = it.next();
			entries[i].value = map.get(entries[i].key);
			i++;
		}
		return entries;
	}

	public void onActivate(ObjectContainer con, Object object, Object members){
		Map map = (Map)object;
		map.clear();
		if(members != null){
			Entry[] entries = (Entry[]) members;
			for(int i = 0; i < entries.length; i++){
				if(entries[i].key != null && entries[i].value != null){
					map.put(entries[i].key,entries[i].value);
				}
			}
		}
	}

	public Class storedClass(){
		return Entry[].class;
	}
}
