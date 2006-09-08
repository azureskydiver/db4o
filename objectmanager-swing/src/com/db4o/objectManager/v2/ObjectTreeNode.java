package com.db4o.objectManager.v2;

import com.db4o.reflect.ReflectField;

import java.util.Collection;

/**
 * User: treeder
 * Date: Sep 8, 2006
 * Time: 11:32:24 AM
 */
public class ObjectTreeNode {
    private ReflectField field;
    private Object ob;

    public ObjectTreeNode(ReflectField field, Object ob) {

        this.field = field;
        this.ob = ob;
    }

    public ObjectTreeNode(Object o) {
        this.ob = o;
    }

    public Object getObject() {
        return ob;
    }

    public String toString() {
        String ret;
        if (field != null) {
            ret = field.getName() + ": ";
        } else ret = "";
        if(ob == null) ret += ob;
        else if (ob.getClass().isArray()) {
            Object[] array = (Object[]) ob;
            ret += "Array[" + array.length + "]";
        } else if (ob instanceof Collection) {
            Collection collection = (Collection) ob;
            ret += "Collection[" + collection.size() + "]";
        } else {
            ret += ob;
        }
        return ret;

    }
}
