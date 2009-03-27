/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.fixtures.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ListSpec<L extends ActivatableList<CollectionElement>> implements Labeled {

	private static String[] NAMES = new String[] {"one", "two", "three"};
	
	private final Closure4<L> _activatableListFactory;
	private final Class<? super L> _listClazz;
	
	public ListSpec(Class<? super L> listClazz, Closure4<L> activatableListFactory) {
		_activatableListFactory = activatableListFactory;
		_listClazz = listClazz;
	}

	public L newActivatableList() {
		L list = createActivatableList();
		for (CollectionElement element: newPlainList()) {
			list.add(element);
		}
		return list;
	}
	
	public List<CollectionElement> newPlainList(){
		List elements = new ArrayList();
		for (String name  : NAMES) {
			elements.add(new Element(name));
		}
		for (String name  : NAMES) {
			elements.add(new ActivatableElement(name));
		}
		return elements;
	}
	
	public static String firstName() {
		return NAMES[0];
	}
	
	private L createActivatableList() {
		return _activatableListFactory.run();
	}

	public String label() {
		return ReflectPlatform.simpleName(_listClazz);
	}
}
