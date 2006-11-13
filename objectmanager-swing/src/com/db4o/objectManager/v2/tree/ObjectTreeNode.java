package com.db4o.objectManager.v2.tree;

import com.db4o.reflect.ReflectField;

import java.util.Collection;
import java.util.Map;

/**
 * User: treeder
 * Date: Sep 8, 2006
 * Time: 11:32:24 AM
 */
public class ObjectTreeNode {
	private ObjectTreeNode parentNode;
	private ReflectField field;
	private Object ob;

	public ObjectTreeNode(ObjectTreeNode parentNode, ReflectField field, Object ob) {
		this.parentNode = parentNode;
		this.field = field;
		this.ob = ob;
	}

	public ObjectTreeNode(ObjectTreeNode parentNode, Object o) {
		this.parentNode = parentNode;
		this.ob = o;
	}

	public Object getObject() {
		return ob;
	}

	public String toString() {
		String ret = "";
		try {
			if (field != null) {
				ret = field.getName() + ": ";
			} else ret = "";
			if (ob == null) ret += ob;
			else if (ob.getClass().isArray()) {
				/*
				wow, this is strange, this occurs on next line, that's why i surrounded with catch all
				Exception in thread "AWT-EventQueue-0" java.lang.ClassCastException: [B
				at com.db4o.objectManager.v2.tree.ObjectTreeNode.toString(ObjectTreeNode.java:40)
				 */
				Object[] array = (Object[]) ob;
				ret += "Array[" + array.length + "]";
			} else if (ob instanceof Collection) {
				Collection collection = (Collection) ob;
				ret += "Collection[" + collection.size() + "]";
			} else if (ob instanceof Map) {
				Map map = (Map) ob;
				ret += "Map[" + map.size() + "]";
			} else {
				ret += ob;
			}
		} catch (Exception e) {
			ret = ob.toString();
		}
		return ret;

	}

	public void setObject(Object object) {
		this.ob = object;
	}

	public ObjectTreeNode getParentNode() {
		return parentNode;
	}

	public ReflectField getField() {
		return field;
	}
}
