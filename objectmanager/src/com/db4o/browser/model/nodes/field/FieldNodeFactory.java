/*
 * This file is part of com.db4o.browser.
 *
 * com.db4o.browser is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * com.db4o.browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.swtworkbench.ed; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.db4o.browser.model.nodes.field;

import java.util.Date;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;


/**
 * Class FieldNodeFactory.  Construct FieldNodes from the object instances.
 * 
 * @author djo
 */
public class FieldNodeFactory {
    
    private static Class[] boxedPrimitiveTypes = {
    		Integer.class,
            Float.class,
            Double.class,
            Long.class,
            Boolean.class,
            Character.class,
            String.class,
            Date.class
    };

    /**
     * Test to see if clazz is in classArray
     * 
     * @param clazz The class to test
     * @param classArray A bunch of classes
     * @return if clazz is in classArray
     */
    private static boolean typeIn(Class clazz, Class[] classArray) {
        for (int i = 0; i < classArray.length; i++) {
            if (classArray[i].isAssignableFrom(clazz))
                return true;
        }
        return false;
    }
    
	/**
     * Construct a FieldNode.  FIXME: Eventually, each class should register
     * itself with the factory rather than hard-coding all of these tests.
	 * @param field
	 * @param instance
	 * @param database 
     * 
	 * @return
	 */
	public static IModelNode construct(ReflectField field, Object instance, Database database) {
        /*
         * There are 4 use-cases here:
         * 
         * 0) The field is a primitive type: no children
         * 1) A field is a List: can use iterator()
         * 2) A field is a Map: need to use keySet().iterator()
         * 3) A field is an object, in which case it may have fields
         */
        IModelNode result;
        
        ReflectClass fieldType = field.getType();
        if (fieldType.isPrimitive()) {// || typeIn(fieldType, boxedPrimitiveTypes)) {
            return new PrimitiveFieldNode(field, instance, database);
        }
        
        if (fieldType.isArray()) {
            // FIXME: StoredArrayFieldNode needs implemented
            return new ArrayFieldNode(field, instance, database);
        }
        
        // If keySet() and get() are present, use them
        result = MapFieldNode.tryToCreate(field, instance, database);
        if (result != null) return result;
        
        // Otherwise treat all iterable things as lists
        result = IterableFieldNode.tryToCreate(field, instance, database);
        if (result != null) return result;
        
        // Otherwise it must be a plain old object
		return new FieldNode(field, instance, database);
	}

}


