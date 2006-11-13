package com.db4o.objectManager.v2.tree;

import com.db4o.ObjectContainer;
import com.db4o.objectManager.v2.tree.ObjectTreeNode;
import com.db4o.objectmanager.api.helpers.ReflectHelper2;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.reflect.generic.GenericObject;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.spaceprogram.db4o.sql.ReflectHelper;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.EventListenerList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: treeder
 * Date: Sep 8, 2006
 * Time: 10:52:27 AM
 */
public class ObjectTreeModel implements TreeModel {
	private ObjectContainer objectContainer;
	private GenericReflector reflector;
	private ObjectTreeNode root;
	protected EventListenerList listenerList = new EventListenerList();


	public ObjectTreeModel(ObjectTreeNode top, ObjectContainer objectContainer) {
		this.root = top;
		this.objectContainer = objectContainer;
		this.reflector = objectContainer.ext().reflector();

	}

	public Object getRoot() {
		return root;
	}

	public Object getChild(Object parent, int index) {
		ObjectTreeNode parentNode = (ObjectTreeNode) parent;
		Object parentObject = parentNode.getObject();
		ReflectClass reflectClass = reflector.forObject(parentObject);
		if (parentObject.getClass().isArray()) {
			Object[] array = (Object[]) parentObject;
			return new ObjectTreeNode(parentNode, null, array[index]);
		} else if (reflector.isCollection(reflectClass)) {
			// reflector.isCollection returns true for Maps too I guess
			if (parentObject instanceof Map) {
				Map map = (Map) parentObject;
				Object[] arr = map.entrySet().toArray(); // todo: this will be poor performance, should do something else
				return new ObjectTreeNode(parentNode, new MapEntry((Map.Entry) arr[index]));
			} else {
				List collection = (List) parentObject;
				return new ObjectTreeNode(parentNode, collection.get(index));
			}
		} else if (parentObject instanceof MapEntry) {
			MapEntry entry = (MapEntry) parentObject;
			if (index == 0) {
				return new ObjectTreeNode(parentNode, entry.getEntry().getKey());
			} else {
				return new ObjectTreeNode(parentNode, entry.getEntry().getValue());
			}
		}
		// todo: could try caching all this reflect information if performance is bad
		//System.out.println("reflectclass: " + reflectClass);
		ReflectField[] fields = ReflectHelper.getDeclaredFieldsInHeirarchy(reflectClass);
		fields[index].setAccessible();
		Object value = fields[index].get(parentObject);
		//System.out.println("getChild parent:" + parentNode.getObject().getClass() + " index:" + index + " field:" + fields[index].getName() + " value:" + value);
		return new ObjectTreeNode(parentNode, fields[index], value);
	}

	public int getChildCount(Object parent) {
		ObjectTreeNode parentNode = (ObjectTreeNode) parent;
		if (parentNode.getObject().getClass().isArray()) {
			Object[] array = (Object[]) parentNode.getObject();
			return array.length;
		} else if (parentNode.getObject() instanceof Collection) {
			Collection collection = (Collection) parentNode.getObject();
			return collection.size();
		} else if (parentNode.getObject() instanceof Map) {
			Map map = (Map) parentNode.getObject();
			return map.size();
		} else if (parentNode.getObject() instanceof MapEntry) {
			return 2;
		}
		ReflectClass reflectClass = reflector.forObject(parentNode.getObject());
		ReflectField[] fields = ReflectHelper.getDeclaredFieldsInHeirarchy(reflectClass);
		return fields.length;
	}

	public boolean isLeaf(Object node) {
		if (node == null || ((ObjectTreeNode) node).getObject() == null) return true;
		Object nodeObject = ((ObjectTreeNode) node).getObject();
		if(nodeObject instanceof GenericObject){
			GenericObject go = (GenericObject) nodeObject;
			ReflectClass gclass = reflector.forObject(nodeObject);
			System.out.println("GENOB: " + go + " class:" + gclass.getName());
			if(gclass.getName().contains("System.DateTime")){
				// todo: move this into isEditable
				return true;
			}
		}
		return ReflectHelper2.isEditable(nodeObject.getClass());
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		ObjectTreeNode aNode = (ObjectTreeNode) path.getLastPathComponent();
		System.out.println("new value: " + newValue + " " + newValue.getClass());
		aNode.setObject(newValue);
		ObjectTreeNode parent = aNode.getParentNode();
		Object p = parent.getObject();
		ReflectField rf = aNode.getField();
		rf.setAccessible();
		rf.set(p, newValue);
		addToBatch(p);
	}

	private void addToBatch(Object o) {
		// similar to Object
		objectContainer.set(o);
		objectContainer.commit();
	}

	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	/**
	 * Only primitive fields (and quasi-primitives) will be editable.
	 *
	 * @param path
	 * @return
	 */
	public boolean isPathEditable(TreePath path) {
		ObjectTreeNode aNode = (ObjectTreeNode) path.getLastPathComponent();
		// todo: should check the expect class type if this is null so you can edit null values
		if(aNode.getObject() == null) return false;
		Class c = aNode.getObject().getClass();
		//System.out.println("class editable: " + c);
		return ReflectHelper2.isEditable(c);
	}
}
