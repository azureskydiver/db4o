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
package com.db4o.browser.gui;

import java.lang.reflect.Field;

/**
 * Class FieldNodeFactory.
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
            String.class
    };

	/**
     * Construct a FieldNode
     * 
	 * @param field
	 * @param _instance
	 * @return
	 */
	public static ITreeNode construct(Field field, Object _instance) {
        /*
         * There are 4 use-cases here:
         * 
         * 0) The field is a primitive type: no children
         * 1) A field is a List: can use iterator()
         * 2) A field is a Map: need to use keySet().iterator()
         * 3) A field is an object, in which case it may have fields
         */
        ITreeNode result;
        Class fieldType = field.getType();
        
        if (fieldType.isPrimitive() || typeIn(fieldType, boxedPrimitiveTypes)) {
            return new PrimitiveFieldNode(field, _instance);
        }
        
        result = IterableFieldNode.tryToCreate(field, _instance);
        if (result != null) return result;
        
 
        
		return new FieldNode(field, _instance);
	}

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

}
