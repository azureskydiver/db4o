package com.db4o.objectManager.v2.tree;

import com.db4o.ObjectContainer;
import com.db4o.objectManager.v2.tree.ObjectTreeNode;
import com.db4o.objectmanager.api.helpers.ReflectHelper2;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.spaceprogram.db4o.sql.ReflectHelper;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.EventListenerList;
import java.util.Collection;
import java.util.List;

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
        if (parentObject.getClass().isArray()) {
            Object[] array = (Object[]) parentObject;
            return new ObjectTreeNode(parentNode, null, array[index]);
        } else if (parentObject instanceof List) {
            // todo: how to handle other collections?
            List collection = (List) parentObject;
            return new ObjectTreeNode(parentNode, collection.get(index));
        }
        // todo: could try caching all this reflect information if performance is bad
        ReflectClass reflectClass = reflector.forObject(parentNode.getObject());
        //System.out.println("reflectclass: " + reflectClass);
        ReflectField[] fields = reflectClass.getDeclaredFields();
        fields[index].setAccessible();
        Object value = fields[index].get(parentNode.getObject());
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
        }
        ReflectClass reflectClass = reflector.forObject(parentNode.getObject());
        ReflectField[] fields = reflectClass.getDeclaredFields();
        return fields.length;
    }

    public boolean isLeaf(Object node) {
        if (node == null || ((ObjectTreeNode) node).getObject() == null) return true;
        return ReflectHelper2.isEditable(((ObjectTreeNode) node).getObject().getClass());
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        ObjectTreeNode aNode = (ObjectTreeNode) path.getLastPathComponent();
        System.out.println("new value: " + newValue + " " + newValue.getClass());
        aNode.setObject(newValue);
        ObjectTreeNode parent = aNode.getParentNode();
        Object p = parent.getObject();
        //ReflectClass rc = reflector.forObject(p);
        ReflectField rf = aNode.getField();
        rf.setAccessible();
        rf.set(p, newValue);
        //System.out.println("Set value on field: " + rf.getName() + " " + rf.getFieldType() + " new value: " + rf.get(o));
        //queryResultsPanel.addObjectToBatch(o);
        //nodeChanged(aNode);
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
        Class c = aNode.getObject().getClass();
        System.out.println("class editable: " + c);
        return ReflectHelper2.isEditable(c);
    }
}
