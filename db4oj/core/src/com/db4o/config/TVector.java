/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import com.db4o.ObjectContainer;
import java.util.*;

public class TVector implements ObjectTranslator
{
	public Object onStore(ObjectContainer con, Object object){
		Vector vt = (Vector)object;
		Object[] elements = new Object[vt.size()];
		Enumeration enum = vt.elements();
		int i = 0;
		while(enum.hasMoreElements()){
			elements[i++] = enum.nextElement();
		}
		return elements;
	}

	public void onActivate(ObjectContainer con, Object object, Object members){
		Vector vt = (Vector)object;
		vt.removeAllElements();
		if(members != null){
			Object[] elements = (Object[]) members;
			for(int i = 0; i < elements.length; i++){
				vt.addElement(elements[i]);
			}
		}
	}

	public Class storedClass(){
		return Object[].class;
	}
}
